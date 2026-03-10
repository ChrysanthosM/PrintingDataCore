package org.masouras.boundary;

import org.masouras.model.mssql.schema.jpa.control.entity.PrintingDataEntity;

public interface PrintingDataEntityProcessor {
    PrintingDataEntity processPrintingDataEntity(Long id);
    PrintingDataEntity processPrintingDataEntity(PrintingDataEntity printingDataEntity);
}
