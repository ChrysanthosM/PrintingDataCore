package org.masouras.data.boundary;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.masouras.squad.printing.mssql.schema.jpa.control.ActivityType;
import org.masouras.squad.printing.mssql.schema.jpa.control.ContentType;
import org.masouras.squad.printing.mssql.schema.jpa.control.FileExtensionType;
import org.masouras.squad.printing.mssql.schema.jpa.entity.ActivityEntity;
import org.masouras.squad.printing.mssql.schema.jpa.entity.PrintingDataEntity;
import org.masouras.squad.printing.mssql.schema.jpa.repository.ActivityRepository;
import org.masouras.squad.printing.mssql.schema.jpa.repository.PrintingDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class RepositoryFacade {
    private final ActivityRepository activityRepository;
    private final PrintingDataRepository printingDataRepository;

    @Autowired
    public RepositoryFacade(ActivityRepository activityRepository, PrintingDataRepository printingDataRepository) {
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

    public Long savePrintingData(ActivityEntity activityEntity,
                                 @NonNull ContentType contentType, @NonNull FileExtensionType fileExtensionType, @NonNull String contentBase64) {
        PrintingDataEntity printingDataEntity = new PrintingDataEntity(
                activityEntity,
                contentType,
                fileExtensionType,
                contentBase64
        );
        return printingDataRepository.save(printingDataEntity).getId();
    }

    public PrintingDataEntity saveContentValidated(PrintingDataEntity printingDataEntity) {
        return printingDataRepository.save(printingDataEntity);
    }
}

