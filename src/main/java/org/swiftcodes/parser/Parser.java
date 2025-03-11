package org.swiftcodes.parser;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.swiftcodes.database.Bank;
import org.swiftcodes.database.Country;
import org.swiftcodes.database.SwiftCode;

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


    static void saveToDatabase(List<String[]> allData, EntityManagerFactory entityManagerFactory) {

        for(String[] row : allData){

            String iso2 = row[0];
            String swiftCode = row[1];
            String codeType = row[2];
            String bankName = row[3];
            String bankAddress = row[4];
            String townName = row[5];
            String countryName = row[6];
            String timezone = row[7];
            boolean is_branch = false;

            if(swiftCode.substring(swiftCode.length()-4,swiftCode.length()-1).equals("XXX"))
                is_branch = true;


            try(EntityManager entityManager = entityManagerFactory.createEntityManager()){
                entityManager.getTransaction().begin();

                List<SwiftCode> swiftCodeList = entityManager.createQuery("select s from SwiftCode s WHERE s.swiftCode = :swiftCode",SwiftCode.class)
                        .setParameter("swiftCode",swiftCode)
                        .getResultList();

                List<Country> countryList = entityManager.createQuery("select c from Country c WHERE c.iso2 = :iso2",Country.class)
                        .setParameter("iso2",iso2)
                        .getResultList();

                if (countryList.size()>1) {
                    throw new RuntimeException("Error while inserting country into database");
                }

                if(!swiftCodeList.isEmpty()) {
                    throw new RuntimeException("Swift code already exists in database");
                }

                int country_id;

                if(countryList.isEmpty()) {
                    Country country = new Country(iso2, countryName, timezone);
                    entityManager.persist(country);
                    country_id = country.getCountryId();
                }else{ //Country already exists in database
                    country_id = countryList.get(0).getCountryId();
                }

                Bank bank = new Bank(is_branch, bankName, bankAddress, townName, country_id);
                entityManager.persist(bank);

                if(swiftCodeList.isEmpty()) {
                    SwiftCode sc = new SwiftCode(swiftCode, codeType, bank.getBankId());
                    entityManager.persist(sc);
                }


                entityManager.getTransaction().commit();

            }catch (Exception e){
                e.printStackTrace();
            }


        }

    }



    public static void main(String[] args){

        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();

        try(EntityManagerFactory entityManagerFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();) {
            List<String[]> allData = readData("src/main/resources/Interns_2025_SWIFT_CODES.csv");
            saveToDatabase(allData, entityManagerFactory);
        }
        catch (Exception e) {
            StandardServiceRegistryBuilder.destroy( registry );
        }

    }

}