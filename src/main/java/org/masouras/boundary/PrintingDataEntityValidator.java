package org.masouras.boundary;

import org.masouras.model.mssql.schema.jpa.control.entity.PrintingDataEntity;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.PrintingWayType;

public interface PrintingDataEntityValidator {
    PrintingDataEntity validatePrintingDataEntity(Long printingDataEntityId, PrintingWayType checkPrintingWayType);
    PrintingDataEntity validatePrintingDataEntity(PrintingDataEntity printingDataEntity);
}
