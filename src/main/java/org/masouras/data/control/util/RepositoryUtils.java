package org.masouras.data.control.util;

import lombok.experimental.UtilityClass;
import org.hibernate.query.TupleTransformer;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@UtilityClass
public class RepositoryUtils {
    public static TupleTransformer<Map<String, Object>> tupleTransformer() {
        return (tuple, aliases) ->
                IntStream.range(0, aliases.length)
                        .boxed()
                        .collect(Collectors.toMap(
                                i -> aliases[i],
                                i -> tuple[i]
                        ));
    }

}
