package org.masouras.trace.control.scheduler;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.masouras.trace.control.repository.SpanInfoMongoRepository;
import org.masouras.trace.control.service.EmailService;
import org.masouras.trace.domain.SpanInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
public class SpanInfoManager {
    @Value("${spring.data.mongodb.uri:#{null}}")
    private String mongoConnectionString;

    private final Queue<SpanInfo> spanInfoQueue = new ConcurrentLinkedDeque<>();
    private final ReentrantLock lock = new ReentrantLock();

    private final SpanInfoMongoRepository spanInfoMongoRepository;
    private final EmailService emailService;
    @Autowired(required = false) private MongoTemplate mongoTemplate;

    @Autowired
    public SpanInfoManager(SpanInfoMongoRepository spanInfoMongoRepository, EmailService emailService) {
        this.spanInfoMongoRepository = spanInfoMongoRepository;
        this.emailService = emailService;
    }

    public void addSpanInfo(SpanInfo spanInfo) {
        spanInfoQueue.add(spanInfo);
    }

    @Scheduled(cron = "0 * * * * *")
    @PreDestroy
    @Transactional
    public void saveSpanInfo() {
        if (StringUtils.isBlank(mongoConnectionString)) {
            log.warn("mongoConnectionString not found, spanInfoQueue will clear {} entries", spanInfoQueue.size());
            spanInfoQueue.clear();
            return;
        }

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
        if (mongoTemplate == null) {
            log.warn("MongoTemplate not configured, skipping persistence");
            return false;
        }
        return isMongoAvailableMain();
    }
    private boolean isMongoAvailableMain() {
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            Future<Boolean> future = executor.submit(() -> {
                mongoTemplate.executeCommand("{ ping: 1 }");
                return true;
            });

            try {
                return future.get(3, TimeUnit.SECONDS);
            } catch (TimeoutException te) {
                log.error("isMongoAvailable timed out after 3 seconds");
                emailService.sendAlert("admin@example.com", "MongoDB Save Timeout", "Ping to MongoDB timed out");
                return false;
            } catch (Exception e) {
                log.error("isMongoAvailable failed: {}", e.getMessage(), e);
                emailService.sendAlert("admin@example.com", "MongoDB Save Failed", "Failed to save SpanInfo batch: " + e.getMessage());
                return false;
            }
        }
    }
}
