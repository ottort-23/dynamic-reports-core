/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plustech.reportscore.utils;


import org.springframework.stereotype.Component;

/**
 *
 * @author ottor
 */
@Component
public class Constants {

    public static final String MONGO_SERVER_URI = "mongodb://EntifixDB:Deventifix@cluster0-shard-00-00-ymgiz.gcp.mongodb.net:27017,cluster0-shard-00-01-ymgiz.gcp.mongodb.net:27017,cluster0-shard-00-02-ymgiz.gcp.mongodb.net:27017/auth-core-db?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin&retryWrites=true";
    public static final String DATA_BASE = "clinical-scheduling-db";

    public static final ReportPage getPageDimensions(String pageSize, String pageOrientation) {
        ReportPage page = new ReportPage();
        String sizeText = pageSize.toUpperCase();
        String orientationText = pageOrientation.toUpperCase();
        
        switch (sizeText) {
            case "LETTER":
                page.setHeight(792);
                page.setWidth(612);
                break;
            case "A0":
                page.setHeight(3370);
                page.setWidth(2384);
                break;
            case "A1":
                page.setHeight(2384);
                page.setWidth(1684);
                break;
            case "A2":
                page.setHeight(1684);
                page.setWidth(1190);
                break;
            case "A3":
                page.setHeight(1190);
                page.setWidth(842);
                break;
            case "A4":
                page.setHeight(842);
                page.setWidth(595);
                break;
            default:
                page.setHeight(792);
                page.setWidth(612);
                break;
        }

        //Setting landscape orientation
        if (orientationText.equals("PORTRAIT")) {
            int height = page.getHeight();
            int width = page.getWidth();
            page.setHeight(width);
            page.setWidth(height);

        }

        return page;
    }
;

}
