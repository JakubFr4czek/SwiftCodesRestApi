import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.swiftcodes.database.objects.Country;

import java.util.List;

public class HibernateTest {

    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    protected void setUp() throws Exception {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();

        try {
            this.entityManagerFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        }
        catch (Exception e) {
            StandardServiceRegistryBuilder.destroy( registry );
        }
    }

    @AfterEach
    protected void tearDown() throws Exception {
        if ( this.entityManagerFactory != null ) {
            this.entityManagerFactory.close();
        }
    }

    @Test
    void hql_test(){

        try(EntityManager entityManager = this.entityManagerFactory.createEntityManager()){

            entityManager.getTransaction().begin();

            List<Country> countryList = entityManager.createQuery("select c from Country c",Country.class).getResultList();

            System.out.println(countryList);

            entityManager.getTransaction().commit();

        }

    }

    @Test
    void countries_save_test(){

        Country country = new Country("this", "is", "a test");

        try(EntityManager entityManager = this.entityManagerFactory.createEntityManager()){

            entityManager.getTransaction().begin();

            entityManager.persist(country);

            entityManager.getTransaction().commit();

        }

    }

}
