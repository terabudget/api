package org.terabudget.api.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRole {

    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String urn;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private String context;

    @Getter
    @Setter
    private boolean builtIn;
}
