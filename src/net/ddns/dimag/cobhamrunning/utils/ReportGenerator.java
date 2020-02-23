package net.ddns.dimag.cobhamrunning.utils;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Date;

public class ReportGenerator {
    public static final String DEST = "./cell_method.pdf";
    public static final String LOGO = "./resources/images/cobham_logo.png";


    public static void main(String[] args) throws Exception {


        new ReportGenerator().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException {
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        try (Document document = new Document(pdf)) {
            Table table = new Table(3);
            Cell cell = new Cell(1, 3)
                    .setTextAlignment(TextAlignment.CENTER)
                    .add(new Paragraph("Cell with colspan 3"));
            table.addCell(cell);
            cell = new Cell(2, 1)
                    .add(new Paragraph("Cell with rowspan 2"))
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);
            table.addCell(cell);
            table.addCell("Cell 1.1");
            table.addCell(new Cell().add(new Paragraph("Cell 1.2")));
            table.addCell(new Cell()
                    .add(new Paragraph("Cell 2.1")));
//                    .setBackgroundColor(Color.LIGHT_GRAY)
//                    .setMargin(5));
            table.addCell(new Cell()
                    .add(new Paragraph("Cell 1.2")));
//                    .setBackgroundColor(Color.LIGHT_GRAY)
//                    .setPadding(5));
            document.add(table);
        }
    }

    private Table getHeader(){
        Cell cell;
        Table table = new Table(3);
        cell = new Cell(3,1);
//        .add(new Image(LOGO));
        return table;
    }
}

