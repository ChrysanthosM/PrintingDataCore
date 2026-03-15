package org.masouras.control.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.helper.Validate;
import org.jspecify.annotations.NonNull;
import org.masouras.control.converter.XmlConverterService;
import org.masouras.control.render.PdfRendererService;
import org.masouras.control.service.PrintingLetterSetUpService;
import org.masouras.control.service.XslTemplateService;
import org.masouras.domain.FileProcessorResult;
import org.masouras.model.mssql.schema.jpa.control.entity.adapter.domain.LetterToPrintDTO;
import org.masouras.model.mssql.schema.jpa.control.entity.adapter.projection.PrintingLetterSetUpProjectionImplementor;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.*;
import org.masouras.util.ChunkUtil;
import org.masouras.xml.invoices.control.InvoicesControlService;
import org.masouras.xml.invoices.domain.Invoice;
import org.springframework.stereotype.Service;

import javax.xml.transform.Templates;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public non-sealed class FileProcessorXML implements FileProcessor {
    private final PrintingLetterSetUpService printingLetterSetUpService;
    private final PdfRendererService pdfRendererService;
    private final XslTemplateService xslTemplateService;
    private final XmlConverterService xmlSerializerService;
    private final InvoicesControlService invoicesControlService;

    @Override
    public FileExtensionType getFileExtensionType() {
        return FileExtensionType.XML;
    }

    @Override
    public FileProcessorResult getFileProcessorResult(Object... params) {
        Object[] validated = ParserParamsValidator.of(4, params)
                .expect(PrintingWayType.class, ActivityType.class, ContentType.class, byte[].class)
                .validate();
        PrintingWayType printingWayType = (PrintingWayType) validated[0];
        ActivityType activityType = (ActivityType) validated[1];
        ContentType contentType = (ContentType) validated[2];
        byte[] validatedContent = (byte[]) validated[3];
        Validate.isTrue(ArrayUtils.isNotEmpty(validatedContent), "validatedContent must not be null");

        List<PrintingLetterSetUpProjectionImplementor> implementorList = printingLetterSetUpService.getPrintingLetterLookUpMap()
                .getOrDefault(activityType.getCode(), Map.of())
                .getOrDefault(contentType.getCode(), List.of());
        if (CollectionUtils.isEmpty(implementorList)) return FileProcessorResult.error("PrintingLetterSetUp not found for ActivityType: " + activityType + " and ContentType: " + contentType);
        return getFileProcessorResultMain(printingWayType, implementorList, validatedContent);
    }
    private FileProcessorResult getFileProcessorResultMain(PrintingWayType printingWayType, List<PrintingLetterSetUpProjectionImplementor> implementorList,
                                                           byte[] validatedContent) {
        return switch (printingWayType) {
            case ARTEMIS -> getPdfResultListForValidatedContentSimple(implementorList, validatedContent);
            case BATCH -> getPdfResultListForValidatedContentBatch(implementorList, validatedContent);
            default -> FileProcessorResult.error("Unknown PrintingWayType: " + printingWayType);
        };
    }

    private @NonNull FileProcessorResult getPdfResultListForValidatedContentBatch(List<PrintingLetterSetUpProjectionImplementor> implementorList, byte[] validatedContent) {
        List<Invoice> invoicesList = invoicesControlService.getInvoiceList(validatedContent);
        if (CollectionUtils.isEmpty(invoicesList)) return FileProcessorResult.error("No invoices found in validatedContent");
        try (ExecutorService executorService = Executors.newFixedThreadPool(10)) {
            List<Future<List<byte[]>>> futureInvoicesList = invoicesList.stream()
                    .map(invoice -> executorService.submit(() -> getPdfResultListForValidatedContentMain(implementorList, xmlSerializerService.toXmlBytes(invoice))))
                    .toList();
            List<byte[]> pdfResultList = futureInvoicesList.stream().flatMap(this::getListFutureOrEmpty).toList();
            return FileProcessorResult.success(pdfResultList);
        }
    }
    private @NonNull Stream<byte[]> getListFutureOrEmpty(Future<List<byte[]>> listFuture) {
        try {
            return listFuture.get().stream();
        } catch (Exception e) {
            log.error("Error processing invoice in batch: {}", e.getMessage(), e);
            return Stream.empty();
        }
    }

    private @NonNull FileProcessorResult getPdfResultListForValidatedContentSimple(List<PrintingLetterSetUpProjectionImplementor> implementorList, byte[] validatedContent) {
        List<byte[]> pdfResultList = getPdfResultListForValidatedContentMain(implementorList, validatedContent);
        return FileProcessorResult.success(pdfResultList);
    }

    private List<byte[]> getPdfResultListForValidatedContentMain(List<PrintingLetterSetUpProjectionImplementor> implementorList, byte[] validatedContent) {
        return implementorList.stream()
                .filter(implementor -> implementor.getValidFlag() == ValidFlag.ENABLED)
                .map(implementor -> new AbstractMap.SimpleEntry<>(implementor, xslTemplateService.getTemplate(implementor.getXslType().name())))
                .filter(entry -> entry.getValue() != null)
                .peek(this::logEntry)
                .map(entry -> pdfRendererService.generatePdf(
                                entry.getKey().getRendererType(),
                                entry.getValue(),
                                validatedContent)
                )
                .toList();
    }
    private void logEntry(AbstractMap.SimpleEntry<PrintingLetterSetUpProjectionImplementor, Templates> entry) {
        if (log.isInfoEnabled()) {
            log.info("pdfRendererService will generatePdf with ActivityType: {}, ContentType: {}, XslType: {}, RendererType: {}",
                    entry.getKey().getActivityType().name(), entry.getKey().getContentType().name(), entry.getKey().getXslType().name(), entry.getKey().getRendererType().name());
        }
    }
}
