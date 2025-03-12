package org.swiftcodes.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.swiftcodes.database.objects.Country;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Integer> {
    List<Country> findByCountryId(int countryId);

    @Query("SELECT c FROM Country c WHERE c.iso2 = :iso2")
    List<Country> findCountryByISO2(@Param("iso2") String iso2);
}
