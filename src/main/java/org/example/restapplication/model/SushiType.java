package org.example.restapplication.model;

import java.util.Objects;
import java.util.UUID;

public class SushiType extends SimpleEntity {
    private String name;
    public SushiType(String typeName) {
        this.name = typeName;
    }

    public SushiType(UUID id, String typeName) {
        super(id);
        this.name = typeName;
    }

    public SushiType(UUID id) {
        super(id);
    }
    public SushiType() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SushiType{" +
                "typeName='" + name + '\'' +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SushiType sushiType)) return false;
        return Objects.equals(name, sushiType.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
