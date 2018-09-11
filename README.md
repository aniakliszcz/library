# library

## Overview
Java library that allows to keeps books and all its information:
- title
- author
- year
- other information connected with library: 
   id of book, 
   if the book is lent, 
   the last person that lent book.
   
## Prerequisites
Install Java (version 8 or later) and the latest version of maven

## Build jar
Use command to generate jar file:
> cd library
> mvn clean package

## Run
You can add jar file to your project, for example
adding a dependency in maven project

or simply run the jar file:
> java -jar library-1.0.jar

then it will run the demo inside the library


