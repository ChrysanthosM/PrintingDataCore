package org.masouras.trace.scheduler;

import jakarta.annotation.PreDestroy;
import org.apache.commons.collections4.CollectionUtils;
import org.masouras.trace.domain.SpanInfo;
import org.masouras.trace.control.repository.mongo.SpanInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class SpanInfoManager {
    private final Queue<SpanInfo> spanInfoQueue = new ConcurrentLinkedDeque<>();
    private final ReentrantLock lock = new ReentrantLock();

    private final SpanInfoRepository spanInfoRepository;

    @Autowired
    public SpanInfoManager(SpanInfoRepository spanInfoRepository) {
        this.spanInfoRepository = spanInfoRepository;
    }

    public void addSpanInfo(SpanInfo spanInfo) {
        spanInfoQueue.add(spanInfo);
    }

    @Scheduled(cron = "0 * * * * *")
    @PreDestroy
    @Transactional
    public void saveSpanInfo() {
        if (lock.tryLock()) {
            try {
                saveSpanInfoMain();
            } finally {
                lock.unlock();
            }
        }
    }

    private void saveSpanInfoMain() {
        List<SpanInfo> spanInfos = spanInfoQueue.stream()
                .filter(Objects::nonNull)
                .toList();
        if (CollectionUtils.isEmpty(spanInfos)) return;
        spanInfoRepository.saveAll(spanInfos);
        spanInfoQueue.removeAll(spanInfos);
    }
}
