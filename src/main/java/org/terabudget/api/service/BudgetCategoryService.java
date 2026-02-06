package org.terabudget.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.terabudget.api.domain.BudgetCategory;
import org.terabudget.api.dto.BudgetCategoryCreateDTO;
import org.terabudget.api.repository.BudgetCategoryRepository;

/**
 * Service for managing bank accounts.
 */
@Service
public class BudgetCategoryService {
    @Autowired
    private BudgetCategoryRepository budgetCategoryRepository;

    /**
     * Get all budget categories.
     * 
     * @return
     */
    public List<BudgetCategory> getBudgetCategories() {
        return budgetCategoryRepository.findAll();
    }

    /**
     * Create a new budget category.
     * 
     * @param budgetCategoryCreateDTO
     * @return
     */
    public BudgetCategory createBudgetCategory(final BudgetCategoryCreateDTO budgetCategoryCreateDTO) {
        budgetCategoryRepository.findOneByName(budgetCategoryCreateDTO.getName())
                .ifPresent(budgetCategory -> {
                    throw new IllegalArgumentException(
                            "Budget category already exists with name: " + budgetCategoryCreateDTO.getName());
                });

        BudgetCategory budgetCategory = new BudgetCategory();
        budgetCategory.setName(budgetCategoryCreateDTO.getName());
        return budgetCategoryRepository.save(budgetCategory);
    }

    /**
     * Delete a budget category by its ID.
     * 
     * @param budgetCategoryId
     */
    public void deleteBudgetCategory(String budgetCategoryId) {
        budgetCategoryRepository.findById(budgetCategoryId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Budget category not found with ID: " + budgetCategoryId));
        budgetCategoryRepository.deleteById(budgetCategoryId);
    }
}
