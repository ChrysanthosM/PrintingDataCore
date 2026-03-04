package org.masouras.data.control.service;

import com.google.common.base.Preconditions;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.springframework.stereotype.Service;

import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Destination;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;


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
        Preconditions.checkArgument(StringUtils.isNotBlank(printingID));
        Preconditions.checkArgument(ArrayUtils.isNotEmpty(pdfBytes), "PDF byte array cannot be empty");
        Preconditions.checkArgument(isLikelyPdf(pdfBytes), "Provided byte array does not appear to be a valid PDF");
        if (log.isInfoEnabled()) log.info("Starting printPdf for printingID {} with PDF byte array of size {}", printingID, pdfBytes.length);
        printPdfMain(printingID, pdfBytes, selectedPrinter, outputPath);
    }
    public void printPdfMain(String printingID, byte[] pdfBytes, @Nullable String selectedPrinter, @Nullable String outputPath) {
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
                attr.add(new Destination(Paths.get(outputPath, printingID + ".pdf").toUri()));
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
