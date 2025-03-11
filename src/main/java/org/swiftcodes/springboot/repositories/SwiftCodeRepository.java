package org.swiftcodes.springboot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swiftcodes.database.SwiftCode;

public interface SwiftCodeRepository extends JpaRepository<SwiftCode, Integer> {
    SwiftCode findBySwiftCode(String swiftCode);
}
