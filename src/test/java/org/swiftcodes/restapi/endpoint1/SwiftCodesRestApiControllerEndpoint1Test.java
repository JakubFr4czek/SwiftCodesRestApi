package org.swiftcodes.restapi.endpoint1;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SwiftCodesRestApiControllerEndpoint1Test {

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
        when(bankRepository.findByBankId(bankId)).thenReturn(List.of(bank));
        when(swiftCodeRepository.findBySwiftCode(swiftCode.getSwiftCode())).thenReturn(List.of(swiftCode));
        when(swiftCodeRepository.findBranches(swiftCode.getSwiftCode())).thenReturn(List.of());


        return new FakeRecord(swiftCodeDTO, country, bank, swiftCode);

    }

    @Test
    void shouldReturnHeadquarter() throws Exception {

        FakeRecord poland = createFakeRecord("Warszawa", "NBP", "PL", "Polska", true, "QWERTYUIXXX", 7777, 7777);

        mockMvc.perform(get("/v1/swift-codes/{swift-code}", poland.swiftCode.getSwiftCode()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value(poland.bank.getAddress()))
                .andExpect(jsonPath("$.bankName").value(poland.bank.getName()))
                .andExpect(jsonPath("$.countryISO2").value(poland.country.getIso2()))
                .andExpect(jsonPath("$.countryName").value(poland.country.getName()))
                .andExpect(jsonPath("$.isHeadquarter").value(poland.bank.getIsHeadquarter()))
                .andExpect(jsonPath("$.swiftCode").value(poland.swiftCode.getSwiftCode()))
                .andExpect(jsonPath("$.branches.length()").value(0));

    }

    @Test
    void shouldReturnHeadquarterWithBranches() throws Exception {

        FakeRecord warsaw = createFakeRecord("Warszawa", "NBP", "PL", "Polska", true, "QWERTYUIXXX", 7777, 7777);
        FakeRecord krakow = createFakeRecord("Kraków", "Filia NBP", "PL", "Polska", false, "QWERTYUIKRK", 8888, 8888);

        when(swiftCodeRepository.findBranches(warsaw.swiftCode.getSwiftCode())).thenReturn(List.of(krakow.swiftCode));

        mockMvc.perform(get("/v1/swift-codes/{swift-code}", warsaw.swiftCode.getSwiftCode()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value(warsaw.bank.getAddress()))
                .andExpect(jsonPath("$.bankName").value(warsaw.bank.getName()))
                .andExpect(jsonPath("$.countryISO2").value(warsaw.country.getIso2()))
                .andExpect(jsonPath("$.countryName").value(warsaw.country.getName()))
                .andExpect(jsonPath("$.isHeadquarter").value(warsaw.bank.getIsHeadquarter()))
                .andExpect(jsonPath("$.swiftCode").value(warsaw.swiftCode.getSwiftCode()))
                .andExpect(jsonPath("$.branches.length()").value(1))
                .andExpect(jsonPath("$.branches[0].address").value(krakow.bank.getAddress()))
                .andExpect(jsonPath("$.branches[0].bankName").value(krakow.bank.getName()))
                .andExpect(jsonPath("$.branches[0].countryISO2").value(krakow.country.getIso2()))
                .andExpect(jsonPath("$.branches[0].countryName").value(krakow.country.getName()))
                .andExpect(jsonPath("$.branches[0].isHeadquarter").value(false))
                .andExpect(jsonPath("$.branches[0].swiftCode").value(krakow.swiftCode.getSwiftCode()));

    }

    @Test
    void shouldReturnNoDetails() throws Exception {

        mockMvc.perform(get("/v1/swift-codes/{swift-code}", "NONEXISTENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value(""))
                .andExpect(jsonPath("$.bankName").value(""))
                .andExpect(jsonPath("$.countryISO2").value(""))
                .andExpect(jsonPath("$.countryName").value(""))
                .andExpect(jsonPath("$.isHeadquarter").value(false))
                .andExpect(jsonPath("$.swiftCode").value(""))
                .andExpect(jsonPath("$.branches").value(new ArrayList<>()));

    }

    @Test
    void shouldReturnMoreThanOneCountryFound() throws Exception{

        when(swiftCodeRepository.findBySwiftCode(anyString())).thenReturn(List.of(new SwiftCode()));
        when(bankRepository.findByBankId(anyInt())).thenReturn(List.of(new Bank()));
        when(countryRepository.findByCountryId(anyInt())).thenReturn(List.of(new Country(), new Country()));

        mockMvc.perform(get("/v1/swift-codes/{swift-code}", "NONEXISTENT"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("More than one country found for countryId!"));

    }

    @Test
    void shouldReturnMoreThanOneBankFound() throws Exception{

        when(swiftCodeRepository.findBySwiftCode(anyString())).thenReturn(List.of(new SwiftCode()));
        when(bankRepository.findByBankId(anyInt())).thenReturn(List.of(new Bank(), new Bank()));

        mockMvc.perform(get("/v1/swift-codes/{swift-code}", "NONEXISTENT"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("More than one bank found for bankId!"));

    }

    @Test
    void shouldReturnMoreThanOneSwiftCodeFound() throws Exception{

        when(swiftCodeRepository.findBySwiftCode(anyString())).thenReturn(List.of(new SwiftCode(), new SwiftCode()));

        mockMvc.perform(get("/v1/swift-codes/{swift-code}", "NONEXISTENT"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("More than one swiftCode object found for swiftCode!"));

    }

}
