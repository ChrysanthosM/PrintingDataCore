package org.masouras.trace.annotation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.masouras.trace.domain.SpanInfo;
import org.masouras.trace.control.scheduler.SpanInfoManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Configuration
@ConditionalOnProperty(name = "spring.data.mongodb.uri")
public class TracingAspect {
    @Value("${spring.data.mongodb.uri:null}")
    private String connectionString;

    private final SpanInfoManager spanInfoManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public TracingAspect(SpanInfoManager spanInfoManager) {
        this.spanInfoManager = spanInfoManager;
    }

    @Around("@annotation(Traceable)")
    public Object traceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        SpanInfo spanInfo = SpanInfo.builder()
                .spanName(joinPoint.getSignature().toShortString())
                .timestamp(System.currentTimeMillis())
                .methodName(joinPoint.getSignature().getName())
                .parameters(serializeArgs(joinPoint.getArgs()))
                .build();
        try {
            Object result = joinPoint.proceed();
            spanInfo.setResult(serializeResult(result));
            return result;
        } catch (Throwable throwable) {
            spanInfo.setError(serializeError(throwable));
            throw throwable;
        } finally {
            spanInfoManager.addSpanInfo(spanInfo);
        }
    }

    private String serializeArgs(Object[] args) {
        try {
            return objectMapper.writeValueAsString(args);
        } catch (Exception e) {
            return Arrays.stream(args)
                    .map(arg -> arg != null ? arg.toString() : "null")
                    .collect(Collectors.joining(", "));
        }
    }

    private String serializeResult(Object result) {
        if (result == null) return null;
        try {
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            return result.toString();
        }
    }

    private String serializeError(Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

}


