package org.masouras.data.boundary;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.NativeQuery;
import org.masouras.data.control.util.RepositoryUtils;
import org.masouras.data.domain.FileOkDto;
import org.masouras.squad.printing.mssql.repo.PrintingOptionsRepo;
import org.masouras.squad.printing.mssql.repo.PrintingOptionsSQL;
import org.masouras.squad.printing.mssql.schema.jpa.control.*;
import org.masouras.squad.printing.mssql.schema.jpa.entity.ActivityEntity;
import org.masouras.squad.printing.mssql.schema.jpa.entity.PrintingDataEntity;
import org.masouras.squad.printing.mssql.schema.jpa.entity.PrintingFilesEntity;
import org.masouras.squad.printing.mssql.schema.jpa.mapper.PrintingLetterSetUpMapper;
import org.masouras.squad.printing.mssql.schema.jpa.projection.PrintingLetterSetUpProjectionImplementor;
import org.masouras.squad.printing.mssql.schema.jpa.repository.PrintingDataRepository;
import org.masouras.squad.printing.mssql.schema.jpa.repository.PrintingFilesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class RepositoryFacade {
    private final PrintingDataRepository printingDataRepository;
    private final PrintingFilesRepository printingFilesTable;
    private final PrintingOptionsSQL printingOptionsSQL;

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

    @SuppressWarnings("unchecked")
    public List<PrintingLetterSetUpProjectionImplementor> getPrintingLetterSetUpProjections() {
        List<Map<String, Object>> rows = (List<Map<String, Object>>) printingOptionsSQL
                .getNativeQuery(PrintingOptionsRepo.NameOfSQL.LIST_PRINTING_SETUP)
                .unwrap(NativeQuery.class)
                .setTupleTransformer(RepositoryUtils.tupleTransformer())
                .getResultList();
        return PrintingLetterSetUpMapper.getPrintingLetterSetUpProjectionImplementors(rows);
    }



}

