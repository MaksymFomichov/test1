package com.gmail.fomichov.m.drillingmagazine.geology;

public class Layer {
    private double startDepth;
    private double endDepth;
    private String description;
    private long idLayer;

    public Layer(){
    }

    public Layer(double startDepth, double endDepth, String description, long idLayer) {
        this.startDepth = startDepth;
        this.endDepth = endDepth;
        this.description = description;
        this.idLayer = idLayer;
    }

    public Layer(double startDepth, double endDepth, String description) {
        this.startDepth = startDepth;
        this.endDepth = endDepth;
        this.description = description;
    }

    // получаем толщину слоя
    public double getLayerPower() {
        return endDepth - startDepth;
    }

    public double getStartDepth() {
        return startDepth;
    }

    public void setStartDepth(double startDepth) {
        this.startDepth = startDepth;
    }

    public double getEndDepth() {
        return endDepth;
    }

    public void setEndDepth(double endDepth) {
        this.endDepth = endDepth;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getIdLayer() {
        return idLayer;
    }

    public void setIdLayer(int idLayer) {
        this.idLayer = idLayer;
    }
}
