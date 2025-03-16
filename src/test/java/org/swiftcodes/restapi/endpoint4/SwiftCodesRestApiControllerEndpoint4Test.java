package org.swiftcodes.restapi.endpoint2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
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

public class SwiftCodesRestApiControllerEndpoint4Test {

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
    ObjectMapper objectMapper;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(swiftCodesRestApiController).build();
        objectMapper = new ObjectMapper();

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
    void shouldReturnSwiftCodeNotFound() throws Exception {

        doNothing().when(swiftCodeRepository).delete(any());

        // Perform the DELETE request and verify the response
        mockMvc.perform(delete("/v1/swift-codes/{id}", "TESTSWIFTCODE"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Swift code not found!"));
    }

    @Test
    void shouldReturnSuccesfullyDeleted() throws Exception {

        when(swiftCodeRepository.findBySwiftCode(any())).thenReturn(List.of(new SwiftCode()));
        doNothing().when(swiftCodeRepository).delete(any());

        // Perform the DELETE request and verify the response
        mockMvc.perform(delete("/v1/swift-codes/{id}", "TESTSWIFTCODE"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Swift code successfully deleted!"));
    }

    @Test
    void shouldFailDueToMoreThanOneMatchingSwiftCode() throws Exception {

        when(swiftCodeRepository.findBySwiftCode(any())).thenReturn(List.of(new SwiftCode(), new SwiftCode()));
        doNothing().when(swiftCodeRepository).delete(any());

        // Perform the DELETE request and verify the response
        mockMvc.perform(delete("/v1/swift-codes/{id}", "TESTSWIFTCODE"))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("More than one swift code object found for given swift code!"));
    }

}
