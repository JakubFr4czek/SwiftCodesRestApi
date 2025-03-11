package org.swiftcodes.springboot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swiftcodes.database.Country;

public interface CountryRepository extends JpaRepository<Country, Integer> {
    Country findByCountryId(int countryId);
}
