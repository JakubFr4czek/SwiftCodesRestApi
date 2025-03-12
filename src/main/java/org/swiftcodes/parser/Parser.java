package org.swiftcodes.parser;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.swiftcodes.Exceptions.SwiftCodesTableException;
import org.swiftcodes.database.management.DatabaseManager;
import org.swiftcodes.database.objects.Bank;
import org.swiftcodes.database.objects.Country;
import org.swiftcodes.database.objects.SwiftCode;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Parser {


    static List<String[]> readData(String filename) {

        List<String[]> allData;

        try (CSVReader csvReader  = new CSVReaderBuilder(new FileReader(filename)).withSkipLines(1).build()) {

            allData = csvReader.readAll();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }

        return allData;

    }


    static void saveToDatabase(List<String[]> allData) {

        for(String[] row : allData){

            String iso2 = row[0].toUpperCase();
            String swiftCodeString = row[1];
            String bankName = row[3];
            String bankAddress = row[4];
            String countryName = row[6];
            boolean isHeadquarter = true;

            if(swiftCodeString.endsWith("XXX"))
                isHeadquarter = false;

            DatabaseManager databaseManager = new DatabaseManager();

            if(!databaseManager.checkIfSwiftCodeAlreadyExists(swiftCodeString)){

                Country country = new Country(iso2, countryName);

                country = databaseManager.saveCountry(country);

                Bank bank = new Bank(isHeadquarter, bankName, bankAddress, country.getCountryId());

                bank = databaseManager.saveBank(bank);

                SwiftCode swiftCode = new SwiftCode(swiftCodeString, bank.getBankId());

                swiftCode = databaseManager.saveSwiftCode(swiftCode);

                if(swiftCode != null) throw new SwiftCodesTableException("Error while inserting SwiftCode");


            }

        }

    }



    public static void main(String[] args){

        List<String[]> allData = readData("src/main/resources/Interns_2025_SWIFT_CODES.csv");
        saveToDatabase(allData);

    }

}