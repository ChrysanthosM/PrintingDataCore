package org.masouras.data.boundary;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.masouras.squad.printing.mssql.schema.jpa.control.ActivityType;
import org.masouras.squad.printing.mssql.schema.jpa.control.ContentType;
import org.masouras.squad.printing.mssql.schema.jpa.control.FileExtensionType;
import org.masouras.squad.printing.mssql.schema.jpa.control.PrintingStatus;
import org.masouras.squad.printing.mssql.schema.jpa.entity.ActivityEntity;
import org.masouras.squad.printing.mssql.schema.jpa.entity.PrintingDataEntity;
import org.masouras.squad.printing.mssql.schema.jpa.repository.ActivityRepository;
import org.masouras.squad.printing.mssql.schema.jpa.repository.PrintingDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public PrintingDataEntity saveContentValidated(PrintingDataEntity printingDataEntity, String contentBase64) {
        printingDataEntity.setContentBase64(contentBase64);
        printingDataEntity.setPrintingStatus(PrintingStatus.VALIDATED);
        return printingDataRepository.save(printingDataEntity);
    }

    @Transactional
    public void saveValidationFailed(PrintingDataEntity printingDataEntity, String errorMessage) {
        printingDataEntity.setErrorMessage(errorMessage);
        printingDataEntity.setPrintingStatus(PrintingStatus.ERROR);
        printingDataRepository.save(printingDataEntity);
    }

}

