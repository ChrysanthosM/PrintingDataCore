package org.masouras.control.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import javax.print.PrintService;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class PrintFileService {

    public List<String> getAvailablePrinters() {
        return Arrays.stream(PrinterJob.lookupPrintServices())
                .map(PrintService::getName)
                .sorted()
                .toList();
    }

    public void printPdf(String printingID, byte[] pdfBytes, @Nullable String selectedPrinter) {
        printPdf(printingID, pdfBytes, selectedPrinter, null);
    }
    public void printPdf(String printingID, byte[] pdfBytes, @Nullable String selectedPrinter, @Nullable String outputPath) {
        Validate.notBlank(printingID);
        Validate.isTrue(ArrayUtils.isNotEmpty(pdfBytes), "PDF byte array cannot be empty");
        Validate.isTrue(isLikelyPdf(pdfBytes), "Provided byte array does not appear to be a valid PDF");
        if (log.isInfoEnabled()) log.info("Starting printPdf for printingID {} with PDF byte array of size {}", printingID, pdfBytes.length);
        printOrExportPdf(printingID, pdfBytes, selectedPrinter, outputPath);
    }

    public void printOrExportPdf(String printingID, byte[] pdfBytes, @Nullable String selectedPrinter, @Nullable String outputPath) {
        try (PDDocument pdDocument = Loader.loadPDF(pdfBytes)) {
            if (StringUtils.isNotBlank(outputPath)) {
                exportPdf(printingID, outputPath, pdDocument);
                return;
            }

            PrinterJob printerJob = PrinterJob.getPrinterJob();
            printerJob.setJobName("Print pdDocument " + printingID);
            printerJob.setPageable(new PDFPageable(pdDocument));
            if (StringUtils.isNotBlank(selectedPrinter)) {
                Arrays.stream(PrinterJob.lookupPrintServices())
                        .filter(ps -> ps.getName().equalsIgnoreCase(selectedPrinter))
                        .findFirst()
                        .ifPresent(ps -> setPrintService(selectedPrinter, ps, printerJob));
            }
            printerJob.print();
        } catch (Exception e) {
            log.error("printOrExportPdf failed: {}", e.getMessage(), e);
        }
    }
    private void exportPdf(String printingID, String outputPath, PDDocument pdDocument) throws IOException {
        Path outputFile = Paths.get(outputPath, printingID + ".pdf");
        pdDocument.save(outputFile.toFile());
        if (log.isInfoEnabled()) log.info("PDF exported to {}", outputFile);
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
