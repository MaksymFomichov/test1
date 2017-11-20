package com.gmail.fomichov.m.drillingmagazine.geology;

public class Zond {
    private long idZond;
    private double depthZond;
    private double qResist;
    private double fResist;

    public Zond() {
    }

    public Zond(long idZond, double depthZond, double qResist, double fResist) {
        this.idZond = idZond;
        this.depthZond = depthZond;
        this.qResist = qResist;
        this.fResist = fResist;
    }

    public long getIdZond() {
        return idZond;
    }

    public void setIdZond(long idZond) {
        this.idZond = idZond;
    }

    public double getDepthZond() {
        return depthZond;
    }

    public void setDepthZond(double depthZond) {
        this.depthZond = depthZond;
    }

    public double getqResist() {
        return qResist;
    }

    public void setqResist(double qResist) {
        this.qResist = qResist;
    }

    public double getfResist() {
        return fResist;
    }

    public void setfResist(double fResist) {
        this.fResist = fResist;
    }
}
