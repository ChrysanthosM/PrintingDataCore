package org.masouras.data.control.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.masouras.data.boundary.FilesFacade;
import org.springframework.stereotype.Service;

import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Destination;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class PrintFileService {
    private final FilesFacade filesFacade;

    public List<String> getAvailablePrinters() {
        return Arrays.stream(PrinterJob.lookupPrintServices())
                .map(PrintService::getName)
                .sorted()
                .toList();
    }

    public void printPdf(byte[] pdfBytes, @Nullable String selectedPrinter) {
        printPdf(pdfBytes, selectedPrinter, null);
    }
    public void printPdf(byte[] pdfBytes, @Nullable String selectedPrinter, @Nullable String outputPath) {
        List<byte[]> pdfResultList = filesFacade.byteArrayToObject(pdfBytes);
        if (CollectionUtils.isEmpty(pdfResultList)) {
            if (log.isWarnEnabled()) log.warn("pdfResultList is empty after converting byte array to object. Cannot print PDFs.");
            return;
        }
        pdfResultList.forEach(result -> printPdfMain(result, selectedPrinter, outputPath));
    }

    private void printPdfMain(byte[] pdfBytes, @Nullable String selectedPrinter, @Nullable String outputPath) {
        if (!isLikelyPdf(pdfBytes)) {
            if (log.isWarnEnabled()) log.warn("Data does not appear to be a valid PDF. Skipping print.");
            return;
        }
        if (log.isInfoEnabled()) log.info("PDF result size: {}", pdfBytes.length);

        try (PDDocument pdDocument = Loader.loadPDF(pdfBytes)) {
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            printerJob.setJobName("Print pdDocument");
            printerJob.setPageable(new PDFPageable(pdDocument));

            if (StringUtils.isNotBlank(selectedPrinter)) {
                Arrays.stream(PrinterJob.lookupPrintServices())
                        .filter(ps -> ps.getName().equalsIgnoreCase(selectedPrinter))
                        .findFirst()
                        .ifPresent(ps -> setPrintService(selectedPrinter, ps, printerJob));
            }

            PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
            if (StringUtils.isNotBlank(outputPath)) {
                attr.add(new Destination(Paths.get(outputPath).toUri()));
            }
            printerJob.print(attr);
        } catch (Exception e) {
            log.error("printPdf failed with message {}", e.getMessage(), e);
        }
    }
    public boolean isLikelyPdf(byte[] data) {
        if (ArrayUtils.isEmpty(data) || data.length < 8) return false;

        String header = new String(data, 0, 8, StandardCharsets.UTF_8);
        if (!header.startsWith("%PDF-")) return false;

        // Check EOF marker in the last 1024 bytes
        int start = Math.max(0, data.length - 1024);
        String tail = new String(data, start, data.length - start, StandardCharsets.UTF_8);
        return tail.contains("%%EOF");
    }

    private void setPrintService(String selectedPrinter, PrintService printService, PrinterJob printerJob) {
        try {
            printerJob.setPrintService(printService);
        } catch (PrinterException e) {
            log.error("Failed to set print service for printer {}: {}", selectedPrinter, e.getMessage(), e);
        }
    }
}
