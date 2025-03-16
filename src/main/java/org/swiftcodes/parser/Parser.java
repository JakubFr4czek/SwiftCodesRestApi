package org.swiftcodes.parser;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.swiftcodes.exceptions.SwiftCodesTableException;
import org.swiftcodes.database.management.DatabaseManager;
import org.swiftcodes.database.objects.Bank;
import org.swiftcodes.database.objects.Country;
import org.swiftcodes.database.objects.SwiftCode;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@SpringBootApplication
@ComponentScan("org.swiftcodes")  // Ensure the base package is scanned for beans
public class Parser {

    private final DatabaseManager databaseManager;

    // Constructor-based injection for DatabaseManager
    public Parser(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    // Method to read data from the CSV
    static List<String[]> readData(String filename) {
        List<String[]> allData;
        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(filename)).withSkipLines(1).build()) {
            allData = csvReader.readAll();
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
        return allData;
    }

    // Method to save data to the database using repository functions
    public void saveToDatabase(List<String[]> allData) {
        for (String[] row : allData) {
            String iso2 = row[0].toUpperCase();
            String swiftCodeString = row[1];
            String bankName = row[3];
            String bankAddress = row[4];
            String countryName = row[6];
            boolean isHeadquarter = swiftCodeString.endsWith("XXX");

            // If the swift code doesn't exist, add new records to the database
            if (!databaseManager.checkIfSwiftCodeAlreadyExists(swiftCodeString)) {
                Country country = new Country(iso2, countryName);
                country = databaseManager.saveCountry(country);

                Bank bank = new Bank(isHeadquarter, bankName, bankAddress, country.getCountryId());
                bank = databaseManager.saveBank(bank);

                SwiftCode swiftCode = new SwiftCode(swiftCodeString, bank.getBankId());
                swiftCode = databaseManager.saveSwiftCode(swiftCode);

                if (swiftCode == null) {
                    throw new SwiftCodesTableException("Error while inserting SwiftCode");
                }
            }
        }
    }

    // Main method to initialize Spring context and run the parser
    public static void main(String[] args) {
        // Set the application type to non-web to avoid web server initialization
        System.setProperty("spring.main.web-application-type", "none");

        // Initialize Spring context
        ApplicationContext context = SpringApplication.run(Parser.class, args);

        // Get the Parser bean from Spring context
        Parser parser = context.getBean(Parser.class);

        // Read the CSV file and save data to the database
        List<String[]> allData = readData("src/main/resources/Interns_2025_SWIFT_CODES.csv");
        parser.saveToDatabase(allData);
    }
}
