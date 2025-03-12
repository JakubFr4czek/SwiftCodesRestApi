package org.swiftcodes.DTO;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.LinkedHashMap;
import java.util.Map;

public class SwiftCodeDTO {

    private String bankAddress;
    private String bankName;
    private String countryISO2;
    private String countryName;
    private boolean isHeadquarter;
    private String swiftCode;

    public SwiftCodeDTO() {
        this.bankAddress = "";
        this.bankName = "";
        this.countryISO2 = "";
        this.countryName = "";
        this.isHeadquarter = false;
        this.swiftCode = "";
    }

    public SwiftCodeDTO(String bankAddress, String bankName, String countryISO2, String countryName, boolean isHeadquarter, String swiftCode) {
        this.bankAddress = bankAddress;
        this.bankName = bankName;
        this.countryISO2 = countryISO2.toUpperCase();
        this.countryName = countryName;
        this.isHeadquarter = isHeadquarter;
        this.swiftCode = swiftCode;
    }

    public Map<String, Object> toHashMap() throws JsonProcessingException {

        Map<String, Object> swiftCodeHashMap = new LinkedHashMap<>();

        swiftCodeHashMap.put("bankAddress", bankAddress);
        swiftCodeHashMap.put("bankName", bankName);
        swiftCodeHashMap.put("countryISO2", countryISO2);
        swiftCodeHashMap.put("countryName", countryName);
        swiftCodeHashMap.put("isHeadquarter", isHeadquarter);
        swiftCodeHashMap.put("swiftCode", swiftCode);

        return swiftCodeHashMap;
    }

    public void fillSwiftCodeData(String swiftCode){
        setSwiftCode(swiftCode);
    }

    public void fillBankData(String bankAddress, String bankName, boolean isHeadquarter){
        setBankAddress(bankAddress);
        setBankName(bankName);
        setIsHeadquarter(isHeadquarter);
    }

    public void fillCountryData(String countryISO2, String countryName){
        setCountryISO2(countryISO2);
        setCountryName(countryName);
    }

    public String getBankAddress() {return this.bankAddress;}

    public void setBankAddress(String bankAddress) {this.bankAddress = bankAddress;}

    public String getBankName() {return this.bankName;}

    public void setBankName(String bankName) {this.bankName = bankName;}

    public String getCountryISO2() {return this.countryISO2.toUpperCase();}

    public void setCountryISO2(String countryISO2) {this.countryISO2 = countryISO2.toUpperCase();}

    public String getCountryName() {return this.countryName;}

    public void setCountryName(String countryName) {this.countryName = countryName;}

    public boolean getIsHeadquarter() {return this.isHeadquarter;}

    public void setIsHeadquarter(boolean isHeadquarter) {this.isHeadquarter = isHeadquarter;}

    public String getSwiftCode() {return this.swiftCode;}

    public void setSwiftCode(String swiftCode) {this.swiftCode = swiftCode;}


}
