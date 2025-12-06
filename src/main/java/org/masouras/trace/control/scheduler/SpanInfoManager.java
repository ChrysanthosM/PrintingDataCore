package org.masouras.trace.control.scheduler;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.masouras.trace.control.repository.SpanInfoMongoRepository;
import org.masouras.trace.domain.SpanInfo;
import org.masouras.trace.control.service.EmailService;
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

    private final SpanInfoMongoRepository spanInfoMongoRepository;
    private final MongoTemplate mongoTemplate;
    private final EmailService emailService;


    @Autowired
    public SpanInfoManager(SpanInfoMongoRepository spanInfoMongoRepository, MongoTemplate mongoTemplate, EmailService emailService) {
        this.spanInfoMongoRepository = spanInfoMongoRepository;
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
            spanInfoMongoRepository.saveAll(spanInfos);
            spanInfoQueue.removeAll(spanInfos);
        } else {
            if (log.isWarnEnabled()) log.warn("MongoDB not available, skipping save");
        }
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
