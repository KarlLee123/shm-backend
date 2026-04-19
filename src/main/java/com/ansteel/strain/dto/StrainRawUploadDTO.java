package com.ansteel.strain.dto;

import java.math.BigDecimal;

public class StrainRawUploadDTO {

    private String sensorId;

    private BigDecimal strainValue;

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    private String collectTime;

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public BigDecimal getStrainValue() {
        return strainValue;
    }

    public void setStrainValue(BigDecimal strainValue) {
        this.strainValue = strainValue;
    }

    public String getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(String collectTime) {
        this.collectTime = collectTime;
    }
}