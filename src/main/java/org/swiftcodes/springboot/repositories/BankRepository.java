package org.swiftcodes.springboot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swiftcodes.database.Bank;

public interface BankRepository extends JpaRepository<Bank, Integer> {
    Bank findByBankId(int bankId);
}
