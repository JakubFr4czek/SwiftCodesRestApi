package org.swiftcodes.database;

import jakarta.persistence.*;

@Entity
@Table(name="banks")
public class Bank {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="bank_id")
    private int bankId;

    @Column(name="is_branch")
    private boolean isBranch;

    @Column(name="name")
    private String name;

    @Column(name="address")
    private String address;

    @Column(name="town_name")
    private String townName;

    @Column(name="country_id")
    private int countryId;

    public Bank() {

    }

    public Bank(Boolean isBranch, String name, String address, String townName, int countryId) {
        this.isBranch = isBranch;
        this.name = name;
        this.address = address;
        this.townName = townName;
        this.countryId = countryId;
    }

    public int getBankId() {return this.bankId;}

    public void setBankId(int bankId) {this.bankId = bankId;}

    public boolean getIsBranch() {return this.isBranch;}

    public void setIsBranch(boolean isBranch) {this.isBranch = isBranch;}

    public String getName() {return this.name;}

    public void setName(String name) {this.name = name;}

    public String getAddress() {return this.address;}

    public void setAddress(String address) {this.address = address;}

    public String getTownName() {return this.townName;}

    public void setTownName(String townName) {this.townName = townName;}

    public int getCountryId() {return this.countryId;}

    public void setCountryId(int countryId) {this.countryId = countryId;}

}
