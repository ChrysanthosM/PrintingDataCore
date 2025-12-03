package org.masouras.trace.scheduler;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.masouras.trace.domain.SpanInfo;
import org.masouras.trace.control.repository.mongo.SpanInfoRepository;
import org.masouras.trace.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
public class SpanInfoManager {
    private final Queue<SpanInfo> spanInfoQueue = new ConcurrentLinkedDeque<>();
    private final ReentrantLock lock = new ReentrantLock();

    private final SpanInfoRepository spanInfoRepository;
    private final MongoTemplate mongoTemplate;
    private final EmailService emailService;


    @Autowired
    public SpanInfoManager(SpanInfoRepository spanInfoRepository, MongoTemplate mongoTemplate, EmailService emailService) {
        this.spanInfoRepository = spanInfoRepository;
        this.mongoTemplate = mongoTemplate;
        this.emailService = emailService;
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

        if (isMongoAvailable()) {
            spanInfoRepository.saveAll(spanInfos);
            spanInfoQueue.removeAll(spanInfos);
        } else {
            if (log.isWarnEnabled()) log.warn("MongoDB not available, skipping save");
        }

        spanInfoRepository.saveAll(spanInfos);
        spanInfoQueue.removeAll(spanInfos);
    }
    private boolean isMongoAvailable() {
        try {
            mongoTemplate.executeCommand("{ ping: 1 }");
            return true;
        } catch (Exception e) {
            log.error("isMongoAvailable failed: {}", e.getMessage(), e);
            emailService.sendAlert("admin@example.com", "MongoDB Save Failed", "Failed to save SpanInfo batch: " + e.getMessage());
            return false;
        }
    }
}
