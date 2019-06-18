/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plustech.reportscore.controller;


import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ottor
 */
@Component
@RestController
@RequestMapping("/")
public class MongoQueryReportController {

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    public String hello() {
        return "Hello Kubernetes";
    };

//    @RequestMapping(value = "pdf", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
//    public void generateReportCustomEntityXls(@RequestBody String json,
//            HttpServletResponse response) throws IOException, JRException, ColumnBuilderException, ClassNotFoundException {
////    @GetMapping("/all")
////    public String getAll() {
//
//        // MongoClient mongoClient = new MongoClient("mongo", 27017);
//        MongoClientURI connectionString = new MongoClientURI(Constants.MONGO_SERVER_URI);
//        MongoClient mongoClient = new MongoClient(connectionString);
//        MongoDatabase database = mongoClient.getDatabase(Constants.DATA_BASE);
//
//        MongoCollection<org.bson.Document> collection = database.getCollection("plustec_medicalappointments");
//        //mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?options]]
//        Document myDoc = (Document) collection.find().first();
//
//        System.out.println("esta es la coleccion:" + myDoc.toJson());
//
//        MongoCursor<Document> cursor = collection.find().iterator();
//        //JSONObject jsonObj = new JSONObject();
//
//        String jsonConstructor = "[ ";
//        try {
//
//            while (cursor.hasNext()) {
//                System.out.println(cursor.next().toJson());
//                jsonConstructor = jsonConstructor + cursor.next().toJson() + ",";
//            }
//        } finally {
//            cursor.close();
//            jsonConstructor = jsonConstructor.substring(0, jsonConstructor.length() - 1);
//            jsonConstructor = jsonConstructor + " ]";
//            // jsonConstructor = jsonConstructor.replace("\"_id\"", "\"otto_engazado\"");
//        }
//
//        //Comienza llenado de reporte
//        JSONObject jsonObj = new JSONObject(json);
//        //   String jsonData = jsonObj.get("data").toString();
//        String titulo = jsonObj.get("title").toString();
//        JSONArray columns = jsonObj.getJSONArray("columns");
//
//        FastReportBuilder drb = new FastReportBuilder();
//
//        Map<String, Object> parameters = new HashMap<String, Object>();
//        parameters.put("titulo", titulo);
//
//        int countColumn = 1;
//
//        for (Object o : columns) {
//            if (o instanceof JSONObject) {
//                jsonConstructor = jsonConstructor.replace("\"" + ((JSONObject) o).getString("key") + "\"", "\"Field_" + Integer.toString(countColumn) + "\"");
//                countColumn++;
//            }
//        }
//
//        System.out.println("esta es la en string: " + jsonConstructor);
//
//        countColumn = 1;
//
//        for (Object o : columns) {
//            if (o instanceof JSONObject) {
//
//                int size = Integer.parseInt(((JSONObject) o).getString("columnSize"));
//                drb.addColumn(((JSONObject) o).getString("description"), "Field_" + Integer.toString(countColumn), String.class.getName(), size);
//                countColumn++;
//            }
//        }
//
////        JSONArray jsonObjArray = new JSONArray(jsonConstructor);
////
////        Gson gson = new Gson();
////        EntityGeneric[] entity = gson.fromJson(jsonConstructor, EntityGeneric[].class);
////        EntityGeneric gen = new EntityGeneric();
////        gen.setData(toList(jsonObjArray));
//        DynamicReport dr = drb
//                //.addGroups(2)
//                .setTitle(titulo)
//                .setSubtitle("Este reporte fue genereado el: " + new Date().toLocaleString())
//                //.setPrintBackgroundOnOddRows(true)
//                .setUseFullPageWidth(true)
//                .build();
//
//        // MoldeReportes data = new MoldeReportes();
//        //Convert json string to byte array.
//        ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(jsonConstructor.getBytes());
//        //Create json datasource from json stream
//        JsonDataSource ds = new JsonDataSource(jsonDataStream);
//
//        //JRDataSource ds = new JRBeanCollectionDataSource();
//        JasperPrint jp = DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(), ds);
//        //JasperViewer.viewReport(jp);    //finally display the report report
//
//        if (jp != null) {
//            byte[] pdfReport = JasperExportManager
//                    .exportReportToPdf(jp);
//            response.reset();
//            response.setContentType("application/pdf");
//            response.setHeader("Cache-Control", "no-store");
//            response.setHeader("Cache-Control", "private");
//            response.setHeader("Pragma", "no-store");
//            response.setContentLength(pdfReport.length);
//            response.getOutputStream().write(pdfReport);
//            response.getOutputStream().flush();
//            response.getOutputStream().close();
//        }
//
////        int countColumn = 1;
////        for (Object o : columns) {
////            if (o instanceof JSONObject) {
//        //JSONArray jsonArray = new JSONArray("[{\"a\":1},{\"b\":2,\"c\":3},{\"d\":4},{\"e\":5,\"f\":7}]");
////        for (int i = 0; i < jsonObjArray.length(); i++) {
////            JSONObject json = jsonObjArray.getJSONObject(i);
////            Iterator<String> keys = json.keys();
////
////            while (keys.hasNext()) {
////                String key = keys.next();
////                System.out.println("Key :" + key + "  Value :" + json.get(key));
////            }
////
////        }
////                int size = Integer.parseInt(((JSONObject) o).getString("columnSize"));
////                //drb.addColumn(((JSONObject) o).getString("description"), "Field_" + Integer.toString(countColumn), String.class.getName(), size);
////                countColumn++;
////            }
////        }
////        return jsonConstructor;
//    }
//
//    public <T> List<T> toList(JSONArray jsonArray) {
//        List<T> list = new ArrayList<>();
//        if (jsonArray != null) {
//            int len = jsonArray.length();
//            for (int i = 0; i < len; i++) {
//                list.add((T) jsonArray.opt(i));
//            }
//        }
//        return list;
//    }

}
