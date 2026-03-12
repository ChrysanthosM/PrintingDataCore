package org.masouras.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.masouras.boundary.PrintingDataEntityInitialPersister;
import org.masouras.boundary.PrintingDataEntityProcessor;
import org.masouras.boundary.PrintingDataEntityValidator;
import org.masouras.model.mssql.schema.jpa.control.entity.PrintingDataEntity;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.PrintingWayType;
import org.masouras.service.PrintingDataEntityInitialService;
import org.masouras.service.PrintingDataEntityProcessService;
import org.masouras.service.PrintingDataEntityValidateService;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrintingDataEntityFacade implements PrintingDataEntityInitialPersister, PrintingDataEntityValidator, PrintingDataEntityProcessor {
    private final PrintingDataEntityInitialService printingDataEntityInitialService;
    private final PrintingDataEntityValidateService printingDataEntityValidateService;
    private final PrintingDataEntityProcessService printingDataEntityProcessService;

    @Override
    public Long initialPersist(File triggerFile) {
        return printingDataEntityInitialService.initial(triggerFile);
    }

    @Override
    public PrintingDataEntity validatePrintingDataEntity(Long printingDataEntityId, PrintingWayType checkPrintingWayType) {
        return printingDataEntityValidateService.validate(printingDataEntityId, checkPrintingWayType);
    }
    @Override
    public PrintingDataEntity validatePrintingDataEntity(PrintingDataEntity printingDataEntity) {
        return printingDataEntityValidateService.validate(printingDataEntity);
    }

    @Override
    public PrintingDataEntity processPrintingDataEntity(Long printingDataEntityId) {
        return printingDataEntityProcessService.process(printingDataEntityId);
    }
    @Override
    public PrintingDataEntity processPrintingDataEntity(PrintingDataEntity printingDataEntity) {
        return printingDataEntityProcessService.process(printingDataEntity);
    }
}
