package org.masouras.data.domain;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Getter;

@Getter
public class FileOkRecord {
    @CsvBindByPosition(position = 0)
    private String relevantFileExtension;

    @CsvBindByPosition(position = 1)
    private String contentType;
}
