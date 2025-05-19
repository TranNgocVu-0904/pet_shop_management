package service;

import model.Bill;
import model.BillItem;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfGenerator {

    public static void generateBillPdf(Bill bill, String filePath) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            document.add(new Paragraph("🐾 Pet Shop Invoice 🐾"));
            document.add(new Paragraph("Transaction Time: " + bill.getTransactionTime().format(formatter)));
            document.add(new Paragraph("Staff ID: " + bill.getStaffId()));
            document.add(new Paragraph("Customer ID: " + bill.getCustomerId()));
            document.add(new Paragraph("Payment Method: " + bill.getPaymentMethod()));
            document.add(new Paragraph("\nItems Purchased:"));

            for (BillItem item : bill.getItems()) {
                String line = String.format(
                        "[%s] %s x%d - $%.2f",
                        item.getItemType(),
                        item.getItemName(),
                        item.getQuantity(),
                        item.getTotal()
                );
                Paragraph p = new Paragraph(line);
                p.setAlignment(Element.ALIGN_LEFT);
                document.add(p);
            }

            document.add(new Paragraph("\nTotal Amount: $" + bill.getTotalAmount()));
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
