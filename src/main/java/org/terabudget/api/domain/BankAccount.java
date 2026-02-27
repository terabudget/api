package org.terabudget.api.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This represents a bank account from a financial instution.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    @NotBlank(message = "Bank account name must not be blank")
    private String name;
    
    private boolean isOnBudget;

    @JsonIgnore
    @OneToMany(mappedBy = "originatingBankAccount")
    private List<BankTransaction> transactions;
}
