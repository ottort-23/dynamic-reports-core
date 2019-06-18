/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plustech.reportscore.impl;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Page;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import com.plustech.reportscore.service.JsonToReportService;
import com.plustech.reportscore.utils.Constants;
import com.plustech.reportscore.utils.ReportPage;
import java.awt.Color;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperExportManager;

/**
 *
 * @author ottor
 */
@Component
public class jsonToReportImpl implements JsonToReportService {

    @Override
    //@SuppressWarnings("empty-statement")
    public HttpServletResponse convertJsonToPdf(String json, HttpServletResponse response) throws Exception {
       // HttpServletResponse response = null;
        JSONObject jsonObj = new JSONObject(json);

        String jsonData = (jsonObj.isNull("data") ? "{}" : jsonObj.get("data").toString());
        String titulo = (jsonObj.isNull("title") ? "{{Non Title}}" : jsonObj.get("title").toString());
        String pageSize = (jsonObj.isNull("pageSize") ? "Letter" : jsonObj.get("pageSize").toString());
        String pageOrientation = (jsonObj.isNull("pageOrientation") ? "Portrait" : jsonObj.get("pageOrientation").toString());
        String tableStriped = (jsonObj.isNull("tableStriped") ? "false" : jsonObj.get("tableStriped").toString());
        JSONArray columns = (jsonObj.isNull("columns") ? new JSONArray() : jsonObj.getJSONArray("columns"));
        JSONArray data = (jsonObj.isNull("data") ? new JSONArray() : jsonObj.getJSONArray("data"));

        FastReportBuilder drb = new FastReportBuilder();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("titulo", titulo);

        int countColumn = 1;

        //Cell styling
        Style cellStyle = new Style("cellParent");
        cellStyle.setBackgroundColor(Color.WHITE);
        cellStyle.setTransparency(Transparency.OPAQUE);
        cellStyle.setBorderTop(Border.THIN());
        cellStyle.setBorderBottom(Border.THIN());
        cellStyle.setBorderLeft(Border.THIN());
        cellStyle.setBorderRight(Border.THIN());
        cellStyle.setHorizontalAlign(HorizontalAlign.LEFT);
        cellStyle.setVerticalAlign(VerticalAlign.TOP);

        //Filling columns
        for (Object o : columns) {
            if (o instanceof JSONObject) {
                int size = 0;
                for (Object d : data) {
                    if (d instanceof JSONObject) {
                        String fieldSize = ((JSONObject) d).get("Field_" + Integer.toString(countColumn)).toString();
                        size = size + fieldSize.length();
                    }
                }

                for (Object o2 : columns) {
                    String fieldNumber = ("Field_" + Integer.toString(countColumn));
                    if (size < 5) {
                        size = 10;
                    };
                    if (((JSONObject) o2).getString("columnName").equals(fieldNumber)) {
                        try {
                            drb.addColumn(((JSONObject) o2).getString("description"), "Field_" + Integer.toString(countColumn), String.class.getName(), size, cellStyle);
                        } catch (ColumnBuilderException | ClassNotFoundException ex) {
                            Logger.getLogger(jsonToReportImpl.class.getName()).log(Level.SEVERE, null, ex);
                            throw new RuntimeException( "Error: Filling columns - fields data error: ", ex);
                        }
                    }
                }

                countColumn++;
            }
        }

        Boolean striped;
        if (tableStriped.equals("true")) {
            striped = true;
        } else {
            striped = false;
        }
        //Cell styling
        Style cellStyleParent = new Style("cellParent");
        cellStyleParent.setBackgroundColor(Color.WHITE);
        cellStyleParent.setTransparency(Transparency.OPAQUE);

        //Header styling
        Style headerStyleParent = new Style("headerParent");
        headerStyleParent.setBackgroundColor(Color.decode("#b3e0ff"));
        headerStyleParent.setTransparency(Transparency.OPAQUE);

        //Title styling
        Style titleStyleParent = new Style("titleParent");
        titleStyleParent.setFont(Font.ARIAL_BIG);

        //Page size configuration
        ReportPage page = Constants.getPageDimensions(pageSize, pageOrientation);

        Style groupVariables = new Style("groupVariables");
        groupVariables.setFont(Font.ARIAL_MEDIUM_BOLD);
        groupVariables.setTextColor(Color.BLUE);
        groupVariables.setBorderBottom(Border.THIN());
        groupVariables.setHorizontalAlign(HorizontalAlign.RIGHT);
        groupVariables.setVerticalAlign(VerticalAlign.BOTTOM);

        //Report print configuration
        DynamicReport dr = drb
                //.addGroups(2)
                .setTitle(titulo)
                .setSubtitle("")
                .setPrintBackgroundOnOddRows(striped)
                .setDefaultStyles(titleStyleParent, null, headerStyleParent, cellStyleParent)
                .setUseFullPageWidth(true)
                .setPageSizeAndOrientation(new Page(page.getHeight(), page.getWidth())) //
                .setReportName("ExportPDF")
                .addAutoText("Este reporte fue genereado el: " + new Date().toGMTString(), AutoText.POSITION_FOOTER, AutoText.ALIGNMENT_RIGHT, 300)
                .build();

        //Convert json string to byte array.
        ByteArrayInputStream jsonDataStream = null;
        try {
            jsonDataStream = new ByteArrayInputStream(jsonData.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(jsonToReportImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Error: jsonDataStream - byte array tranformation data error: ", ex);
            
        }
        //Create json datasource from json stream
        JsonDataSource ds = null;
        try {
            ds = new JsonDataSource(jsonDataStream);
        } catch (JRException ex) {
            Logger.getLogger(jsonToReportImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Error: JsonDataSource - filling data source error: ", ex);
          
        }
        //Finally print the report report
        JasperPrint jp = null;
        try {
            jp = DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(), ds);
        } catch (JRException ex) {
            Logger.getLogger(jsonToReportImpl.class.getName()).log(Level.SEVERE, null, ex);
           throw new RuntimeException("Error: JasperPrint - generate jasper print error: ", ex);
           
        }

        
        //JasperPrint jps = jp;
        //Response configuration
        try {
            if (jp != null) {
                byte[] pdfReport = JasperExportManager
                        .exportReportToPdf(jp);
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=response.pdf");
                response.setHeader("Cache-Control", "no-store");
                response.setHeader("Cache-Control", "private");
                response.setHeader("Pragma", "no-store");
                response.setContentLength(pdfReport.length);
                response.getOutputStream().write(pdfReport);
                response.getOutputStream().flush();
                response.getOutputStream().close();
            }
           
        } catch (JRException ex) {
             throw new RuntimeException("Error: JasperExportManager - export report to pdf error: ", ex);
          
        } catch (IOException ex) {
            Logger.getLogger(jsonToReportImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Error: JasperExportManager - export report to pdf error: ", ex);
            
        }
        

        return response;

    }

    @Override
    public HttpServletResponse convertJsonToXls(String json, HttpServletResponse response)  throws IOException{

        JSONObject jsonObj = new JSONObject(json);
        JSONArray jsonData = jsonObj.getJSONArray("data");
        String titulo = jsonObj.get("title").toString();
        String tableStriped = jsonObj.get("tableStriped").toString();
        JSONArray columns = jsonObj.getJSONArray("columns");
        //String image = jsonObj.get("imageUrl").toString();

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Hoja 1");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);

        //Setting imge configuration, commented by pending funcitonality
//        try {
//            String base64Image = image.split(",")[1];
//            byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
//
//            //InputStream is = new FileInputStream("pic");
//            //byte[] bytes = image.getBytes();
//            int pictureIdx = sheet.getWorkbook().addPicture(imageBytes, Workbook.PICTURE_TYPE_JPEG);
//            //is.close();
//            CreationHelper helper = sheet.getWorkbook().getCreationHelper();
//
//            Drawing drawing = sheet.createDrawingPatriarch();
//
//            ClientAnchor anchor = helper.createClientAnchor();
//            anchor.setAnchorType(AnchorType.MOVE_AND_RESIZE);
//
//            anchor.setCol1(0);
//            anchor.setRow1(0);
//            anchor.setCol2(3);
//            anchor.setRow2(3);
//
//            drawing.createPicture(anchor, pictureIdx);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        //Combining cells
        sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, columns.length() - 1));
        Row title = sheet.createRow(4);
        // Setting the tittle styles
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setFillForegroundColor(IndexedColors.AUTOMATIC.getIndex());
        titleStyle.setFillPattern(FillPatternType.SQUARES);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFFont tittleFont = ((XSSFWorkbook) workbook).createFont();
        tittleFont.setFontName("Arial");
        tittleFont.setFontHeightInPoints((short) 24);
        tittleFont.setBold(true);
        titleStyle.setFont(tittleFont);

        Cell titleCell = title.createCell(0);
        titleCell.setCellValue(titulo);
        titleCell.setCellStyle(titleStyle);

        //Setting heading cells
        Row header = sheet.createRow(6);
        CellStyle headerStyle = workbook.createCellStyle();

        headerStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        headerStyle.setFont(font);

        int countColumn = 0;
        //Filling columns headers
        for (Object o : columns) {
            if (o instanceof JSONObject) {

                for (Object o2 : columns) {
                    String fieldNumber = ("Field_" + Integer.toString(countColumn + 1));
                    if (((JSONObject) o2).getString("columnName").equals(fieldNumber)) {
                        Cell headerCell = header.createCell(countColumn);
                        headerCell.setCellValue(((JSONObject) o2).getString("description"));
                        headerCell.setCellStyle(headerStyle);
                    }
                }
                countColumn++;
            }
        }

        //Next, let’s write the content of the table with a different style:
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        // Table striped atribute verify
        if (tableStriped.equals("true")) {
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
        }

        int cellNumber = 0;
        int rowNumber = 7;

        Map<String, Object> retMap = new HashMap<>();
        for (Object a : jsonData) {
            if (a instanceof JSONObject) {
                int size = ((JSONObject) a).length();
                if (a != JSONObject.NULL) {
                    retMap = toMap((JSONObject) a);
                }
                Row row = sheet.createRow(rowNumber);
                for (int i = 1; i <= size; i++) {
                    Cell cell = row.createCell(cellNumber);
                    cell.setCellValue(retMap.get("Field_" + Integer.toString(i)).toString());
                    cell.setCellStyle(style);
                    sheet.autoSizeColumn(i - 1);
                    cellNumber++;
                }
                cellNumber = 0;
                rowNumber++;
            }
        }
        
       // HttpServletResponse response = null;
        
         // Setup the output 
        String contentType = "application/vnd.ms-excel";
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment; filename=response.xls");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "private");
        response.setHeader("Pragma", "no-store");
        workbook.write(response.getOutputStream());
        

        return response;

    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    @Override
    public JasperPrint convertGroupJsonToPdf(String json) {

        JSONObject jsonObj = new JSONObject(json);
        String jsonData = jsonObj.get("data").toString();
        String titulo = jsonObj.get("title").toString();
        String pageSize = jsonObj.get("pageSize").toString();
        String pageOrientation = jsonObj.get("pageOrientation").toString();
        String tableStriped = jsonObj.get("tableStriped").toString();
        JSONArray columns = jsonObj.getJSONArray("columns");
        JSONArray data = jsonObj.getJSONArray("data");

        FastReportBuilder drb = new FastReportBuilder();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("titulo", titulo);

        int countColumn = 1;

        //Cell styling
        Style cellStyle = new Style("cellParent");
        cellStyle.setBackgroundColor(Color.WHITE);
        cellStyle.setTransparency(Transparency.OPAQUE);
        cellStyle.setBorderTop(Border.THIN());
        cellStyle.setBorderBottom(Border.THIN());
        cellStyle.setBorderLeft(Border.THIN());
        cellStyle.setBorderRight(Border.THIN());
        cellStyle.setHorizontalAlign(HorizontalAlign.LEFT);
        cellStyle.setVerticalAlign(VerticalAlign.TOP);

        //Filling columns
        for (Object o : columns) {
            if (o instanceof JSONObject) {
                int size = 0;
                for (Object d : data) {
                    if (d instanceof JSONObject) {
                        String fieldSize = ((JSONObject) d).get("Field_" + Integer.toString(countColumn)).toString();
                        size = size + fieldSize.length();
                    }
                }

                for (Object o2 : columns) {
                    String fieldNumber = ("Field_" + Integer.toString(countColumn));
                    if (size < 5) {
                        size = 10;
                    };
                    if (((JSONObject) o2).getString("columnName").equals(fieldNumber)) {
                        try {

                            if (((JSONObject) o2).getString("columnName").equals("Field_3")) {
                                drb.addColumn(((JSONObject) o2).getString("description"), "Field_" + Integer.toString(countColumn), Integer.class.getName(), 80, cellStyle);
                            } else {
                                drb.addColumn(((JSONObject) o2).getString("description"), "Field_" + Integer.toString(countColumn), String.class.getName(), 80, cellStyle);
                            }

                        } catch (ColumnBuilderException | ClassNotFoundException ex) {
                            Logger.getLogger(jsonToReportImpl.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

                countColumn++;
            }
        }

        Boolean striped;
        if (tableStriped.equals("true")) {
            striped = true;
        } else {
            striped = false;
        }
        //Cell styling
        Style cellStyleParent = new Style("cellParent");
        cellStyleParent.setBackgroundColor(Color.WHITE);
        cellStyleParent.setTransparency(Transparency.OPAQUE);

        //Header styling
        Style headerStyleParent = new Style("headerParent");
        headerStyleParent.setBackgroundColor(Color.decode("#b3e0ff"));
        headerStyleParent.setTransparency(Transparency.OPAQUE);

        //Title styling
        Style titleStyleParent = new Style("titleParent");
        titleStyleParent.setFont(Font.ARIAL_BIG);
        titleStyleParent.setHorizontalAlign(HorizontalAlign.RIGHT);

        //Page size configuration
        ReportPage page = Constants.getPageDimensions(pageSize, pageOrientation);

        //Adding groups
        Style headerVariables = new Style();
        headerVariables.setFont(Font.TIMES_NEW_ROMAN_BIG_BOLD);
        //		headerVariables.setBorderBottom(Border.THIN());
        headerVariables.setHorizontalAlign(HorizontalAlign.RIGHT);
        headerVariables.setVerticalAlign(VerticalAlign.MIDDLE);

        String a = drb.getColumn(0).toString();
        GroupBuilder gb1 = new GroupBuilder();
        gb1.setCriteriaColumn((PropertyColumn) drb.getColumn(0))
                .addFooterVariable(drb.getColumn(2), DJCalculation.SUM, titleStyleParent) // tell the group place a variable footer of the column "columnAmount" with the SUM of allvalues of the columnAmount in this group.				.addHeaderVariable(columnaQuantity,DJCalculation.SUM,headerVariables) // idem for the columnaQuantity column
                // .addFooterVariable(drb.getColumn(0), DJCalculation.SUM, headerVariables) // tell the group place a variable footer of the column "columnAmount" with the SUM of allvalues of the columnAmount in this group.
                // .addFooterVariable(drb.getColumn(0), DJCalculation.SUM, headerVariables) // idem for the columnaQuantity column
                .setGroupLayout(GroupLayout.DEFAULT_WITH_HEADER); // tells the group how to be shown, there are manyposibilities, see the GroupLayout for more.
        // .build();

        DJGroup g1 = gb1.build();

        //Report print configuration
        Integer margin = new Integer(20);

        Style detailStyle = new Style();

        Style headerStyle = new Style();
        headerStyle.setFont(Font.ARIAL_MEDIUM_BOLD);
        headerStyle.setBorderBottom(Border.PEN_1_POINT());
        headerStyle.setBackgroundColor(Color.gray);
        headerStyle.setTextColor(Color.white);
        headerStyle.setHorizontalAlign(HorizontalAlign.CENTER);
        headerStyle.setVerticalAlign(VerticalAlign.MIDDLE);
        headerStyle.setTransparency(Transparency.OPAQUE);

        Style titleStyle = new Style();
        titleStyle.setFont(new Font(18, Font._FONT_VERDANA, true));

        DynamicReport dr = drb
                //.addGroups(2)
                .setTitle(titulo)
                .setSubtitle("Este reporte fue genereado el: " + new Date().toGMTString())
                .setDetailHeight(new Integer(15)).setLeftMargin(margin)
                .setRightMargin(margin).setTopMargin(margin).setBottomMargin(margin)
                .setPrintBackgroundOnOddRows(false)
                .setGrandTotalLegend("Grand Total")
                .setGrandTotalLegendStyle(headerVariables)
                //.setDefaultStyles(titleStyleParent, null, headerStyleParent, cellStyleParent)
                .setDefaultStyles(titleStyle, null, headerStyle, detailStyle)
                .setPrintColumnNames(false)
                .setReportName("ExportPDF")
                .addGroup(g1)
                .build();

        //drb.addGroup(g1);
        //dr = drb.build();
        ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(jsonData.getBytes());
        //Create json datasource from json stream
        JsonDataSource ds = null;
        try {
            ds = new JsonDataSource(jsonDataStream);
        } catch (JRException ex) {
            Logger.getLogger(jsonToReportImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Finally print the report report
        JasperPrint jp = null;
        try {
            jp = DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(), ds);
        } catch (JRException ex) {
            Logger.getLogger(jsonToReportImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return jp;

//
//        Style detailStyle = new Style("detail");
//
//        Style headerStyle = new Style("header");
//        headerStyle.setFont(Font.ARIAL_MEDIUM_BOLD);
//        headerStyle.setBorderBottom(Border.PEN_1_POINT());
//        headerStyle.setBackgroundColor(Color.gray);
//        headerStyle.setTextColor(Color.white);
//        headerStyle.setHorizontalAlign(HorizontalAlign.CENTER);
//        headerStyle.setVerticalAlign(VerticalAlign.MIDDLE);
//        headerStyle.setTransparency(Transparency.OPAQUE);
//
//        Style headerVariables = new Style("headerVariables");
//        headerVariables.setFont(Font.ARIAL_BIG_BOLD);
//        headerVariables.setBorderBottom(Border.THIN());
//        headerVariables.setHorizontalAlign(HorizontalAlign.RIGHT);
//        headerVariables.setVerticalAlign(VerticalAlign.TOP);
//        headerVariables.setStretchWithOverflow(true);
//
//        Style groupVariables = new Style("groupVariables");
//        groupVariables.setFont(Font.ARIAL_MEDIUM_BOLD);
//        groupVariables.setTextColor(Color.BLUE);
//        groupVariables.setBorderBottom(Border.THIN());
//        groupVariables.setHorizontalAlign(HorizontalAlign.RIGHT);
//        groupVariables.setVerticalAlign(VerticalAlign.BOTTOM);
//
//        Style titleStyle = new Style("titleStyle");
//        titleStyle.setFont(new Font(18, Font._FONT_VERDANA, true));
//        Style importeStyle = new Style();
//        importeStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
//        Style oddRowStyle = new Style();
//        oddRowStyle.setBorder(Border.NO_BORDER());
//        oddRowStyle.setBackgroundColor(Color.LIGHT_GRAY);
//        oddRowStyle.setTransparency(Transparency.OPAQUE);
//
//        DynamicReportBuilder drb = new DynamicReportBuilder();
//        Integer margin = new Integer(20);
//        drb
//                .setTitleStyle(titleStyle)
//                .setTitle("November sales report") //defines the title of the report
//                .setSubtitle("The items in this report correspond "
//                        + "to the main products: DVDs, Books, Foods and Magazines")
//                .setDetailHeight(new Integer(15)).setLeftMargin(margin)
//                .setRightMargin(margin).setTopMargin(margin).setBottomMargin(margin)
//                .setPrintBackgroundOnOddRows(true)
//                .setGrandTotalLegend("Grand Total")
//                .setGrandTotalLegendStyle(headerVariables)
//                .setOddRowBackgroundStyle(oddRowStyle);
//
//        AbstractColumn columnState = ColumnBuilder.getNew()
//                .setColumnProperty("state", String.class.getName()).setTitle(
//                "State").setWidth(new Integer(85))
//                .setStyle(titleStyle).setHeaderStyle(titleStyle).build();
//
//        AbstractColumn columnBranch = ColumnBuilder.getNew()
//                .setColumnProperty("branch", String.class.getName()).setTitle(
//                "Branch").setWidth(new Integer(85)).setStyle(
//                detailStyle).setHeaderStyle(headerStyle).build();
//
//        AbstractColumn columnaProductLine = ColumnBuilder.getNew()
//                .setColumnProperty("productLine", String.class.getName())
//                .setTitle("Product Line").setWidth(new Integer(85)).setStyle(
//                detailStyle).setHeaderStyle(headerStyle).build();
//
//        AbstractColumn columnaItem = ColumnBuilder.getNew()
//                .setColumnProperty("item", String.class.getName()).setTitle(
//                "Item").setWidth(new Integer(85)).setStyle(detailStyle)
//                .setHeaderStyle(headerStyle).build();
//
//        AbstractColumn columnCode = ColumnBuilder.getNew()
//                .setColumnProperty("id", Long.class.getName()).setTitle("ID")
//                .setWidth(new Integer(40)).setStyle(importeStyle)
//                .setHeaderStyle(headerStyle).build();
//
//        AbstractColumn columnaQuantity = ColumnBuilder.getNew()
//                .setColumnProperty("quantity", Long.class.getName()).setTitle(
//                "Quantity").setWidth(new Integer(25)).setStyle(
//                importeStyle).setHeaderStyle(headerStyle).build();
//
//        AbstractColumn columnAmount = ColumnBuilder.getNew()
//                .setColumnProperty("amount", Float.class.getName()).setTitle(
//                "Amount").setWidth(new Integer(100))
//                .setPattern("$ 0.00").setStyle(importeStyle).setHeaderStyle(
//                headerStyle).build();
//
//        drb.addGlobalHeaderVariable(columnAmount, DJCalculation.SUM, headerVariables);
//        drb.addGlobalHeaderVariable(columnaQuantity, DJCalculation.SUM, headerVariables);
//        drb.addGlobalFooterVariable(columnAmount, DJCalculation.SUM, headerVariables);
//        drb.addGlobalFooterVariable(columnaQuantity, DJCalculation.SUM, headerVariables);
//        drb.setGlobalHeaderVariableHeight(new Integer(25));
//        drb.setGlobalFooterVariableHeight(new Integer(25));
//
//        GroupBuilder gb1 = new GroupBuilder();
//
//        //		 define the criteria column to group by (columnState)
//        DJGroup g1 = gb1.setCriteriaColumn((PropertyColumn) columnState)
//                .addFooterVariable(columnaQuantity, DJCalculation.SUM, groupVariables) // idem for the columnaQuantity column
//                //				.addFooterVariable(columnAmount,DJCalculation.SUM,groupVariables) // tell the group place a variable footer of the column "columnAmount" with the SUM of allvalues of the columnAmount in this group.
//                .addHeaderVariable(columnaQuantity, DJCalculation.SUM, groupVariables) // idem for the columnaQuantity column
//                .addHeaderVariable(columnAmount, DJCalculation.SUM, groupVariables) // tell the group place a variable footer of the column "columnAmount" with the SUM of allvalues of the columnAmount in this group.
//                .setGroupLayout(GroupLayout.VALUE_IN_HEADER) // tells the group how to be shown, there are manyposibilities, see the GroupLayout for more.
//                .setFooterVariablesHeight(new Integer(20))
//                .setFooterHeight(new Integer(50), true)
//                .setHeaderVariablesHeight(new Integer(35))
//                .build();
//
//        GroupBuilder gb2 = new GroupBuilder(); // Create another group (using another column as criteria)
//        DJGroup g2 = gb2.setCriteriaColumn((PropertyColumn) columnBranch) // and we add the same operations for the columnAmount and
//                .addFooterVariable(columnAmount,
//                        DJCalculation.SUM) // columnaQuantity columns
//                .addFooterVariable(columnaQuantity, DJCalculation.SUM)
//                .build();
//
//        drb.addColumn(columnState);
//        drb.addColumn(columnBranch);
//        drb.addColumn(columnaProductLine);
//        drb.addColumn(columnaItem);
//        drb.addColumn(columnCode);
//        drb.addColumn(columnaQuantity);
//        drb.addColumn(columnAmount);
//
//        drb.addGroup(g1); // add group g1
//        //		drb.addGroup(g2); // add group g2
//
//        drb.setUseFullPageWidth(true);
//        drb.addAutoText(AutoText.AUTOTEXT_PAGE_X_SLASH_Y, AutoText.POSITION_FOOTER, AutoText.ALIGNMENT_RIGHT);
//
//        DynamicReport dr = drb.build();
//
//        //Convert json string to byte array.
//    //    ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(jsonData.getBytes());
//        //Create json datasource from json stream
//        JsonDataSource ds = null;
////        try {
////            ds = new JsonDataSource(jsonDataStream);
////        } catch (JRException ex) {
////            Logger.getLogger(jsonToReportImpl.class.getName()).log(Level.SEVERE, null, ex);
////        }
//        //Finally print the report report
//        JasperPrint jp = null;
//        try {
//            jp = DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(),ds);
//        } catch (JRException ex) {
//            Logger.getLogger(jsonToReportImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return jp;
    }

}
