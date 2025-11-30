package org.masouras.data.boundary;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.masouras.data.control.CsvParser;
import org.masouras.data.control.FileExtensionType;
import org.masouras.data.domain.FileOkDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
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

    public File getRelevantFile(File okFile, FileOkDto fileOkDto) {
        String baseName = Files.getNameWithoutExtension(okFile.getName());
        return new File(okFile.getParentFile(), baseName + "." + fileOkDto.getFileExtensionType().getExtension());
    }
    public List<String> getPossibleRelevantFileNames(File okFile) {
        String baseName = Files.getNameWithoutExtension(okFile.getName());
        return Arrays.stream(FileExtensionType.values()).map(ext -> baseName + "." + ext.getExtension()).toList();
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
    public void copyFile(@NonNull File file, String toPath) {
        Preconditions.checkArgument(StringUtils.isNotBlank(toPath));

        if (!ensureFolderExists(toPath)) {
            if (log.isWarnEnabled()) log.warn("Folder NOT exists/created : '{}'", toPath);
        }
        boolean fileCopied = fileCopyMain(file, toPath);
        if (log.isDebugEnabled()) log.debug("{} file copied:{}", file.getName(), fileCopied);
        if (!fileCopied && log.isWarnEnabled()) log.warn("{} file NOT copied:", file.getName());
    }
    private boolean fileCopyMain(@NotNull File file, String toPath) {
        try {
            FileCopyUtils.copy(file, new File(toPath, file.getName()));
            return true;
        } catch (IOException e) {
            log.error("Error copying file {} to {}: {}", file.getName(), toPath, e.getMessage(), e);
            return false;
        }
    }
    private boolean ensureFolderExists(String toPath) {
        File targetDir = new File(toPath);
        if (!targetDir.exists()) {
            return targetDir.mkdirs();
        }
        return false;
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
