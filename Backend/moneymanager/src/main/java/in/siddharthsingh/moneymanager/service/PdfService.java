package in.siddharthsingh.moneymanager.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import in.siddharthsingh.moneymanager.dto.ExpenseDTO;
import in.siddharthsingh.moneymanager.dto.IncomeDTO;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.OutputStream;
import java.util.List;

@Service
public class PdfService {

    public void writeIncomesToPdf(OutputStream os, List<IncomeDTO> incomes) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, os);
        document.open();

        // Title
        Paragraph title = new Paragraph("Income Report",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" ")); // Blank line

        // Table
        PdfPTable table = getPdfPTable(incomes);

        document.add(table);
        document.close();
    }

    private static PdfPTable getPdfPTable(List<IncomeDTO> incomes) {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.addCell("S.No");
        table.addCell("Name");
        table.addCell("Category");
        table.addCell("Amount");
        table.addCell("Date");

        int index = 1;
        for (IncomeDTO income : incomes) {
            table.addCell(String.valueOf(index++));
            table.addCell(income.getName() != null ? income.getName() : "N/A");
            table.addCell(income.getCategoryName() != null ? income.getCategoryName() : "N/A");
            table.addCell(income.getAmount() != null ? income.getAmount().toString() : "0");
            table.addCell(income.getDate() != null ? income.getDate().toString() : "N/A");
        }
        return table;
    }

    public void writeExpensesToPdf(OutputStream os, List<ExpenseDTO> expenses) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, os);
        document.open();

        Paragraph title = new Paragraph("Expense Report",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.addCell("S.No");
        table.addCell("Name");
        table.addCell("Category");
        table.addCell("Amount");
        table.addCell("Date");

        int index = 1;
        for (ExpenseDTO expense : expenses) {
            table.addCell(String.valueOf(index++));
            table.addCell(expense.getName() != null ? expense.getName() : "N/A");
            table.addCell(expense.getCategoryName() != null ? expense.getCategoryName() : "N/A");
            table.addCell(expense.getAmount() != null ? expense.getAmount().toString() : "0");
            table.addCell(expense.getDate() != null ? expense.getDate().toString() : "N/A");
        }

        document.add(table);
        document.close();
    }
}
