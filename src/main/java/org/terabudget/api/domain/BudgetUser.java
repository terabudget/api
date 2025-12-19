package org.terabudget.api.domain;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The physical DB representation of a user.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetUser {

    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "budget_user_role", joinColumns = @JoinColumn(name = "budget_user_id"), inverseJoinColumns = @JoinColumn(name = "budget_role_id"))
    private Set<BudgetRole> roles;

    @Column(unique = true)
    private String username;

    @Nullable
    private String email;

    @JsonIgnore
    private String password;

    private boolean enabled;
}
