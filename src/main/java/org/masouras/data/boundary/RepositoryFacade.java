package org.masouras.data.boundary;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.masouras.data.domain.FileOkDto;
import org.masouras.model.mssql.schema.jpa.boundary.ActivityService;
import org.masouras.model.mssql.schema.jpa.boundary.PrintingDataService;
import org.masouras.model.mssql.schema.jpa.boundary.PrintingFilesService;
import org.masouras.model.mssql.schema.jpa.control.entity.ActivityEntity;
import org.masouras.model.mssql.schema.jpa.control.entity.PrintingDataEntity;
import org.masouras.model.mssql.schema.jpa.control.entity.PrintingFilesEntity;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.ActivityType;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.PrintingStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class RepositoryFacade {
    private final ActivityService activityService;
    private final PrintingDataService printingDataService;
    private final PrintingFilesService printingFilesService;

    private PrintingFilesEntity savePrintingFilesEntity(byte[] contentBinary) {
        PrintingFilesEntity printingFilesEntity = new PrintingFilesEntity(contentBinary);
        return printingFilesService.save(printingFilesEntity);
    }

    private ActivityEntity createActivity(@NonNull ActivityType activityType) {
        return new ActivityEntity(
                activityType,
                this.getClass().getName(),
                System.getProperty("user.name"),
                LocalDateTime.now()
        );
    }

    @Transactional
    public Long saveInitialPrintingData(FileOkDto fileOkDto, byte[] initialContentBinary) {
        PrintingFilesEntity printingFilesEntity = savePrintingFilesEntity(initialContentBinary);
        ActivityEntity activityEntity = activityService.save(createActivity(fileOkDto.getActivityType()));
        PrintingDataEntity printingDataEntity = new PrintingDataEntity(
                activityEntity,
                fileOkDto.getContentType(),
                fileOkDto.getFileExtensionType(),
                printingFilesEntity
        );
        return printingDataService.save(printingDataEntity).getId();
    }
    @Transactional
    public PrintingDataEntity saveContentValidated(PrintingDataEntity printingDataEntity, byte[] validatedContentBinary) {
        PrintingFilesEntity printingFilesEntity = savePrintingFilesEntity(validatedContentBinary);
        printingDataEntity.setValidatedContent(printingFilesEntity);
        printingDataEntity.setPrintingStatus(PrintingStatus.VALIDATED);
        return printingDataService.save(printingDataEntity);
    }
    @Transactional
    public PrintingDataEntity saveContentParsed(PrintingDataEntity printingDataEntity, byte[] finalContentBinary) {
        PrintingFilesEntity printingFilesEntity = savePrintingFilesEntity(finalContentBinary);
        printingDataEntity.setFinalContent(printingFilesEntity);
        printingDataEntity.setPrintingStatus(PrintingStatus.PROCESSED);
        return printingDataService.save(printingDataEntity);
    }

    @Transactional
    public void saveStepFailed(PrintingDataEntity printingDataEntity, String errorMessage) {
        printingDataEntity.setErrorMessage(errorMessage);
        printingDataEntity.setPrintingStatus(PrintingStatus.ERROR);
        printingDataService.save(printingDataEntity);
    }
}

