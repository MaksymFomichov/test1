package com.gmail.fomichov.m.drillingmagazine.geology;

import java.util.List;

public class Excavation {
    private String nameExcavation; // номер или имя
    private String dateStart; // дата начала
    private String dateEnd; // дата окончания
    private List<Probe> probes; // массив проб
    private List<Layer> layers; // массив слоев
    private List<Zond> zonds; // массив слоев
    private String descriptionExcavation; // описание выработки
    private String latitude; // широта
    private String longitude; // долгота
    private String equipment; // оборудование
    private String whoWorked; // кто производил работы
    private String penetrationMethod; // метод работ
    private String typeExcavation; // тип выработки
    private String object; // имя обьекта
    private double absoluteElevation; // абсолютная отметка выработки
    private long keyExc; // ключ выработки в базе данных
    private double waterShow; // появивийся уровень воды
    private double waterStay; // устоявшийся уровень воды
    private String dateWaterStay; // дата замера устовшегося уровня
    private String dateWaterShow; // дата замера появившегося уровня

    public Excavation() {
    }

    public Excavation(String nameExcavation, String dateStart, String dateEnd, String descriptionExcavation, String latitude, String longitude,
                      String equipment, String whoWorked, String penetrationMethod, String typeExcavation, String object, double absoluteElevation,
                      long keyExc, double waterShow, double waterStay, String dateWaterStay, String dateWaterShow) {
        this.nameExcavation = nameExcavation;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.descriptionExcavation = descriptionExcavation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.equipment = equipment;
        this.whoWorked = whoWorked;
        this.penetrationMethod = penetrationMethod;
        this.typeExcavation = typeExcavation;
        this.object = object;
        this.absoluteElevation = absoluteElevation;
        this.keyExc = keyExc;
        this.waterShow = waterShow;
        this.waterStay = waterStay;
        this.dateWaterStay = dateWaterStay;
        this.dateWaterShow = dateWaterShow;
    }

    public Excavation(String nameExcavation, String dateStart, String dateEnd, List<Probe> probes,
                      List<Layer> layers, String descriptionExcavation, String latitude, String longitude,
                      String equipment, String whoWorked, String penetrationMethod, String typeExcavation,
                      String object, double absoluteElevation, double waterShow, double waterStay, String dateWaterStay, String dateWaterShow) {
        this.nameExcavation = nameExcavation;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.probes = probes;
        this.layers = layers;
        this.descriptionExcavation = descriptionExcavation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.equipment = equipment;
        this.whoWorked = whoWorked;
        this.penetrationMethod = penetrationMethod;
        this.typeExcavation = typeExcavation;
        this.object = object;
        this.absoluteElevation = absoluteElevation;
        this.waterShow = waterShow;
        this.waterStay = waterStay;
        this.dateWaterStay = dateWaterStay;
        this.dateWaterShow = dateWaterShow;
    }



    public Excavation(String object) {
        this.object = object;
    }

    public Excavation(String nameExcavation, String typeExcavation, String object) {
        this.nameExcavation = nameExcavation;
        this.typeExcavation = typeExcavation;
        this.object = object;
    }

    public double getAbsoluteElevation() {
        return absoluteElevation;
    }

    public void setAbsoluteElevation(double absoluteElevation) {
        this.absoluteElevation = absoluteElevation;
    }

    public String getNameExcavation() {
        return nameExcavation;
    }

    public void setNameExcavation(String nameExcavation) {
        this.nameExcavation = nameExcavation;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public List<Probe> getProbes() {
        return probes;
    }

    public void setProbes(List<Probe> probes) {
        this.probes = probes;
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }

    public String getDescriptionExcavation() {
        return descriptionExcavation;
    }

    public void setDescriptionExcavation(String descriptionExcavation) {
        this.descriptionExcavation = descriptionExcavation;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getWhoWorked() {
        return whoWorked;
    }

    public void setWhoWorked(String whoWorked) {
        this.whoWorked = whoWorked;
    }

    public String getPenetrationMethod() {
        return penetrationMethod;
    }

    public void setPenetrationMethod(String penetrationMethod) {
        this.penetrationMethod = penetrationMethod;
    }

    public String getTypeExcavation() {
        return typeExcavation;
    }

    public void setTypeExcavation(String typeExcavation) {
        this.typeExcavation = typeExcavation;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public long getKeyExc() {
        return keyExc;
    }

    public void setKeyExc(long keyExc) {
        this.keyExc = keyExc;
    }

    public double getWaterShow() {
        return waterShow;
    }

    public void setWaterShow(double waterShow) {
        this.waterShow = waterShow;
    }

    public double getWaterStay() {
        return waterStay;
    }

    public void setWaterStay(double waterStay) {
        this.waterStay = waterStay;
    }

    public String getDateWaterStay() {
        return dateWaterStay;
    }

    public void setDateWaterStay(String dateWaterStay) {
        this.dateWaterStay = dateWaterStay;
    }

    public String getDateWaterShow() {
        return dateWaterShow;
    }

    public void setDateWaterShow(String dateWaterShow) {
        this.dateWaterShow = dateWaterShow;
    }

    @Override
    public String toString() {
        return "Excavation{" +
                "nameExcavation='" + nameExcavation + '\'' +
                ", dateStart='" + dateStart + '\'' +
                ", dateEnd='" + dateEnd + '\'' +
                ", probes=" + probes +
                ", layers=" + layers +
                ", descriptionExcavation='" + descriptionExcavation + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", equipment='" + equipment + '\'' +
                ", whoWorked='" + whoWorked + '\'' +
                ", penetrationMethod='" + penetrationMethod + '\'' +
                ", typeExcavation='" + typeExcavation + '\'' +
                ", object='" + object + '\'' +
                ", absoluteElevation=" + absoluteElevation +
                ", keyExc=" + keyExc +
                ", waterShow=" + waterShow +
                ", waterStay=" + waterStay +
                ", dateWaterStay='" + dateWaterStay + '\'' +
                ", dateWaterShow='" + dateWaterShow + '\'' +
                '}';
    }
}
