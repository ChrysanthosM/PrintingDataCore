package org.masouras.data.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.masouras.printing.sqlite.schema.control.ActivityType;
import org.masouras.printing.sqlite.schema.control.ContentType;
import org.masouras.printing.sqlite.schema.entity.ActivityEntity;
import org.masouras.printing.sqlite.schema.entity.PrintingDataEntity;
import org.masouras.printing.sqlite.schema.repository.ActivityRepository;
import org.masouras.printing.sqlite.schema.repository.PrintingDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class FileOnDBActions {
    private final ActivityRepository activityRepository;
    private final PrintingDataRepository printingDataRepository;

    @Autowired
    public FileOnDBActions(ActivityRepository activityRepository, PrintingDataRepository printingDataRepository) {
        this.activityRepository = activityRepository;
        this.printingDataRepository = printingDataRepository;
    }

    public ActivityEntity createActivity(@NonNull ActivityType activityType) {
        ActivityEntity activityEntity = new ActivityEntity(
                activityType,
                this.getClass().getName(),
                System.getProperty("user.name"),
                LocalDateTime.now()
        );
        return activityRepository.save(activityEntity);
    }

    public Long savePrintingData(ActivityEntity activityEntity, @NonNull ContentType contentType, @NonNull String contentBase64) {
        PrintingDataEntity printingDataEntity = new PrintingDataEntity(
                activityEntity,
                contentType,
                contentBase64
        );
        return printingDataRepository.save(printingDataEntity).getId();
    }

}

