package org.masouras.data.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.masouras.config.FileExtensionType;
import org.masouras.printing.sqlite.schema.control.ContentType;
import org.masouras.printing.sqlite.schema.entity.ActivityEntity;
import org.masouras.printing.sqlite.schema.entity.PrintingDataEntity;
import org.masouras.printing.sqlite.schema.repository.ActivityRepository;
import org.masouras.printing.sqlite.schema.repository.PrintingDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Objects;

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

    public ActivityEntity createActivity(FileExtensionType fileExtensionType) {
        ActivityEntity activityEntity = new ActivityEntity(
                fileExtensionType.getActivityType(),
                this.getClass().getName(),
                System.getProperty("user.name"),
                LocalDateTime.now()
        );
        return activityRepository.save(activityEntity);
    }

    public Long savePrintingData(ActivityEntity activityEntity, File relevantFile, @NonNull String contentBase64) {
        PrintingDataEntity printingDataEntity = new PrintingDataEntity(
                activityEntity,
                Objects.requireNonNull(ContentType.fromStartsWith(relevantFile.getName().substring(0, relevantFile.getName().indexOf("_")))),
                contentBase64
        );
        return printingDataRepository.save(printingDataEntity).getId();
    }

}

