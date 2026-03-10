package org.masouras.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.masouras.boundary.PrintingDataEntityProcessor;
import org.masouras.boundary.PrintingDataEntityValidator;
import org.masouras.model.mssql.schema.jpa.control.entity.PrintingDataEntity;
import org.masouras.service.PrintingDataEntityProcessService;
import org.masouras.service.PrintingDataEntityValidateService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrintingDataEntityFacade implements PrintingDataEntityValidator, PrintingDataEntityProcessor {
    private final PrintingDataEntityProcessService printingDataEntityProcessService;
    private final PrintingDataEntityValidateService printingDataEntityValidateService;

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
