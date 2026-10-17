package com.clearbillsolutions.service;

import com.clearbillsolutions.dto.BusinessSettingsDTO;
import com.clearbillsolutions.dto.InvoiceDTO;
import com.lowagie.text.pdf.BaseFont;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

@Service
public class PdfService {

    private static final Logger log = LoggerFactory.getLogger(PdfService.class);

    private final ITemplateEngine templateEngine;

    private static final Set<String> VALID_TEMPLATES = Set.of(
            "red", "blue", "green", "orange", "black"
    );

    private String extractedRegularFontPath = null;
    private String extractedBoldFontPath = null;
    private String extractedHindiFontPath = null;

    public PdfService(ITemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * Resolves template name to one of the 5 templates (red, blue, green, orange, black).
     * Falls back to "red" if template is unknown or from legacy version.
     */
    public String resolveTemplate(String templateName) {
        if (templateName == null || templateName.trim().isEmpty()) {
            return "red";
        }
        String clean = templateName.trim().toLowerCase().replace("_", "-");
        return VALID_TEMPLATES.contains(clean) ? clean : "red";
    }

    /**
     * Renders the Thymeleaf template into well-formed XHTML.
     */
    public String renderInvoiceHtml(InvoiceDTO invoice, BusinessSettingsDTO settings, String templateName) {
        String resolvedTemplate = resolveTemplate(templateName != null ? templateName : invoice.getTemplateName());

        Context context = new Context();
        context.setVariable("invoice", invoice);
        context.setVariable("business", settings);

        // Process the Thymeleaf template from src/main/resources/templates/invoices/{name}.html
        String rawHtml = templateEngine.process("invoices/" + resolvedTemplate, context);

        // Convert HTML to strictly compliant XHTML using Jsoup for Flying Saucer
        Document document = Jsoup.parse(rawHtml);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        document.outputSettings().charset("UTF-8");

        return document.html();
    }

    /**
     * Generates a PDF byte array from well-formed XHTML using Flying Saucer OpenPDF.
     * Registers FreeSans font to ensure Rupee symbol (₹) and Indian text glyphs render cleanly.
     */
    public byte[] generatePdf(InvoiceDTO invoice, BusinessSettingsDTO settings, String templateName) {
        String xhtml = renderInvoiceHtml(invoice, settings, templateName);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();

            // Register FreeSans fonts for Rupee (₹) symbol support
            registerFonts(renderer);

            renderer.setDocumentFromString(xhtml);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF for invoice {}: {}", invoice.getInvoiceNumber(), e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }

    private synchronized void registerFonts(ITextRenderer renderer) {
        try {
            if (extractedRegularFontPath == null) {
                extractedRegularFontPath = getFontFilePath("fonts/FreeSans.ttf", "FreeSans.ttf");
            }
            if (extractedRegularFontPath != null) {
                renderer.getFontResolver().addFont(extractedRegularFontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            }

            if (extractedBoldFontPath == null) {
                extractedBoldFontPath = getFontFilePath("fonts/FreeSansBold.ttf", "FreeSansBold.ttf");
            }
            if (extractedBoldFontPath != null) {
                renderer.getFontResolver().addFont(extractedBoldFontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            }

            if (extractedHindiFontPath == null) {
                extractedHindiFontPath = getFontFilePath("fonts/DroidSansDevanagari.ttf", "DroidSansDevanagari.ttf");
            }
            if (extractedHindiFontPath != null) {
                renderer.getFontResolver().addFont(extractedHindiFontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            }
        } catch (Exception e) {
            log.warn("Could not register custom font FreeSans for PDF: {}", e.getMessage());
        }
    }

    private String getFontFilePath(String resourcePath, String fileName) {
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);
            if (!resource.exists()) {
                return null;
            }

            try {
                // If running unpacked from filesystem
                File file = resource.getFile();
                if (file.exists()) {
                    return file.getAbsolutePath();
                }
            } catch (Exception ignored) {
                // Inside jar: extract to temp file
            }

            File tempFont = new File(System.getProperty("java.io.tmpdir"), "clearbill-" + fileName);
            if (!tempFont.exists() || tempFont.length() != resource.contentLength()) {
                try (InputStream in = resource.getInputStream();
                     FileOutputStream out = new FileOutputStream(tempFont)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
            }
            return tempFont.getAbsolutePath();
        } catch (Exception e) {
            log.warn("Failed to extract font {}: {}", fileName, e.getMessage());
            return null;
        }
    }
}
