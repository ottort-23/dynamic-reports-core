/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plustech.reportscore.service;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 *
 * @author ottor
 */
@Component
public interface JsonToReportService {

    HttpServletResponse convertJsonToPdf(String json, HttpServletResponse response) throws Exception; //JasperPrint

    HttpServletResponse convertJsonToXls(String json, HttpServletResponse response) throws IOException; //Workbook

    JasperPrint convertGroupJsonToPdf(String json);

}
