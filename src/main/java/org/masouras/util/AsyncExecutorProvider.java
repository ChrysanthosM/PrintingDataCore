package org.masouras.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

@UtilityClass
@Slf4j
public class AsyncExecutorProvider {

    public static void runAsync(Callable<Boolean> backgroundTask, Consumer<Boolean> successHandler, Consumer<Throwable> errorHandler) {
        runAsyncSequential(List.of(backgroundTask), successHandler, errorHandler);
    }
    public static void runAsyncSequential(List<Callable<Boolean>> backgroundTasks, Consumer<Boolean> successHandler, Consumer<Throwable> errorHandler) {
        CompletableFuture
                .supplyAsync(() -> runBackgroundTasksSequentialAsync(backgroundTasks))
                .whenComplete((result, err) -> backgroundTasksCompletedAll(successHandler, result, err, errorHandler));
    }
    private static @NonNull Boolean runBackgroundTasksSequentialAsync(List<Callable<Boolean>> backgroundTasks) {
        try {
            return runBackgroundTasksSequential(backgroundTasks);
        } catch (Exception e) {
            log.error("runBackgroundTasksSequentialAsync failed with message: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    private static Boolean runBackgroundTasksSequential(List<Callable<Boolean>> backgroundTasks) {
        return backgroundTasks.stream().allMatch(AsyncExecutorProvider::callBackgroundTask);
    }
    private static Boolean callBackgroundTask(Callable<Boolean> backgroundTask) {
        try {
            return backgroundTask.call();
        } catch (Exception e) {
            log.error("callBackgroundTask failed with message: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static void backgroundTasksCompletedAll(Consumer<Boolean> successHandler, Boolean result,
                                                    Throwable err, Consumer<Throwable> errorHandler) {
        if (err != null) {
            errorHandler.accept(err.getCause() != null ? err.getCause() : err);
        } else {
            successHandler.accept(result);
        }
    }

    public static <T> T unwrapNonBlockingFuture(Future<T> future) {
        if (future == null) return null;
        if (!future.isDone()) {
            if (log != null && log.isWarnEnabled()) log.warn("Future result is not done yet, cannot unwrap non-blocking.");
            return null;
        }
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to unwrap completed Future", e);
            return null;
        }
    }

}
