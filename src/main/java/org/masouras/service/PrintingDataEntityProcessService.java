package org.masouras.service;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.masouras.control.parser.FileProcessor;
import org.masouras.control.parser.FileProcessorFactory;
import org.masouras.domain.FileProcessorResult;
import org.masouras.exception.ValidationException;
import org.masouras.facade.FilesFacade;
import org.masouras.facade.RepositoryFacade;
import org.masouras.model.mssql.schema.jpa.control.entity.PrintingDataEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrintingDataEntityProcessService {
    private final FileProcessorFactory fileProcessorFactory;
    private final FilesFacade filesFacade;
    private final RepositoryFacade repositoryFacade;

    public PrintingDataEntity process(Long printingDataEntityId) {
        PrintingDataEntity printingDataEntity = repositoryFacade.getPrintingDataEntityById(printingDataEntityId);
        return process(printingDataEntity);
    }

    @Timed("printing.data.entity.process")
    @Counted("printing.data.entity.process")
    public PrintingDataEntity process(PrintingDataEntity printingDataEntity) {
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
