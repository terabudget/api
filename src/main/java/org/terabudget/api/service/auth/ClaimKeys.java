package org.terabudget.api.service.auth;

public enum ClaimKeys {
    APPLICATION_ID_CLAIM_KEY("terabudget:application_id"),
    REFRESH_PAYLOAD_CLAIM_KEY("terabudget:refresh_payload");

    private final String key;

    ClaimKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
