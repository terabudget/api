package org.terabudget.api.domain;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This represents a bank account from a financial instution.
 */
@Node("BankAccount")
public class BankAccount {
    @Id
    @GeneratedValue
    private String id;

    @Property("name")
    private String name;

    @JsonIgnore
    @Relationship(type = "ORIGINATED_FROM", direction = Relationship.Direction.INCOMING)
    private List<BankTransaction> transactions;
}
