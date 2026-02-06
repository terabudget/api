package org.terabudget.api.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
public class BudgetCategory {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    @NotBlank(message = "Budget category name must not be blank")
    private String name;

}
