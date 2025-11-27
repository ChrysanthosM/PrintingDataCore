package org.masouras.data.service;

import lombok.extern.slf4j.Slf4j;
import org.masouras.printing.sqlite.schema.entity.ActivityEntity;
import org.masouras.printing.sqlite.schema.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class FileOnDBActions {
    private final ActivityRepository activityRepository;

    @Autowired
    public FileOnDBActions(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Transactional
    public Long createActivity() {
        ActivityEntity activity = new ActivityEntity();
        activity.setActivityType("10001");
        activity.setPgmStamp(this.getClass().getName());
        activity.setUserStamp(System.getProperty("user.name"));
        activity.setDateStamp(LocalDateTime.now());

        return activityRepository.save(activity).getId();
    }
}

