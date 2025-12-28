package org.terabudget.api.model.auth;

import lombok.Getter;

public enum BuiltInRoleUrn {
    ACCOUNT_CREATE_ANY("urn:org.terabudget:builtin:rbac:role:account_create_any");

    @Getter
    private final String urn;

    private BuiltInRoleUrn(String urn) {
        this.urn = urn;
    }
}
