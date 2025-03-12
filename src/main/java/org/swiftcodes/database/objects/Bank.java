package org.swiftcodes.database.objects;

import jakarta.persistence.*;

@Entity
@Table(name="banks")
public class Bank {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="bank_id")
    private int bankId;

    @Column(name="is_headquarter")
    private boolean isHeadquarter;

    @Column(name="name")
    private String name;

    @Column(name="address")
    private String address;

    @Column(name="country_id")
    private int countryId;

    public Bank() {

    }

    public Bank(Boolean isHeadquarter, String name, String address, int countryId) {
        this.isHeadquarter = isHeadquarter;
        this.name = name;
        this.address = address;
        this.countryId = countryId;
    }

    public int getBankId() {return this.bankId;}

    public void setBankId(int bankId) {this.bankId = bankId;}
    public boolean getIsHeadquarter() {return this.isHeadquarter;}

    public void setIsHeadquarter(boolean isHeadquarter) {this.isHeadquarter = isHeadquarter;}

    public String getName() {return this.name;}

    public void setName(String name) {this.name = name;}

    public String getAddress() {return this.address;}

    public void setAddress(String address) {this.address = address;}

    public int getCountryId() {return this.countryId;}

    public void setCountryId(int countryId) {this.countryId = countryId;}

}
