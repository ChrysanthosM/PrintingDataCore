package org.masouras.data.service;

import lombok.extern.slf4j.Slf4j;
import org.masouras.printing.sqlite.repo.squad.data.ActivityJ2SQL;
import org.masouras.printing.sqlite.repo.squad.data.ActivityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FileOnDBActions {
    private final ActivityJ2SQL activityJ2SQL;

    @Autowired
    public FileOnDBActions(ActivityJ2SQL activityJ2SQL) {
        this.activityJ2SQL = activityJ2SQL;
    }

    public int createActivity() {
        String runSql = activityJ2SQL.getSQL(ActivityRepo.NameOfSQL.INSERT);
        if (log.isDebugEnabled()) log.debug("Will run sql: {}", runSql);
        return 0;
    }

}
