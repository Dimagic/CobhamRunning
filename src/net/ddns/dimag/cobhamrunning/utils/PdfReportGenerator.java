package net.ddns.dimag.cobhamrunning.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.ddns.dimag.cobhamrunning.models.Measurements;
import net.ddns.dimag.cobhamrunning.models.Tests;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Comparator;

public class PdfReportGenerator {
    private String FILE;

    private final Font paragrafFont = new Font(Font.FontFamily.TIMES_ROMAN, 16);
    private final Font tableHeadFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
    private final Font tableFontBlack = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);
    private final Font tableFontGreen = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.GREEN);
    private final Font tableFontRed = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.RED);


    public PdfReportGenerator(Tests currTest) {
        this.FILE = "./Reports/rmv_report.pdf";
        try {
            ObservableList<Measurements> measList = FXCollections.observableArrayList(currTest.getMeas());
            FXCollections.sort(measList, Comparator.comparingInt(Measurements::getMeasureNumber));
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(FILE));
            document.open();

            createHeaderTable(document, currTest);
            createSysInfoTable(document, currTest);
            createMeasureTable(document, measList);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createHeaderTable(Document document, Tests currTest) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 35, 10, 20, 35});
        String header = String.format("RMV Test Report: %s", currTest.getHeadDescription());
        PdfPCell c = getTableCell(header, 0, tableHeadFont);
        c.setColspan(4);
        c.setMinimumHeight(30);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setVerticalAlignment(Element.ALIGN_RIGHT);
        c.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(c);
        table.setHeaderRows(1);

        table.addCell(getTableCell(String.format("Article Number: %s", currTest.getDevice().getAsis().getArticleHeaders().getName()), 0, tableFontBlack));
        table.addCell(getTableCell(String.format("Rev: %s", currTest.getDevice().getAsis().getArticleHeaders().getRevision()), 0, tableFontBlack));
        table.addCell(getTableCell(String.format("Serial: %s", currTest.getAsis()), 0, tableFontBlack));
        table.addCell(getTableCell(String.format("Date: %s", Utils.getFormattedDate(currTest.getDateTest())), 0, tableFontBlack));
        document.add(table);
        addParagraf(document, String.format("Test Status: %s", currTest.getStringTestStatus()), paragrafFont);
    }

    private void createSysInfoTable(Document document, Tests currTest) throws DocumentException {
        addParagraf(document, "System Information:", paragrafFont);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[] {33, 33, 34});
        table.addCell(getTableCell("Configuration:", 0, tableHeadFont, BaseColor.LIGHT_GRAY));
        table.addCell(getTableCell("User Name:", 0, tableHeadFont, BaseColor.LIGHT_GRAY));
        table.addCell(getTableCell("Test Time:", 0, tableHeadFont, BaseColor.LIGHT_GRAY));
        table.setHeaderRows(1);

        table.addCell(getTableCell(currTest.getName(), 0, tableFontBlack));
        table.addCell(getTableCell(currTest.getUserName(), 0, tableFontBlack));
        table.addCell(getTableCell(currTest.getTestTimeHMSM(), 0, tableFontBlack));
        document.add(table);

        table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[] {33, 33, 34});
        table.addCell(getTableCell("MP Version", 0, tableHeadFont, BaseColor.LIGHT_GRAY));
        table.addCell(getTableCell("MP Version", 0, tableHeadFont, BaseColor.LIGHT_GRAY));
        table.addCell(getTableCell("Computer Name:", 0, tableHeadFont, BaseColor.LIGHT_GRAY));
        table.setHeaderRows(1);

        table.addCell(getTableCell("", 0, tableFontBlack));
        table.addCell(getTableCell("", 0, tableFontBlack));
        table.addCell(getTableCell(currTest.getComputerName(), 0, tableFontBlack));
        document.add(table);
    }

    private void createMeasureTable(Document document, ObservableList<Measurements> measList ) throws DocumentException {
        addParagraf(document, "Measure Information:", paragrafFont);
        ArrayList<String> columnNameList = new ArrayList<>();
        columnNameList.add("Description");
        columnNameList.add("Min");
        columnNameList.add("Result");
        columnNameList.add("Max");
        columnNameList.add("Status");

        PdfPTable table = new PdfPTable(columnNameList.size());
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 45, 14, 19, 14, 8});
        columnNameList.forEach(val -> {
            PdfPCell c = new PdfPCell(new Phrase(val, tableHeadFont));
            c.setHorizontalAlignment(Element.ALIGN_CENTER);
            c.setVerticalAlignment(Element.ALIGN_CENTER);
            c.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(c);
        });
        table.setHeaderRows(1);

        measList.forEach(c -> {
            table.addCell(getTableCell(c.getMeasName(), 0, tableFontBlack));
            table.addCell(getTableCell(c.getMeasMin(), 1, tableFontBlack));
            table.addCell(getTableCell(c.getMeasVal(), 1, tableFontBlack));
            table.addCell(getTableCell(c.getMeasMax(), 1, tableFontBlack));
            table.addCell(getTableCell(c.getStringMeasStatus(), 1,
                    c.getMeasStatus() == 0 ? tableFontGreen: tableFontRed));
        });
        document.add(table);
        addEmptyLine(document, 1);
    }

    private PdfPCell getTableCell(String val, int align, Font font){
        PdfPCell c = new PdfPCell(new Phrase(val, font));
        c.setHorizontalAlignment(align);
        c.setVerticalAlignment(Element.ALIGN_CENTER);
        c.setMinimumHeight(20);
        return c;
    }

    private PdfPCell getTableCell(String val, int align, Font font, BaseColor backColor){
        PdfPCell c = getTableCell(val, align, font);
        c.setBackgroundColor(backColor);
        return c;
    }

    private void addParagraf(Document document, String val, Font font) throws DocumentException {
        Paragraph paragraph = new Paragraph(val, font);
        paragraph.setSpacingAfter(font.getSize()/2);
        document.add(paragraph);
    }

    private void addEmptyLine(Document document, int count) throws DocumentException {
        for (int i = 0; i < count; i++){
            document.add(new Phrase("\n"));
        }
    }

    public String getFILE() {
        return FILE;
    }
}
