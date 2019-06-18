/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plustech.reportscore.controller;

import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import com.plustech.reportscore.service.AMQService;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.plustech.reportscore.service.JsonToReportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author ottor
 */
@Component
@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("reports/json-to-report/")
public class JsonToReportController {

    @Autowired

    private JsonToReportService jsonToReportService;

    @CrossOrigin
    @RequestMapping(value = "pdf", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_PDF_VALUE)
    public void convertToPdf(@RequestBody String json,
            HttpServletResponse response, @RequestHeader(value = "authorization", required = true) String token) throws Exception, IOException, JRException, ColumnBuilderException, ClassNotFoundException {

        response = jsonToReportService.convertJsonToPdf(json, response);
        response.getOutputStream().flush();
        response.getOutputStream().close();
        System.out.println("**********PDF report generation success**********");

    }

    @CrossOrigin
    @RequestMapping(value = "xls", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void convertToExcel(@RequestBody String json,
            HttpServletResponse response, @RequestHeader(value = "authorization", required = true) String token) throws IOException, JRException, ColumnBuilderException, ClassNotFoundException {

        response = jsonToReportService.convertJsonToXls(json, response);
        System.out.println("**********Xls report generation success**********");
        response.getOutputStream().flush();
        response.getOutputStream().close();

    }

    @CrossOrigin
    @RequestMapping(value = "group", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void convertTotalToPdf(@RequestBody String json,
            HttpServletResponse response) throws IOException, JRException, ColumnBuilderException, ClassNotFoundException {

        JasperPrint jp = jsonToReportService.convertGroupJsonToPdf(json);

        System.out.println("**********Reporte PDF generado con exito**********");

        //Response configuration
        if (jp != null) {
            byte[] pdfReport = JasperExportManager
                    .exportReportToPdf(jp);
            response.reset();
            response.setContentType("application/pdf");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Cache-Control", "private");
            response.setHeader("Pragma", "no-store");
            response.setContentLength(pdfReport.length);
            response.getOutputStream().write(pdfReport);
            response.getOutputStream().flush();
            response.getOutputStream().close();
        }

    }

}
