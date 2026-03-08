package org.masouras.data.boundary;

import org.masouras.model.mssql.schema.jpa.control.entity.PrintingDataEntity;

public interface PrintingDataEntityProcessor {
    void processPrintingDataEntity(Long id);
    PrintingDataEntity processPrintingDataEntity(PrintingDataEntity printingDataEntity);
}
