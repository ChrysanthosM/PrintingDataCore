package org.masouras.data.facade;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.masouras.data.boundary.PrintingDataEntityProcessor;
import org.masouras.data.control.parser.FileProcessor;
import org.masouras.data.control.parser.FileProcessorFactory;
import org.masouras.data.domain.FileProcessorResult;
import org.masouras.data.exception.ValidationException;
import org.masouras.model.mssql.schema.jpa.control.entity.PrintingDataEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrintingJobFacade implements PrintingDataEntityProcessor {
    private final FileProcessorFactory fileProcessorFactory;
    private final FilesFacade filesFacade;
    private final RepositoryFacade repositoryFacade;

    @Override
    public void processPrintingDataEntity(Long printingDataEntityId) {
        if (log.isInfoEnabled()) log.info("{}: Parsing printingDataEntity {}", this.getClass().getSimpleName(), printingDataEntityId);
        PrintingDataEntity printingDataEntity = repositoryFacade.getPrintingDataEntityById(printingDataEntityId);
        processPrintingDataEntity(printingDataEntity);
    }

    @Timed("PrintingJobFacade.processPrintingDataEntity")
    @Counted("PrintingJobFacade.processPrintingDataEntity")
    @Override
    public PrintingDataEntity processPrintingDataEntity(PrintingDataEntity printingDataEntity) {
        if (log.isInfoEnabled()) log.info("{}: Parsing printingDataEntity {}", this.getClass().getSimpleName(), printingDataEntity.getId());
        return saveContentParsed(printingDataEntity, getFileProcessorResult(printingDataEntity));
    }

    private FileProcessorResult getFileProcessorResult(PrintingDataEntity printingDataEntity) {
        FileProcessor fileProcessor = fileProcessorFactory.getFileProcessor(printingDataEntity.getFileExtensionType().name());
        if (fileProcessor == null) { throw new ValidationException("Parser failed, FileExtensionType not found: " + printingDataEntity.getFileExtensionType().name()); }
        FileProcessorResult fileProcessorResult = fileProcessor.getFileProcessorResult(printingDataEntity.getActivity().getActivityType(), printingDataEntity.getContentType(), printingDataEntity.getValidatedContent().getContentBinary());
        if (fileProcessorResult.getStatus() == FileProcessorResult.ProcessorStatus.ERROR) throw new ValidationException("Parser failed with message: " + fileProcessorResult.getMessage());
        return fileProcessorResult;
    }

    private PrintingDataEntity saveContentParsed(@NonNull PrintingDataEntity printingDataEntity, FileProcessorResult fileProcessorResult) {
        return repositoryFacade.saveContentParsed(printingDataEntity, filesFacade.objectToByteArray(fileProcessorResult.getResult()));
    }
}
