package org.swiftcodes;
import jakarta.persistence.*;

@Entity
@Table(name="Countries")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int countryId;

    @Column(name="iso2")
    private String iso2;

    @Column(name="name")
    private String name;

    @Column(name="timezone")
    private String timezone;

    public Country(){

    }

    public Country(String iso2, String name, String timezone){
        this.iso2 = iso2;
        this.name = name;
        this.timezone = timezone;
    }

    public int getCountryId() {return this.countryId;}

    public void setCountryId(int countryId) { this.countryId = countryId; }

    public String getIso2() {return this.iso2;}

    public void setIso2(String iso2) { this.iso2 = iso2; }

    public String getName() {return this.name;}

    public void setName(String name) { this.name = name; }

}
