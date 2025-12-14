package org.terabudget.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * This represents a bank transaction from a financial institution.
 */
@Entity
@Data
public class BankTransaction {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    /**
     * The bank account from which this transaction first originated.
     */
    @NotBlank(message = "Originating bank account must not be blank")
    @JsonIgnore
    @ManyToOne
    private BankAccount originatingBankAccount;
}
