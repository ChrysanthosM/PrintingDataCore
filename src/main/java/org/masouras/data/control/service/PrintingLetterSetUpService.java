package org.masouras.data.control.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.masouras.data.boundary.RepositoryFacade;
import org.masouras.squad.printing.mssql.schema.jpa.projection.PrintingLetterSetUpProjectionImplementor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrintingLetterSetUpService {
    private final RepositoryFacade repositoryFacade;

    private final AtomicBoolean refreshingNow = new AtomicBoolean(false);

    private volatile List<PrintingLetterSetUpProjectionImplementor> printingLetterSetUpProjectionImplementors = List.of();
    @Getter
    private volatile Map<String, Map<String, List<PrintingLetterSetUpProjectionImplementor>>> printingLetterLookUpMap = Map.of();

    @PostConstruct
    public void init() {
        refresh();
    }
    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES, initialDelay = 1)
    public void scheduledRefresh() {
        refresh();
    }
    public synchronized void refresh() {
        if (!refreshingNow.compareAndSet(false, true)) return;
        try {
            this.printingLetterSetUpProjectionImplementors = repositoryFacade.getPrintingLetterSetUpProjections();
            this.printingLetterLookUpMap = this.printingLetterSetUpProjectionImplementors.stream()
                    .collect(Collectors.groupingBy(row -> row.getActivityType().getCode(),
                            Collectors.groupingBy(row -> row.getContentType().getCode())));
        } catch (Exception e) {
            log.error("Refresh of PrintingLetterSetUpService failed with message: {}", e.getMessage(), e);
        } finally {
            refreshingNow.set(false);
        }
    }

}
