package org.masouras.data.boundary;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.masouras.data.control.converter.CsvParser;
import org.masouras.data.domain.TriggerFileDto;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.FileExtensionType;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilesFacade {
    private final CsvParser csvParser;

    public @NonNull File getRelevantFile(File triggerFile, TriggerFileDto triggerFileDto) {
        String baseName = FilenameUtils.removeExtension(triggerFile.getName());
        return new File(triggerFile.getParentFile(), baseName + "." + triggerFileDto.getFileExtensionType().name());
    }
    public List<String> getPossibleRelevantFileNames(File triggerFile) {
        return Arrays.stream(FileExtensionType.values())
                .map(ext -> new File(triggerFile.getParent(), FilenameUtils.removeExtension(triggerFile.getName()) + "." + ext.name().toLowerCase()).getAbsolutePath())
                .toList();
    }

    public Optional<byte[]> getContentBytes(File fromFile) {
        try {
            return Optional.of(Files.readAllBytes(fromFile.toPath()));
        } catch (IOException e) {
            log.error("Failed to read relevant File: {}", fromFile.getAbsolutePath(), e);
            return Optional.empty();
        }
    }

    public void deleteFile(File file) {
        boolean fileDeleted = file.exists() && file.isFile() && file.delete();
        if (log.isDebugEnabled()) log.debug("{} file deleted:{}", file.getName(), fileDeleted);
        if (!fileDeleted && log.isWarnEnabled()) log.warn("{} file NOT deleted:", file.getName());
    }

    public void copyFile(File file, String toPath) {
        if (!ensureFolderExists(toPath)) {
            if (log.isWarnEnabled()) {
                log.warn("Folder NOT exists/created : '{}'", toPath);
                log.warn("{} file NOT copied:", file.getName());
                return;
            }
        }
        boolean fileCopied = fileCopyMain(file, toPath);
        if (log.isDebugEnabled()) log.debug("{} file copied:{}", file.getName(), fileCopied);
        if (!fileCopied && log.isWarnEnabled()) log.warn("{} file NOT copied:", file.getName());
    }
    private boolean fileCopyMain(File file, String toPath) {
        try {
            FileCopyUtils.copy(file, new File(toPath, file.getName()));
            return true;
        } catch (IOException e) {
            log.error("Error copying file {} to {}: {}", file.getName(), toPath, e.getMessage(), e);
            return false;
        }
    }
    private boolean ensureFolderExists(String toPath) {
        try {
            Path targetDir = Paths.get(toPath);
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            return Files.isDirectory(targetDir);
        } catch (IOException e) {
            log.error("Error creating folder for '{}'", toPath, e);
            return false;
        }
    }

    public void moveFile(File file, String moveToPath) {
        if (!ensureFolderExists(moveToPath)) {
            if (log.isWarnEnabled()) {
                log.warn("Folder NOT exists/created : '{}'", moveToPath);
                log.warn("{} file NOT moved:", file.getName());
                return;
            }
            return;
        }
        try {
            Path target = Paths.get(moveToPath).resolve(file.getName());
            Files.move(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            log.info("Moved file '{}' to '{}'", file.getAbsolutePath(), target.toAbsolutePath());
        } catch (IOException e) {
            log.error("Error moving file from '{}' to '{}'", file.toPath(), moveToPath, e);
        }
    }

    public <T> List<T> getCsvContent(@NonNull Class<T> type, @NonNull CsvParser.DelimiterType delimiterType, File fromFile) {
        try {
            return csvParser.parseFile(type, fromFile, delimiterType);
        } catch (Exception e) {
            log.error("Failed to parseFile: {}", fromFile.getAbsolutePath(), e);
            return Collections.emptyList();
        }
    }

    public String documentToString(Document doc) throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.getBuffer().toString();
    }

    public byte[] objectToByteArray(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Serialization failed", e);
        }
    }
    @SuppressWarnings("unchecked")
    public <T> List<T> byteArrayToObject(byte[] data) {
        if (data == null) return null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (List<T>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Deserialization failed", e);
        }
    }

}
