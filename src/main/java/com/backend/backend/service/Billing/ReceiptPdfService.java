package com.backend.backend.service.Billing;

import com.backend.backend.entity.patient.Appointment;
import com.backend.backend.entity.patient.AppointmentBilling;
import com.backend.backend.entity.patient.Patient;
import com.backend.backend.entity.practice.Cabinet;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

/**
 * Service for generating PDF receipts for appointment billings.
 * Billing is per appointment, not per consultation.
 */
@Service
public class ReceiptPdfService {

    @Value("${app.receipts.path:uploads/receipts}")
    private String receiptsPath;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(41, 128, 185);

    /**
     * Generates a PDF receipt for an appointment billing.
     *
     * @param billing The billing entity with all related data loaded
     * @return The path to the generated PDF file
     */
    public String generateReceipt(AppointmentBilling billing) throws IOException {
        // Ensure directory exists
        Path directory = Paths.get(receiptsPath);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        // Generate file path
        String fileName = "receipt_" + billing.getReceiptNumber() + ".pdf";
        Path filePath = directory.resolve(fileName);

        // Get related entities
        Appointment appointment = billing.getAppointment();
        Patient patient = appointment.getPatient();
        Cabinet cabinet = appointment.getCabinet();

        // Create PDF
        try (PdfWriter writer = new PdfWriter(filePath.toString());
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Header - Cabinet info
            addHeader(document, cabinet);

            // Receipt title and number
            addReceiptTitle(document, billing);

            // Patient and Appointment info
            addPatientInfo(document, patient, appointment);

            // Payment details table
            addPaymentDetails(document, billing);

            // Footer
            addFooter(document, billing);
        }

        return filePath.toString();
    }

    private void addHeader(Document document, Cabinet cabinet) {
        Paragraph cabinetName = new Paragraph(cabinet.getName())
                .setFontSize(20)
                .setBold()
                .setFontColor(HEADER_COLOR)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(cabinetName);

        if (cabinet.getAddress() != null) {
            Paragraph address = new Paragraph(cabinet.getAddress())
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(address);
        }

        if (cabinet.getPhone() != null) {
            Paragraph phone = new Paragraph("Tél: " + cabinet.getPhone())
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(phone);
        }

        document.add(new Paragraph("\n"));
    }

    private void addReceiptTitle(Document document, AppointmentBilling billing) {
        Paragraph title = new Paragraph("REÇU DE PAIEMENT")
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setBackgroundColor(new DeviceRgb(240, 240, 240))
                .setPadding(10);
        document.add(title);

        Paragraph receiptNumber = new Paragraph("N° " + billing.getReceiptNumber())
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(receiptNumber);

        Paragraph date = new Paragraph("Date: " + billing.getPaymentDate().format(DATE_FORMATTER))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(date);

        document.add(new Paragraph("\n"));
    }

    private void addPatientInfo(Document document, Patient patient, Appointment appointment) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth();

        // Patient info
        Cell patientHeader = new Cell()
                .add(new Paragraph("PATIENT").setBold())
                .setBorder(Border.NO_BORDER)
                .setBackgroundColor(new DeviceRgb(230, 230, 230));
        table.addCell(patientHeader);

        Cell appointmentHeader = new Cell()
                .add(new Paragraph("RENDEZ-VOUS").setBold())
                .setBorder(Border.NO_BORDER)
                .setBackgroundColor(new DeviceRgb(230, 230, 230));
        table.addCell(appointmentHeader);

        Cell patientName = new Cell()
                .add(new Paragraph("Nom: " + patient.getFirstName() + " " + patient.getLastName()))
                .setBorder(Border.NO_BORDER);
        table.addCell(patientName);

        Cell appointmentDate = new Cell()
                .add(new Paragraph("Date: " + appointment.getAppointmentDateTime().format(DATE_FORMATTER)))
                .setBorder(Border.NO_BORDER);
        table.addCell(appointmentDate);

        if (patient.getCin() != null) {
            Cell patientCin = new Cell()
                    .add(new Paragraph("CIN: " + patient.getCin()))
                    .setBorder(Border.NO_BORDER);
            table.addCell(patientCin);
        } else {
            table.addCell(new Cell().setBorder(Border.NO_BORDER));
        }

        Cell doctorName = new Cell()
                .add(new Paragraph("Médecin: Dr. " + appointment.getDoctor().getFullName()))
                .setBorder(Border.NO_BORDER);
        table.addCell(doctorName);

        document.add(table);
        document.add(new Paragraph("\n"));
    }

    private void addPaymentDetails(Document document, AppointmentBilling billing) {
        Paragraph detailsTitle = new Paragraph("DÉTAILS DU PAIEMENT")
                .setBold()
                .setFontSize(12)
                .setBackgroundColor(new DeviceRgb(230, 230, 230))
                .setPadding(5);
        document.add(detailsTitle);

        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1}))
                .useAllAvailableWidth();

        // Original price
        table.addCell(createCell("Prix du rendez-vous:", false));
        table.addCell(createCell(formatPrice(billing.getOriginalPrice()) + " MAD", true));

        // Discount if applicable
        if (billing.getDiscountAmount() != null && billing.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            String discountText = "Réduction";
            if (billing.getDiscountReason() != null && !billing.getDiscountReason().isEmpty()) {
                discountText += " (" + billing.getDiscountReason() + ")";
            }
            table.addCell(createCell(discountText + ":", false));
            table.addCell(createCell("- " + formatPrice(billing.getDiscountAmount()) + " MAD", true)
                    .setFontColor(new DeviceRgb(231, 76, 60)));
        }

        // Separator
        Cell separator = new Cell(1, 2)
                .add(new Paragraph(""))
                .setBorderBottom(new com.itextpdf.layout.borders.SolidBorder(1));
        table.addCell(separator);

        // Final amount
        Cell totalLabel = createCell("TOTAL À PAYER:", false)
                .setBold()
                .setFontSize(14);
        table.addCell(totalLabel);

        Cell totalAmount = createCell(formatPrice(billing.getFinalAmount()) + " MAD", true)
                .setBold()
                .setFontSize(14)
                .setFontColor(HEADER_COLOR);
        table.addCell(totalAmount);

        // Payment type
        table.addCell(createCell("Mode de paiement:", false));
        table.addCell(createCell(translatePaymentType(billing.getPaymentType().name()), true));

        // Payment status
        table.addCell(createCell("Statut:", false));
        Cell statusCell = createCell(translatePaymentStatus(billing.getPaymentStatus().name()), true);
        if ("PAID".equals(billing.getPaymentStatus().name())) {
            statusCell.setFontColor(new DeviceRgb(39, 174, 96));
        }
        table.addCell(statusCell);

        document.add(table);
        document.add(new Paragraph("\n"));
    }

    private void addFooter(Document document, AppointmentBilling billing) {
        Paragraph processedBy = new Paragraph("Traité par: " + billing.getProcessedBy().getFullName())
                .setFontSize(9)
                .setFontColor(ColorConstants.GRAY);
        document.add(processedBy);

        if (billing.getNotes() != null && !billing.getNotes().isEmpty()) {
            Paragraph notes = new Paragraph("Notes: " + billing.getNotes())
                    .setFontSize(9)
                    .setFontColor(ColorConstants.GRAY);
            document.add(notes);
        }

        document.add(new Paragraph("\n\n"));

        Paragraph thankYou = new Paragraph("Merci de votre confiance!")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10)
                .setItalic();
        document.add(thankYou);
    }

    private Cell createCell(String text, boolean alignRight) {
        Cell cell = new Cell()
                .add(new Paragraph(text))
                .setBorder(Border.NO_BORDER)
                .setPadding(5);
        if (alignRight) {
            cell.setTextAlignment(TextAlignment.RIGHT);
        }
        return cell;
    }

    private String formatPrice(BigDecimal price) {
        if (price == null) return "0.00";
        return String.format("%.2f", price);
    }

    private String translatePaymentType(String type) {
        return switch (type) {
            case "CASH" -> "Espèces";
            case "CREDIT_CARD" -> "Carte bancaire";
            case "BANK_TRANSFER" -> "Virement bancaire";
            case "CHECK" -> "Chèque";
            case "ONLINE" -> "Paiement en ligne";
            default -> type;
        };
    }

    private String translatePaymentStatus(String status) {
        return switch (status) {
            case "PAID" -> "Payé";
            case "PENDING" -> "En attente";
            case "PARTIALLY_PAID" -> "Partiellement payé";
            case "OVERDUE" -> "En retard";
            case "CANCELLED" -> "Annulé";
            default -> status;
        };
    }
}

