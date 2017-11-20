package com.gmail.fomichov.m.drillingmagazine.geology;

import java.util.List;

public class ObjectGeology {
    private String description;
    private String name;
    private List<Excavation> excavations;

    public ObjectGeology(){

    }

    public ObjectGeology(String name, String description) {
        this.description = description;
        this.name = name;
    }

    public ObjectGeology(String name, String description, List<Excavation> excavations) {
        this.description = description;
        this.name = name;
        this.excavations = excavations;
    }

    public ObjectGeology(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Excavation> getExcavations() {
        return excavations;
    }

    public void setExcavations(List<Excavation> excavations) {
        this.excavations = excavations;
    }
}
