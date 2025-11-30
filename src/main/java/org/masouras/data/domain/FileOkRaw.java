package org.masouras.data.domain;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Getter;

@Getter
public class FileOkRaw {
    @CsvBindByPosition(position = 0)
    private String fileExtension;

    @CsvBindByPosition(position = 1)
    private String contentType;

    @CsvBindByPosition(position = 2)
    private String activityType;
}
