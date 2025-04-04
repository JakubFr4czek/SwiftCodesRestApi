package org.swiftcodes.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.swiftcodes.dto.SwiftCodeDTO;
import org.swiftcodes.database.objects.SwiftCode;

import java.util.List;

public interface SwiftCodeRepository extends JpaRepository<SwiftCode, Integer> {
    List<SwiftCode> findBySwiftCode(String swiftCode);

    @Query("SELECT sc FROM SwiftCode sc WHERE " +
            "SUBSTRING(sc.swiftCode, 1, 8) = SUBSTRING(:swiftCode, 1, 8) ")
    List<SwiftCode> findBranches(@Param("swiftCode") String swiftCode);

    @Query("SELECT new org.swiftcodes.dto.SwiftCodeDTO(b.address, b.name, c.iso2, c.name, b.isHeadquarter, sc.swiftCode) FROM Country c" +
            " INNER JOIN Bank b ON b.countryId = c.countryId" +
            " INNER JOIN SwiftCode sc ON b.bankId = sc.bankId WHERE c.iso2 = :iso2")
    List<SwiftCodeDTO> findSwiftCodesByISO2(@Param("iso2")String iso2);
}
