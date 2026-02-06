package org.terabudget.api.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class BudgetCategoryCreateDTO {
    @NotEmpty(message = "Budget category name must not be empty")
    private String name;
}
