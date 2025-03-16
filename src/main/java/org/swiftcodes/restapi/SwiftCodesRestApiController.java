package org.swiftcodes.restapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swiftcodes.dto.SwiftCodeDTO;
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

    @Autowired
    private DatabaseManager databaseManager;

    @RequestMapping("/v1/swift-codes/{swift-code}")
    public ResponseEntity<String> getHeadquatersBySwiftCode(@PathVariable("swift-code") String swiftCodeString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> branchesMaps = new ArrayList<>();

        SwiftCodeDTO swiftCodeDTO = new SwiftCodeDTO();

        List<SwiftCode> swiftCodeList = swiftCodeRepository.findBySwiftCode(swiftCodeString);

        if(swiftCodeList.size() == 1) {

            swiftCodeDTO.fillSwiftCodeData(swiftCodeList.get(0).getSwiftCode());

            List<Bank> bankList = bankRepository.findByBankId(swiftCodeList.get(0).getBankId());

            if (bankList.size() == 1) {

                swiftCodeDTO.fillBankData(bankList.get(0).getAddress(), bankList.get(0).getName(), bankList.get(0).getIsHeadquarter());

                if(bankList.get(0).getIsHeadquarter()) {

                    List<SwiftCodeDTO> branches = getBranchBySwiftCode(swiftCodeList.get(0).getSwiftCode());

                    if(branches == null){
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Error while getting branches\"}");
                    }

                    for (SwiftCodeDTO branch : branches) {
                        branchesMaps.add(branch.toHashMap());
                    }

                }

                List<Country> countryList = countryRepository.findByCountryId(bankList.get(0).getCountryId());

                if(countryList.size() == 1) {

                    swiftCodeDTO.fillCountryData(countryList.get(0).getIso2().toUpperCase(), countryList.get(0).getName());

                }else if(countryList.size() > 1) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"More than one country found for countryId!\"}");
                }

            }else if(bankList.size() > 1) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"More than one bank found for bankId!\"}");
            }

        }else if(swiftCodeList.size() > 1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"More than one swiftCode object found for swiftCode!\"}");

        }

        Map<String, Object> response = swiftCodeDTO.toHashMap();
        response.put("branches", branchesMaps);

        return ResponseEntity.status(HttpStatus.OK).body(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));

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
                        return null;
                    }

                }else if(bankList.size() > 1){
                    return null;
                }

            }else if(swiftCodeList.size() > 1) {
                return null;
            }

            response.add(swiftCodeDTO);

        }

        return response;

    }

    @RequestMapping("/v1/swift-codes/country/{countryISO2code}")
    public ResponseEntity<String> getCoutryByISO2(@PathVariable("countryISO2code") String countryISO2code) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> swiftCodesList = new ArrayList<>();

        List<Country> countryList = countryRepository.findCountryByISO2(countryISO2code.toUpperCase());
        Country country;

        if(countryList.size() == 1) {

            country = countryList.get(0);

            List<SwiftCodeDTO> swiftcodes = swiftCodeRepository.findSwiftCodesByISO2(country.getIso2().toUpperCase());

            for (SwiftCodeDTO swiftcode : swiftcodes) {
                swiftCodesList.add(swiftcode.toHashMap());
            }

        }else if(countryList.size() > 1){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"More than one country found for ISO2 code!\"}");

        }else{
            country = new Country();
        }

        Map<String, Object> response = country.toHashMap();
        response.put("swiftCodes", swiftCodesList);

        return ResponseEntity.status(HttpStatus.OK).body(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(response));
    }

    @PostMapping("/v1/swift-codes")
    public ResponseEntity<String> newSwiftCode(@RequestBody SwiftCodeDTO swiftCodeDTO) {

        if(!databaseManager.checkIfSwiftCodeAlreadyExists(swiftCodeDTO.getSwiftCode())){

            Country country = new Country(swiftCodeDTO.getCountryISO2(), swiftCodeDTO.getCountryName());

            country = databaseManager.saveCountry(country);

            Bank bank = new Bank(swiftCodeDTO.getIsHeadquarter(), swiftCodeDTO.getBankName(), swiftCodeDTO.getBankAddress(), country.getCountryId());

            bank = databaseManager.saveBank(bank);

            SwiftCode swiftCode = new SwiftCode(swiftCodeDTO.getSwiftCode(), bank.getBankId());

            swiftCode = databaseManager.saveSwiftCode(swiftCode);

            if(swiftCode != null)
                return ResponseEntity.status(HttpStatus.CREATED).body("{\"message\": \"Swift code successfully added!\"}");
            else
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Error while adding Swift code!\"}");
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"Swift code already exists!\"}");
        }

    }

    @DeleteMapping("/v1/swift-codes/{swift-code}")
    public ResponseEntity<String> deleteSwiftCode(@PathVariable("swift-code") String swiftCodeString) {

        List<SwiftCode> swiftCodeList = swiftCodeRepository.findBySwiftCode(swiftCodeString);

        if (swiftCodeList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"Swift code not found!\"}");
        }else if(swiftCodeList.size() == 1){
            swiftCodeRepository.delete(swiftCodeList.get(0));
            return ResponseEntity.status(HttpStatus.OK).body("{\"message\": \"Swift code successfully deleted!\"}");
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"More than one swift code object found for given swift code!\"}");
        }

    }



}
