package org.swiftcodes.restapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.swiftcodes.DTO.SwiftCodeDTO;
import org.swiftcodes.Exceptions.BankTableException;
import org.swiftcodes.Exceptions.CountryTableException;
import org.swiftcodes.Exceptions.SwiftCodesTableException;
import org.swiftcodes.database.management.DatabaseManager;
import org.swiftcodes.database.objects.Bank;
import org.swiftcodes.database.objects.Country;
import org.swiftcodes.database.objects.SwiftCode;
import org.swiftcodes.database.repositories.BankRepository;
import org.swiftcodes.database.repositories.CountryRepository;
import org.swiftcodes.database.repositories.SwiftCodeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class SwiftCodesRestApiController {
    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private SwiftCodeRepository swiftCodeRepository;

    @RequestMapping("/v1/swift-codes/{swift-code}")
    public String getHeadquatersBySwiftCode(@PathVariable("swift-code") String swiftCodeString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<String> branchesString = new ArrayList<>();

        SwiftCodeDTO swiftCodeDTO = new SwiftCodeDTO();

        List<SwiftCode> swiftCodeList = swiftCodeRepository.findBySwiftCode(swiftCodeString);

        if(swiftCodeList.size() == 1) {

            swiftCodeDTO.fillSwiftCodeData(swiftCodeList.get(0).getSwiftCode());

            List<Bank> bankList = bankRepository.findByBankId(swiftCodeList.get(0).getBankId());

            if (bankList.size() == 1) {

                swiftCodeDTO.fillBankData(bankList.get(0).getAddress(), bankList.get(0).getName(), bankList.get(0).getIsHeadquarter());

                if(bankList.get(0).getIsHeadquarter()) {

                    List<SwiftCodeDTO> branches = getBranchBySwiftCode(swiftCodeList.get(0).getSwiftCode());

                    for (SwiftCodeDTO branch : branches) {
                        branchesString.add(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(branch.toHashMap()));
                    }

                }

                List<Country> countryList = countryRepository.findByCountryId(bankList.get(0).getCountryId());

                if(countryList.size() == 1) {

                    swiftCodeDTO.fillCountryData(countryList.get(0).getIso2().toUpperCase(), countryList.get(0).getName());

                }else if(countryList.size() > 1) {
                    throw new CountryTableException("More than one country found for countryId: " + countryList.get(0).getCountryId());
                }

            }else if(bankList.size() > 1) {
                throw new BankTableException("More than one bank found for bankId " + swiftCodeList.get(0).getBankId());
            }

        }else if(swiftCodeList.size() > 1) {
            throw new SwiftCodesTableException("More than one swift code found for Swift code " + swiftCodeString);
        }

        Map<String, Object> response = swiftCodeDTO.toHashMap();
        response.put("branches", branchesString);

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);

    }

    public List<SwiftCodeDTO> getBranchBySwiftCode(String swiftCodeString) throws JsonProcessingException {

        List<SwiftCode> branchesList = swiftCodeRepository.findBranches(swiftCodeString);

        List<SwiftCodeDTO> response = new ArrayList<>();

        for(SwiftCode branch : branchesList){

            SwiftCodeDTO swiftCodeDTO = new SwiftCodeDTO();

            List<SwiftCode> swiftCodeList = swiftCodeRepository.findBySwiftCode(branch.getSwiftCode());

            if(swiftCodeList.size() == 1) {

                swiftCodeDTO.fillSwiftCodeData(swiftCodeList.get(0).getSwiftCode());

                List<Bank> bankList = bankRepository.findByBankId(swiftCodeList.get(0).getBankId());

                if (bankList.size() == 1) {

                    swiftCodeDTO.fillBankData(bankList.get(0).getAddress(), bankList.get(0).getName(), bankList.get(0).getIsHeadquarter());

                    List<Country> countryList = countryRepository.findByCountryId(bankList.get(0).getCountryId());

                    if (countryList.size() == 1) {

                        swiftCodeDTO.fillCountryData(countryList.get(0).getIso2().toUpperCase(), countryList.get(0).getName());

                    }else if(countryList.size() > 1) {
                        throw new CountryTableException("More than one country found for countryId: " + bankList.get(0).getCountryId());
                    }

                }else if(bankList.size() > 1){
                    throw new BankTableException("More than one bank found for bankId " + swiftCodeList.get(0).getBankId());
                }

            }else if(swiftCodeList.size() > 1) {
                throw new SwiftCodesTableException("More than one swift code found for Swift code " + swiftCodeString);
            }

            response.add(swiftCodeDTO);

        }

        return response;

    }

    @RequestMapping("/v1/swift-codes/country/{countryISO2code}")
    public String getCoutryByISO2(@PathVariable("countryISO2code") String countryISO2code) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<String> swiftcodesString = new ArrayList<>();

        List<Country> countryList = countryRepository.findCountryByISO2(countryISO2code.toUpperCase());
        Country country;

        if(countryList.size() == 1) {

            country = countryList.get(0);

            List<SwiftCodeDTO> swiftcodes = swiftCodeRepository.findSwiftCodesByISO2(country.getIso2().toUpperCase());

            for (SwiftCodeDTO swiftcode : swiftcodes) {
                swiftcodesString.add(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(swiftcode.toHashMap()));
            }

        }else if(countryList.size() > 1){
            throw new CountryTableException("More than one country found for ISO2 code " + countryISO2code.toUpperCase());
        }else{
            country = new Country();
        }

        Map<String, Object> response = country.toHashMap();
        response.put("swiftCodes", swiftcodesString);

        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(response);
    }

    @PostMapping("/v1/swift-codes")
    public String newSwiftCode(@RequestBody SwiftCodeDTO swiftCodeDTO) {

        DatabaseManager databaseManager = new DatabaseManager();

        if(!databaseManager.checkIfSwiftCodeAlreadyExists(swiftCodeDTO.getSwiftCode())){

            Country country = new Country(swiftCodeDTO.getCountryISO2(), swiftCodeDTO.getCountryName());

            country = databaseManager.saveCountry(country);

            Bank bank = new Bank(swiftCodeDTO.getIsHeadquarter(), swiftCodeDTO.getBankName(), swiftCodeDTO.getBankAddress(), country.getCountryId());

            bank = databaseManager.saveBank(bank);

            SwiftCode swiftCode = new SwiftCode(swiftCodeDTO.getSwiftCode(), bank.getBankId());

            swiftCode = databaseManager.saveSwiftCode(swiftCode);

            if(swiftCode != null)
                return "{\"message\": \"Swift code successfully added!\"}";
            else
                return "{\"message\": \"Error while adding Swift code!\"}";

        }else{
            return "{\"message\": \"Swift code already exists!\"}";
        }

    }

    @DeleteMapping("/v1/swift-codes/{swift-code}")
    public String deleteSwiftCode(@PathVariable("swift-code") String swiftCodeString) {

        List<SwiftCode> swiftCodeList = swiftCodeRepository.findBySwiftCode(swiftCodeString);

        if (swiftCodeList.isEmpty()) {
            return "{\"message\": \"Swift code not found!\"}";
        }else if(swiftCodeList.size() == 1){
            swiftCodeRepository.delete(swiftCodeList.get(0));
            return "{\"message\": \"Swift code successfully deleted!\"}";
        }else{
            throw new SwiftCodesTableException("More than one swift code found for Swift code " + swiftCodeString);
        }

    }


}
