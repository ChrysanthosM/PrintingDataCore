package org.masouras.data.control;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Collections;
import java.util.List;

@Component
public class CsvParser {
    @RequiredArgsConstructor
    @Getter
    public enum DelimiterType {
        COMMA(','), PIPE('|');
        private final char delimiter;
    }

    public <T> List<T> parseFile(@NonNull Class<T> type, @NonNull File file, @NonNull DelimiterType delimiterType) throws Exception {
        if (!file.exists() || !file.isFile()) return Collections.emptyList();

        try (Reader reader = new FileReader(file)) {
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withType(type)
                    .withSeparator(delimiterType.getDelimiter())
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse();
        }
    }
}
