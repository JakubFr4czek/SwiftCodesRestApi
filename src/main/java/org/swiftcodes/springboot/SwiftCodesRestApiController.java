package org.swiftcodes.springboot;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.swiftcodes.database.Bank;
import org.swiftcodes.database.Country;
import org.swiftcodes.database.SwiftCode;
import org.swiftcodes.springboot.repositories.BankRepository;
import org.swiftcodes.springboot.repositories.CountryRepository;
import org.swiftcodes.springboot.repositories.SwiftCodeRepository;

@RestController
public class SwiftCodesRestApiController {
    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private SwiftCodeRepository swiftCodeRepository;

    @RequestMapping("/v1/swift-codes/{swift-code}")
    public String getSwiftCode(@PathVariable("swift-code") String swiftCodeParam) {
        SwiftCode swiftCode = swiftCodeRepository.findBySwiftCode(swiftCodeParam);

        JSONObject response = new JSONObject();

        if(swiftCode != null) {

            //selecting associated bank
            Bank bank = bankRepository.findByBankId(swiftCode.getBankId());

            if (bank != null) {

                //selecting associated country
                Country country = countryRepository.findByCountryId(bank.getCountryId());

                if(country != null) {

                    response.put("address", bank.getAddress());
                    response.put("bankName", bank.getName());
                    response.put("countryName", country.getName());
                    response.put("isHeadquater", !bank.getIsBranch());
                    response.put("swiftCode", swiftCode.getSwiftCode());
                    response.put("branches", "");

                    //todo: find branches

                    return response.toString();

                }else{
                    //thrwsmerr
                }

            }else{
                //throw some error
            }

        }else{
        }

        return "Swift code not found"; //TODO: change this
    }

}
