package com.ansteel.acceleration.dto;

import java.math.BigDecimal;

public class AccelerationRawUploadDTO {

    private String sensorId;

    private BigDecimal accelerationValue;

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

    public BigDecimal getAccelerationValue() {
        return accelerationValue;
    }

    public void setAccelerationValue(BigDecimal accelerationValue) {
        this.accelerationValue = accelerationValue;
    }

    public String getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(String collectTime) {
        this.collectTime = collectTime;
    }
}