package org.terabudget.api.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Node("Transaction")
@Data
public class BankTransaction {
    @Id
    @GeneratedValue
    private String id;

    /**
     * The bank account from which this transaction first originated.
     */
    @NotBlank(message = "Originating bank account must not be blank")
    @JsonIgnore
    @Relationship(type = "ORIGINATED_FROM", direction = Relationship.Direction.OUTGOING)
    private BankAccount originatingBankAccount;
}
