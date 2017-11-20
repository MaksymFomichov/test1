package com.gmail.fomichov.m.drillingmagazine.geology;


public class Probe {
    private double depth;
    private String description;
    private String type;
    private long idProbe;

    public Probe(){

    }

    public Probe(double depth, String description, String type, long idProbe) {
        this.depth = depth;
        this.description = description;
        this.type = type;
        this.idProbe = idProbe;
    }

    public Probe(double depth, String description, String type) {
        this.depth = depth;
        this.description = description;
        this.type = type;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getIdProbe() {
        return idProbe;
    }

    public void setIdProbe(int idProbe) {
        this.idProbe = idProbe;
    }
}
