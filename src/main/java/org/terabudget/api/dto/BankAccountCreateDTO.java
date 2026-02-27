package org.terabudget.api.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class BankAccountCreateDTO {
    @NotEmpty(message = "Bank account name must not be empty")
    private String name;
    private boolean isOnBudget;
}
