package com.tecsup.library.model;

import java.util.Objects;

public class Member {
    private final String id;
    private String name;
    private int activeLoans = 0;

    public Member(String id, String name) {
        this.id = Objects.requireNonNull(id, "ID no puede ser null");
        this.name = Objects.requireNonNull(name, "Name no puede ser null");
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getActiveLoans() { return activeLoans; }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Name no puede ser null");
    }

    /** Llama al crear un préstamo activo */
    public void incrementLoans() { this.activeLoans++; }

    /** Llama al devolver un préstamo */
    public void decrementLoans() { 
        if (activeLoans > 0) this.activeLoans--; 
    }

    @Override
    public String toString() {
        return "Member{" +
               "id='" + id + '\'' +
               ", name='" + name + '\'' +
               ", activeLoans=" + activeLoans +
               '}';
    }
}
