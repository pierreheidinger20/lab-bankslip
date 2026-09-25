package com.lab.bankslip.infrastructure.service.pdf;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.lab.bankslip.domain.model.BankSlip;
import com.lab.bankslip.domain.service.BankSlipPdfGenerator;
import com.lab.bankslip.infrastructure.service.pdf.dto.BankSlipPdfDto;
import com.lab.bankslip.infrastructure.service.pdf.mapper.BankSlipPdfMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class OpenHtmlBankSlipPdfGenerator
        implements BankSlipPdfGenerator {

    private final TemplateEngine templateEngine;
    private final BankSlipPdfMapper mapper;

    @Override
    public String generateBase64(BankSlip bankSlip) {

        try {

            String barcodeImage =
                    generateBarcodeImage(bankSlip.getBarcode());
            BankSlipPdfDto dto = mapper.toDto(bankSlip);

            Context context = new Context();

            context.setVariable("bankSlip", dto);
            context.setVariable("barcodeImage", barcodeImage);

            String html =
                    templateEngine.process(
                            "lab-bank-slip",
                            context
                    );

            byte[] pdf = generatePdf(html);

            return Base64.getEncoder()
                    .encodeToString(pdf);

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Could not generate bank slip PDF",
                    exception
            );
        }
    }

    private byte[] generatePdf(String html)
            throws IOException {

        try (ByteArrayOutputStream output =
                     new ByteArrayOutputStream()) {

            PdfRendererBuilder builder =
                    new PdfRendererBuilder();

            builder
                    .withHtmlContent(html, null)
                    .toStream(output)
                    .useFastMode();

            builder.run();

            return output.toByteArray();
        }
    }

    private String generateBarcodeImage(
            String barcode
    ) throws WriterException, IOException {

        if (barcode == null || barcode.isBlank()) {
            throw new IllegalArgumentException(
                    "Barcode is required to generate PDF"
            );
        }

        Code128Writer writer =
                new Code128Writer();

        BitMatrix matrix =
                writer.encode(
                        barcode,
                        BarcodeFormat.CODE_128,
                        600,
                        100
                );

        try (ByteArrayOutputStream output =
                     new ByteArrayOutputStream()) {

            MatrixToImageWriter.writeToStream(
                    matrix,
                    "PNG",
                    output
            );

            String base64 =
                    Base64.getEncoder()
                            .encodeToString(
                                    output.toByteArray()
                            );

            return "data:image/png;base64," + base64;
        }
    }
}