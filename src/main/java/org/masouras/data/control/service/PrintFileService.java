package org.masouras.data.control.service;

import com.google.common.base.Preconditions;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
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
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;


@Service
@Slf4j
public class PrintFileService {

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
    private void setPrintService(String selectedPrinter, PrintService printService, PrinterJob printerJob) {
        try {
            printerJob.setPrintService(printService);
        } catch (PrinterException e) {
            log.error("Failed to set print service for printer {}: {}", selectedPrinter, e.getMessage(), e);
        }
    }

    public void printToMicrosoftPdf(byte[] pdfBytes, String outputPath) {
        Preconditions.checkArgument(StringUtils.isNotBlank(outputPath));

        try (PDDocument pdDocument = Loader.loadPDF(pdfBytes)) {
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            printerJob.setJobName("Print To Microsoft Pdf");
            printerJob.setPageable(new PDFPageable(pdDocument));

            // Find Microsoft Print to PDF
            PrintService[] services = PrinterJob.lookupPrintServices();
            for (PrintService ps : services) {
                if (ps.getName().equalsIgnoreCase("Microsoft Print to PDF")) {
                    printerJob.setPrintService(ps);
                    break;
                }
            }

            // Auto-save to a specific path
            PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
            attr.add(new Destination(Paths.get(outputPath).toUri()));

            printerJob.print(attr);
        } catch (Exception e) {
            log.error("printToMicrosoftPdf failed with message {}", e.getMessage(), e);
        }
    }
}
