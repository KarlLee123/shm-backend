package com.ansteel.stress.dto;

import java.math.BigDecimal;

public class StressRawUploadDTO {

    private String sensorId;

    private BigDecimal forceValue;

    private BigDecimal displacementValue;

    /**
     * 前端/设备传入格式固定：
     * yyyy-MM-dd HH:mm:ss
     */
    private String collectTime;

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public BigDecimal getForceValue() {
        return forceValue;
    }

    public void setForceValue(BigDecimal forceValue) {
        this.forceValue = forceValue;
    }

    public BigDecimal getDisplacementValue() {
        return displacementValue;
    }

    public void setDisplacementValue(BigDecimal displacementValue) {
        this.displacementValue = displacementValue;
    }

    public String getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(String collectTime) {
        this.collectTime = collectTime;
    }
}