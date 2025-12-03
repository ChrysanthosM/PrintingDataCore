package org.masouras.trace.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.masouras.trace.domain.SpanInfo;
import org.masouras.trace.scheduler.SpanInfoManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
public class TracingAspect {
//    private @Autowired Tracer tracer;

//    @Around("@annotation(Traceable)")
//    public Object traceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
//        String spanName = joinPoint.getSignature().toShortString();
//        Span span = tracer.spanBuilder(spanName).startSpan();
//        try {
//            return joinPoint.proceed();
//        } finally {
//            span.end();
//        }
//    }

    private final SpanInfoManager spanInfoManager;

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
                .parameters(Arrays.stream(joinPoint.getArgs()).map(Object::toString).collect(Collectors.joining(", ")))
                .build();

        try {
            Object result = joinPoint.proceed();
            spanInfo.setResult(result != null ? result.toString() : null);
            return result;
        } catch (Throwable throwable) {
            spanInfo.setError(throwable.getMessage());
            throw throwable;
        } finally {
            spanInfoManager.addSpanInfo(spanInfo);
        }
    }
}


