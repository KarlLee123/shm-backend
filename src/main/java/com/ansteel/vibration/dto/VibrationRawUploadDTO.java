package com.ansteel.vibration.dto;

import java.math.BigDecimal;

public class VibrationRawUploadDTO {

    private String sensorId;

    private BigDecimal vibrationValue;

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

    public BigDecimal getVibrationValue() {
        return vibrationValue;
    }

    public void setVibrationValue(BigDecimal vibrationValue) {
        this.vibrationValue = vibrationValue;
    }

    public String getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(String collectTime) {
        this.collectTime = collectTime;
    }
}