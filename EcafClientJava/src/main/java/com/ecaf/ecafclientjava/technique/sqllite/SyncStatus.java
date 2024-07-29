package com.ecaf.ecafclientjava.technique.sqllite;

public enum SyncStatus {
    SYNCED("synced"),
    PENDING("pending"),
    DELETE_PENDING("delete_pending"),
    NEW("new"),
    UPDATED("updated"),
    DELETED("deleted");

    private final String status;

    SyncStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return status;
    }
}
