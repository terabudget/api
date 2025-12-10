package org.terabudget.api.domain;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * This represents a bank account from a financial instution.
 */
@Node("BankAccount")
@Data
public class BankAccount {
    @Id
    @GeneratedValue
    private String id;

    @NotBlank(message = "Bank account name must not be blank")
    @Property("name")
    private String name;

    @JsonIgnore
    @Relationship(type = "ORIGINATED_FROM", direction = Relationship.Direction.INCOMING)
    private List<BankTransaction> transactions;
}
