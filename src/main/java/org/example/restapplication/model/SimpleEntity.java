package org.example.restapplication.model;

import java.util.UUID;

public class SimpleEntity {
    protected UUID id;

    protected SimpleEntity() {
    }

    protected SimpleEntity(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }


}
