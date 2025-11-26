package org.masouras.data.service;

import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.masouras.printing.sqlite.repo.squad.data.ActivityJ2SQL;
import org.masouras.printing.sqlite.repo.squad.data.ActivityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class FileOnDBActions {
    private final ActivityJ2SQL activityJ2SQL;

    @Autowired
    public FileOnDBActions(ActivityJ2SQL activityJ2SQL) {
        this.activityJ2SQL = activityJ2SQL;
    }

    @Transactional
    public int createActivity() {
        Query runQuery = activityJ2SQL.getNativeQuery(ActivityRepo.NameOfSQL.INSERT);
        runQuery.setParameter(1, "10001");
        runQuery.setParameter(2, this.getClass().getName());
        runQuery.setParameter(3, System.getProperty("user.name"));
        runQuery.setParameter(4, LocalDateTime.now());
        return runQuery.executeUpdate();
    }

}
