package org.masouras.control.parser;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public final class ParserParamsValidator {
    private final int requiredParams;
    private final Object[] params;
    private final List<Class<?>> expectedTypes = new ArrayList<>();

    public static ParserParamsValidator of(int requiredParams, Object[] params) {
        return new ParserParamsValidator(requiredParams, params);
    }

    public ParserParamsValidator expect(Class<?>... expectedTypes) {
        Objects.requireNonNull(expectedTypes, "expectedTypes must not be null");
        Validate.noNullElements(expectedTypes, "expectedTypes contains null elements");
        this.expectedTypes.addAll(List.of(expectedTypes));
        return this;
    }

    public Object[] validate() {
        Validate.isTrue(requiredParams >= 0, "required parameters must not be negative");
        if (requiredParams == 0) {
            Validate.isTrue(ArrayUtils.isEmpty(params), "params must be empty");
            return new Object[0];
        }

        Objects.requireNonNull(params, "params must not be null");
        Validate.noNullElements(params, "params contains null elements");
        Validate.isTrue(params.length == requiredParams, "invalid number of params");
        Validate.isTrue(expectedTypes.size() == requiredParams, "invalid number of expectedTypes");

        return IntStream.range(0, params.length)
                .mapToObj(i -> {
                    Object paramValue = params[i];
                    Class<?> expectedType = expectedTypes.get(i);
                    if (!expectedType.isInstance(paramValue)) {
                        throw new IllegalArgumentException("param[" + i + "] must be of type " + expectedType.getSimpleName());
                    }
                    return paramValue;
                })
                .toArray(Object[]::new);
    }
}
