package org.swiftcodes.database.objects;

import jakarta.persistence.*;

@Entity
@Table(name="swiftcodes")
public class SwiftCode {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="swift_code_id")
    private int swiftCodeId;

    @Column(name="swift_code", unique = true)
    private String swiftCode;

    @Column(name="bank_id")
    private int bankId;

    public SwiftCode() {}

    public SwiftCode(String swiftCode, int bankId) {
        this.swiftCode = swiftCode;
        this.bankId = bankId;
    }

    public int getSwiftCodeId() { return swiftCodeId; }

    public void setSwiftCodeId(int swiftCodeId) { this.swiftCodeId = swiftCodeId; }

    public String getSwiftCode() { return swiftCode; }

    public void setSwiftCode(String swiftCode) { this.swiftCode = swiftCode; }

    public int getBankId() { return bankId; }

    public void setBankId(int bankId) { this.bankId = bankId; }

}
