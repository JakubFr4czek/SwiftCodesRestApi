package org.swiftcodes;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;
import java.util.List;

public class Main {

    public static void readData(String filename){

        try (CSVReader csvReader  = new CSVReaderBuilder(new FileReader(filename)).withSkipLines(1).build()) {

            List<String[]> allData = csvReader.readAll();

            for(String[] row : allData){
                System.out.println(row);
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }


    }

    public static void main(String[] args){

        readData("src/main/resources/Interns_2025_SWIFT_CODES.csv");

    }

}