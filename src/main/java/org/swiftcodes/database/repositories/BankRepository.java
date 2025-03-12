package org.swiftcodes.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swiftcodes.database.objects.Bank;

import java.util.List;

public interface BankRepository extends JpaRepository<Bank, Integer> {
    List<Bank> findByBankId(int bankId);
}
