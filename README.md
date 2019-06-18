# reports-core-api

This project is an api made that receives an object and returns as a response a file of type report.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites



```
Java 8 version
```

### Installing

A step by step series of examples that tell you how to get a development env running


```
git clone https://gitlab.com/entifix-core/reports-core-api.git
```

Build Spring Boot project with maven

```
mvn install
```
Or 
```
maven package
```

## Run Project

Run Spring Boot app with java -jar command
```
java -jar target/reports-core-0.0.1-SNAPSHOT.jar
```
Or run Spring Boot app using Maven
```
mvn spring-boot:run
```

## Print sample report

* Default port:8095
* POST
* url: http://localhost:8095/json-to-report/pdf

```
mvn spring-boot:run
```

Example object

```
{  
   "data":[  
      {  
         "Field_1":44,
         "Field_3":11,
         "Field_2":"Edwin Taylor",
         "Field_4":"45693212",
         "Field_5":"mailTest@gmail.com"
      },
      {  
         "Field_1":88,
         "Field_3":25,
         "Field_2":"Otto Rodriguez",
         "Field_4":"13212333",
         "Field_5":"mail@gmail.com"
      }
   ],
   "title":"Example Report",
   "columns":[  
      {  
         "description":"Id",
         "columnName":"Field_1"
      },
      {  
         "description":"Edad",
         "columnName":"Field_3"
      },
      {  
         "description":"Nombre",
         "columnName":"Field_2"
      },
      {  
         "description":"Telefono",
         "columnName":"Field_4"
      },
      {  
         "description":"Email",
         "columnName":"Field_5"
      }
   ],
   "tableStriped":"false",
   "pageSize": "Letter", 
   "pageOrientation": "Landscape", 
   "imageUrl":""
}

```
## Page sizes
* Letter
* Legal
* A0
* A1
* A2
* A3
* A4

```
## Page orientations
* Landscape
* Portrait


```
## Versioning

Version:
```
Version 1.0.0
```

## Authors
* Members






