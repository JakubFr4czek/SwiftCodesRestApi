package org.swiftcodes.database.objects;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name="countries")
public class Country {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="Country_id")
    private int countryId;

    @Column(name="iso2", unique = true)
    private String iso2;

    @Column(name="name", unique = true)
    private String name;

    public Country(){
        this.iso2 = "";
        this.name = "";
    }

    public Country(String iso2, String name){
        this.iso2 = iso2;
        this.name = name;
    }

    public Map<String, Object> toHashMap() throws JsonProcessingException {

        Map<String, Object> countryHashMap = new LinkedHashMap<>();

        countryHashMap.put("countryISO2", iso2);
        countryHashMap.put("countryName", name);

        return countryHashMap;
    }

    public int getCountryId() {return this.countryId;}

    public void setCountryId(int countryId) { this.countryId = countryId; }

    public String getIso2() {return this.iso2;}

    public void setIso2(String iso2) { this.iso2 = iso2; }

    public String getName() {return this.name;}

    public void setName(String name) { this.name = name; }

}
