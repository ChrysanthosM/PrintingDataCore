package org.masouras.data.service;

import com.google.common.io.Files;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.masouras.config.FileExtensionType;
import org.masouras.data.control.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class FileOnDiscActions {
    private final CsvParser csvParser;

    @Autowired
    public FileOnDiscActions(CsvParser csvParser) {
        this.csvParser = csvParser;
    }

    public File getRelevantFile(File okFile, FileExtensionType fileExtensionType) {
        String baseName = Files.getNameWithoutExtension(okFile.getName());
        return new File(okFile.getParentFile(), baseName + "." + fileExtensionType.getExtension());
    }


    public String getContentBase64(File fromFile) {
        try {
            return Base64.getEncoder().encodeToString(java.nio.file.Files.readAllBytes(fromFile.toPath()));
        } catch (IOException e) {
            log.error("Failed to read relevant File: {}", fromFile.getAbsolutePath(), e);
            return null;
        }
    }

    public void deleteFile(@NonNull File file) {
        boolean fileDeleted = file.exists() && file.isFile() && file.delete();
        if (log.isDebugEnabled()) log.debug("{} file deleted:{}", file.getName(), fileDeleted);
        if (!fileDeleted && log.isWarnEnabled()) log.warn("{} file NOT deleted:", file.getName());
    }

    public <T> List<T> getCsvContent(@NonNull Class<T> type, @NonNull File fromFile, @NonNull CsvParser.DelimiterType delimiterType) {
        try {
            return csvParser.parseFile(type, fromFile, delimiterType);
        } catch (Exception e) {
            log.error("Failed to parseFile: {}", fromFile.getAbsolutePath(), e);
            return Collections.emptyList();
        }
    }
}
