package org.masouras.util;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@UtilityClass
public class ChunkUtil {

    public static <T> Map<Integer, Set<T>> chunkOf(Collection<T> entities, int chunkSize) {
        AtomicInteger index = new AtomicInteger(0);
        return entities.stream()
                .collect(Collectors.groupingBy(
                        _ -> index.getAndIncrement() / chunkSize,
                        LinkedHashMap::new,
                        Collectors.toUnmodifiableSet()
                ));
    }

}
