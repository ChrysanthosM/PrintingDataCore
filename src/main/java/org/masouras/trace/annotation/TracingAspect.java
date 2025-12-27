package org.masouras.trace.annotation;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.masouras.trace.control.scheduler.SpanInfoManager;
import org.masouras.trace.domain.SpanInfo;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Configuration
@RequiredArgsConstructor
public class TracingAspect {
    private final SpanInfoManager spanInfoManager;
    private final Tracer tracer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("@annotation(Traceable)")
    public Object traceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String spanName = joinPoint.getSignature().toShortString();

        SpanInfo spanInfo = SpanInfo.builder()
                .spanName(spanName)
                .timestamp(System.currentTimeMillis())
                .methodName(joinPoint.getSignature().getName())
                .parameters(serializeArgs(joinPoint.getArgs()))
                .build();

        Span otelSpan = tracer.spanBuilder(spanName).startSpan();
        try (Scope scope = otelSpan.makeCurrent()) {
            Object result = joinPoint.proceed();
            spanInfo.setResult(serializeResult(result));
            return result;
        } catch (Throwable throwable) {
            spanInfo.setError(serializeError(throwable));

            otelSpan.recordException(throwable);
            otelSpan.setStatus(StatusCode.ERROR);

            throw throwable;
        } finally {
            otelSpan.end();

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


