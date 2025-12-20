package org.masouras.data.boundary;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.masouras.data.domain.FileOkDto;
import org.masouras.squad.printing.mssql.schema.jpa.control.ActivityType;
import org.masouras.squad.printing.mssql.schema.jpa.control.PrintingStatus;
import org.masouras.squad.printing.mssql.schema.jpa.entity.ActivityEntity;
import org.masouras.squad.printing.mssql.schema.jpa.entity.PrintingDataEntity;
import org.masouras.squad.printing.mssql.schema.jpa.entity.PrintingFilesEntity;
import org.masouras.squad.printing.mssql.schema.jpa.repository.PrintingDataRepository;
import org.masouras.squad.printing.mssql.schema.jpa.repository.PrintingFilesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class RepositoryFacade {
    private final PrintingDataRepository printingDataRepository;
    private final PrintingFilesRepository printingFilesTable;

    @Autowired
    public RepositoryFacade(PrintingDataRepository printingDataRepository, PrintingFilesRepository printingFilesTable) {
        this.printingDataRepository = printingDataRepository;
        this.printingFilesTable = printingFilesTable;
    }

    @Transactional
    public Long saveInitialPrintingData(FileOkDto fileOkDto, @NonNull String initialContentBase65) {
        PrintingFilesEntity printingFilesEntity = savePrintingFilesEntity(initialContentBase65);
        ActivityEntity activityEntity = createActivity(fileOkDto.getActivityType());
        PrintingDataEntity printingDataEntity = new PrintingDataEntity(
                activityEntity,
                fileOkDto.getContentType(),
                fileOkDto.getFileExtensionType(),
                printingFilesEntity
        );
        return printingDataRepository.save(printingDataEntity).getId();
    }
    private PrintingFilesEntity savePrintingFilesEntity(@NonNull String contentBase64) {
        PrintingFilesEntity printingFilesEntity = new PrintingFilesEntity(contentBase64);
        return printingFilesTable.save(printingFilesEntity);
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
    public PrintingDataEntity saveContentValidated(PrintingDataEntity printingDataEntity, String contentBase64) {
        PrintingFilesEntity printingFilesEntity = savePrintingFilesEntity(contentBase64);
        printingDataEntity.setValidatedContent(printingFilesEntity);
        printingDataEntity.setPrintingStatus(PrintingStatus.VALIDATED);
        return printingDataRepository.save(printingDataEntity);
    }
    @Transactional
    public PrintingDataEntity saveContentParsed(PrintingDataEntity printingDataEntity, String contentBase64) {
        PrintingFilesEntity printingFilesEntity = savePrintingFilesEntity(contentBase64);
        printingDataEntity.setFinalContent(printingFilesEntity);
        printingDataEntity.setPrintingStatus(PrintingStatus.PROCESSED);
        return printingDataRepository.save(printingDataEntity);
    }

    @Transactional
    public void saveStepFailed(PrintingDataEntity printingDataEntity, String errorMessage) {
        printingDataEntity.setErrorMessage(errorMessage);
        printingDataEntity.setPrintingStatus(PrintingStatus.ERROR);
        printingDataRepository.save(printingDataEntity);
    }

}

