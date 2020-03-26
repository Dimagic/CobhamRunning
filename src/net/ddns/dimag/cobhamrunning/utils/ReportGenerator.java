package net.ddns.dimag.cobhamrunning.utils;

import javafx.collections.ObservableList;
import net.ddns.dimag.cobhamrunning.models.ShippingSystem;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class ReportGenerator {
    private final SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
    private final LocalDate dateFrom;
    private final LocalDate dateTo;
    private final String filter;
    private final ObservableList<ShippingSystem> reportData;
    private Map<String, CellStyle> styles;
    private Cell cell;


    public ReportGenerator(LocalDate dateFrom, LocalDate dateTo, String filter, ObservableList<ShippingSystem> reportData) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.filter = filter;
        this.reportData = reportData;
    }

    public boolean shippingSystemReportToExcell() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        String TITLE = "Shipped systems report";
        String reportFolder = "./Reports/";
        String fileName = "ShippedSystemsReport.xlsx";
        XSSFSheet sheet = workbook.createSheet(TITLE);
        styles = createStyles(workbook);

        String[] titles = {"Date", "Article", "Rev.", "ASIS", "SN", "Common ver.", "System ver.", "Target ver."};

        /**
         * create the report head
         */
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, titles.length-1));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, titles.length-1));
        Row reportNameRow = sheet.createRow(0);
        cell = reportNameRow.createCell(0);
        cell.setCellValue(TITLE);
        cell.setCellStyle(styles.get("reportHeader"));
        Row reportPeriodRow = sheet.createRow(1);
        cell = reportPeriodRow.createCell(0);
        cell.setCellValue(String.format("from %s to %s", dateFrom, dateTo));
        cell.setCellStyle(styles.get("reportHeader"));


        /**
         * create the header row
         */
        int headerRowNum = 3;
        Row headerRow = sheet.createRow(headerRowNum);
        headerRow.setHeightInPoints(12.75f);
        for (int i = 0; i < titles.length; i++) {
            cell = headerRow.createCell(i);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(styles.get("tableTitle"));
        }

        /**
         * filling report data
         */
        int dataRowNum = headerRowNum;
        Row dataRow;
        String currArticle;
        Map<String, Integer> summaryData = new HashMap<String, Integer>();
        for (ShippingSystem shippingSystem : reportData) {
            dataRowNum += 1;
            dataRow = sheet.createRow(dataRowNum);
            setDataCell(dataRow, "dataCell_center", fmt.format(shippingSystem.getDateShip()));
            setDataCell(dataRow, "dataCell_center", shippingSystem.getDevice().getAsis().getArticleHeaders().getName());
            setDataCell(dataRow, "dataCell_center", shippingSystem.getDevice().getAsis().getArticleHeaders().getRevision());
            setDataCell(dataRow, "dataCell_center", shippingSystem.getDevice().getAsis().getAsis());
            setDataCell(dataRow, "dataCell_center", shippingSystem.getDevice().getSn());
            setDataCell(dataRow, "dataCell_center", shippingSystem.getDevice().getDeviceInfo().getCommonVer());
            setDataCell(dataRow, "dataCell_center", shippingSystem.getDevice().getDeviceInfo().getSystemVer());
            setDataCell(dataRow, "dataCell_center", shippingSystem.getDevice().getDeviceInfo().getTargetVer());

            currArticle = shippingSystem.getDevice().getAsis().getArticleHeaders().getArticle();
            if (summaryData.get(currArticle) == null){
                summaryData.put(currArticle, 1);
            } else {
                summaryData.put(currArticle, summaryData.get(currArticle) + 1);
            }
        }

        /**
         * filling report statistic by article
         */
        int lastRow = sheet.getLastRowNum() + 2;
        dataRow = sheet.createRow(lastRow);
        setDataCell(dataRow, "tableTitle", "Systems statistic");
        setDataCell(dataRow, "tableTitle", "");
        sheet.addMergedRegion(new CellRangeAddress(lastRow, lastRow, 0, 1));
        dataRow = sheet.createRow(lastRow + 1);
        setDataCell(dataRow, "tableTitle", "Article");
        setDataCell(dataRow, "tableTitle", "Count");

        dataRowNum = sheet.getLastRowNum();
        int total = 0;
        List<String> keyList = new ArrayList<String>(summaryData.keySet());
        Collections.sort(keyList);
        for (String key: keyList){
            dataRowNum += 1;
            dataRow = sheet.createRow(dataRowNum);
            setDataCell(dataRow, "dataCell_center", key);
            setDataCell(dataRow, "dataCell_center", Integer.toString(summaryData.get(key)));
            total = total + summaryData.get(key);
        }
        dataRow = sheet.createRow(dataRowNum + 1);
        setDataCell(dataRow, "dataCell_right_bold", "Total:");
        setDataCell(dataRow, "tableTitle", Integer.toString(total));

        for (int j = 0; j < titles.length; j++) {
            sheet.autoSizeColumn(j);
        }

        File f = new File(reportFolder);
        if (!f.exists()){
            if (!f.mkdirs()){
                MsgBox.msgError("Shipping system report", String.format("Cant't create folder: %s", reportFolder));
                return false;
            }
        }

        try (FileOutputStream outputStream = new FileOutputStream(String.format("%s%s", reportFolder, fileName))) {
            workbook.write(outputStream);
            Desktop.getDesktop().open(new File(String.format("%s%s", reportFolder, fileName)));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void setDataCell(Row dataRow, String style, String value){
        int cellNum = dataRow.getFirstCellNum();
        if (cellNum == -1){
            cellNum = 0;
        } else {
            cellNum = dataRow.getLastCellNum();
        }
        cell = dataRow.createCell(cellNum);
        cell.setCellStyle(styles.get(style));
        cell.setCellValue(value);
    }

    /**
     * create a library of cell styles
     */
    private Map<String, CellStyle> createStyles(Workbook wb){
        Map<String, CellStyle> styles = new HashMap<>();
        CellStyle style;
        Font font;

        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFont(font);
        styles.put("reportHeader", style);

        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFont(font);
        styles.put("tableTitle", style);

        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_NORMAL);
        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFont(font);
        styles.put("dataCell_center", style);

        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        style.setFont(font);
        styles.put("dataCell_right_bold", style);

        return styles;
    }

    private CellStyle createBorderedStyle(Workbook wb){
        short thin = 1;
        short black = IndexedColors.BLACK.getIndex();

        CellStyle style = wb.createCellStyle();
        style.setBorderRight(thin);
        style.setRightBorderColor(black);
        style.setBorderBottom(thin);
        style.setBottomBorderColor(black);
        style.setBorderLeft(thin);
        style.setLeftBorderColor(black);
        style.setBorderTop(thin);
        style.setTopBorderColor(black);
        return style;
    }

}

