package org.swiftcodes.database.management;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.swiftcodes.database.objects.Bank;
import org.swiftcodes.database.objects.Country;
import org.swiftcodes.database.objects.SwiftCode;
import org.swiftcodes.database.repositories.CountryRepository;
import org.swiftcodes.database.repositories.SwiftCodeRepository;

import java.util.List;

@Service
public class DatabaseManager {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private SwiftCodeRepository swiftCodeRepository;

    public Country saveCountry(Country country){

        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();

        try(EntityManagerFactory entityManagerFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory()) {

            try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {

                List<Country> countryList = countryRepository.findCountryByISO2(country.getIso2());

                if (countryList.size() > 1) {
                    country = null;
                } else if (countryList.size() == 1) {
                    country = countryList.get(0);
                } else {
                    entityManager.getTransaction().begin();
                    entityManager.persist(country);
                    entityManager.getTransaction().commit();
                }

            } catch (Exception e) {
                StandardServiceRegistryBuilder.destroy(registry);
            }
        }

        return country;
    }

    public Bank saveBank(Bank bank){

        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();

        try(EntityManagerFactory entityManagerFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory()) {

            try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {

                entityManager.getTransaction().begin();
                entityManager.persist(bank);
                entityManager.getTransaction().commit();

            } catch (Exception e) {
                StandardServiceRegistryBuilder.destroy(registry);
            }
        }

        return bank;
    }

    public SwiftCode saveSwiftCode(SwiftCode swiftCode) {

        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();

        try (EntityManagerFactory entityManagerFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory()) {

            try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {

                if(checkIfSwiftCodeAlreadyExists(swiftCode.getSwiftCode())) {
                    swiftCode = null;
                }else{
                    entityManager.getTransaction().begin();
                    entityManager.persist(swiftCode);
                    entityManager.getTransaction().commit();
                }
            } catch (Exception e) {
                StandardServiceRegistryBuilder.destroy(registry);
            }
        }

        return swiftCode;
    }

    public boolean checkIfSwiftCodeAlreadyExists(String swiftCodeString){

        List<SwiftCode> swiftCodeList = swiftCodeRepository.findBySwiftCode(swiftCodeString);

        return !swiftCodeList.isEmpty();
    }



}
