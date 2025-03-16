package org.swiftcodes.restapi.endpoint2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.swiftcodes.database.management.DatabaseManager;
import org.swiftcodes.database.objects.Bank;
import org.swiftcodes.database.objects.Country;
import org.swiftcodes.database.objects.SwiftCode;
import org.swiftcodes.database.repositories.BankRepository;
import org.swiftcodes.database.repositories.CountryRepository;
import org.swiftcodes.database.repositories.SwiftCodeRepository;
import org.swiftcodes.dto.SwiftCodeDTO;
import org.swiftcodes.restapi.SwiftCodesRestApiController;
import org.swiftcodes.restapi.endpoint1.SwiftCodesRestApiControllerEndpoint1Test;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SwiftCodesRestApiControllerEndpoint2Test {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private BankRepository bankRepository;

    @Mock
    private SwiftCodeRepository swiftCodeRepository;

    @Mock DatabaseManager databaseManager;

    @InjectMocks
    private SwiftCodesRestApiController swiftCodesRestApiController;

    private MockMvc mockMvc;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(swiftCodesRestApiController).build();

    }

    record FakeRecord(SwiftCodeDTO dto, Country country, Bank bank, SwiftCode swiftCode) {}

    FakeRecord createFakeRecord(String address, String name, String iso2, String countryName, boolean isHeadquarter, String swiftCodeString, int countryId, int bankId) {

        SwiftCodeDTO swiftCodeDTO = new SwiftCodeDTO(address, name,  iso2, countryName, isHeadquarter, swiftCodeString);
        Country country = swiftCodeDTO.getCountryData();
        Bank bank = swiftCodeDTO.getBankData(countryId);
        SwiftCode swiftCode = swiftCodeDTO.getSwiftCodeData(bankId);

        when(countryRepository.findByCountryId(countryId)).thenReturn(List.of(country));
        when(countryRepository.findCountryByISO2(iso2)).thenReturn(List.of(country));
        when(bankRepository.findByBankId(bankId)).thenReturn(List.of(bank));
        when(swiftCodeRepository.findBySwiftCode(swiftCode.getSwiftCode())).thenReturn(List.of(swiftCode));
        when(swiftCodeRepository.findBranches(swiftCode.getSwiftCode())).thenReturn(List.of());

        return new FakeRecord(swiftCodeDTO, country, bank, swiftCode);

    }

    @Test
    void shouldReturnOneSwiftCode() throws Exception {
        FakeRecord poland = createFakeRecord("Warszawa", "NBP", "PL", "Polska", true, "QWERTYUIXXX", 7777, 7777);

        when(swiftCodeRepository.findSwiftCodesByISO2(poland.country.getIso2())).thenReturn(List.of(poland.dto));

        mockMvc.perform(get("/v1/swift-codes/country/{countryISO2Code}", poland.country.getIso2()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryISO2").value(poland.country.getIso2()))
                .andExpect(jsonPath("$.countryName").value(poland.country.getName()))
                .andExpect(jsonPath("$.swiftCodes.length()").value(1));
    }


    @Test
    void shouldReturnTwoSwiftCodes() throws Exception {

        FakeRecord warsaw = createFakeRecord("Warszawa", "NBP", "PL", "Polska", true, "QWERTYUIXXX", 7777, 7777);
        FakeRecord krakow = createFakeRecord("Kraków", "Filia NBP", "PL", "Polska", false, "QWERTYUIKRK", 8888, 8888);

        when(swiftCodeRepository.findSwiftCodesByISO2(warsaw.country.getIso2())).thenReturn(List.of(warsaw.dto, krakow.dto));

        mockMvc.perform(get("/v1/swift-codes/country/{countryISO2Code}", warsaw.country.getIso2()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryISO2").value(warsaw.country.getIso2()))
                .andExpect(jsonPath("$.countryName").value(warsaw.country.getName()))
                .andExpect(jsonPath("$.swiftCodes.length()").value(2));
    }

    @Test
    void shouldReturnNoSwiftCodes() throws Exception {

        mockMvc.perform(get("/v1/swift-codes/country/{countryISO2Code}", "NONEXISTENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryISO2").value(""))
                .andExpect(jsonPath("$.countryName").value(""))
                .andExpect(jsonPath("$.swiftCodes.length()").value(0));

    }

    @Test
    void shouldReturnInternalServerError() throws Exception {

        when(countryRepository.findCountryByISO2("TEST")).thenReturn(List.of(new Country(), new Country()));

        mockMvc.perform(get("/v1/swift-codes/country/{countryISO2Code}", "TEST"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("More than one country found for ISO2 code!"));

    }

}
