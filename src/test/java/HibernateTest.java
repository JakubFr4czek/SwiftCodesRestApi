import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.swiftcodes.Country;

public class HibernateTest {

    private SessionFactory sessionFactory;

    @BeforeEach
    protected void setUp() throws Exception {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            this.sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        }
        catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy( registry );
        }
    }

    @AfterEach
    protected void tearDown() throws Exception {
        if ( this.sessionFactory != null ) {
            this.sessionFactory.close();
        }
    }

    @Test
    void countries_save_test(){

        Country country = new Country("this", "is", "a test");

        try(Session session = this.sessionFactory.openSession()){

            session.beginTransaction();

            session.persist(country);

            session.getTransaction().commit();

        }

    }

}
