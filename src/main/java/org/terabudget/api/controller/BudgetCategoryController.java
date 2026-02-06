package org.terabudget.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.terabudget.api.domain.BudgetCategory;
import org.terabudget.api.dto.BudgetCategoryCreateDTO;
import org.terabudget.api.service.BudgetCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin
@RestController
@RequestMapping("/api/budget-categories")
@Tag(name = "budget_categories", description = "Budget category management endpoints")
public class BudgetCategoryController {
    @Autowired
    private BudgetCategoryService budgetCategoryService;

    /**
     * Get all budget categories.
     * 
     * @return
     */
    @GetMapping
    @Operation(summary = "Get all budget categories", description = "Returns a list of all budget categories")
    public List<BudgetCategory> getBudgetCategories() {
        return budgetCategoryService.getBudgetCategories();
    }

    /**
     * Create a new budget category.
     * 
     * @param budgetCategoryCreateDTO
     * @return
     */
    @Operation(summary = "Create a new budget category", description = "Creates a new budget category with the provided details")
    @PostMapping
    public BudgetCategory createBudgetCategory(@RequestBody @Valid BudgetCategoryCreateDTO budgetCategoryCreateDTO) {
        try {
            return budgetCategoryService.createBudgetCategory(budgetCategoryCreateDTO);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Delete a bank account by its ID.
     * 
     * @param budgetCategoryId
     */
    @DeleteMapping("/{budgetCategoryId}")
    @Operation(summary = "Delete a budget category", description = "Deletes a budget category by its ID")
    public void deleteBudgetCategory(String budgetCategoryId) {
        try {
            budgetCategoryService.deleteBudgetCategory(budgetCategoryId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
