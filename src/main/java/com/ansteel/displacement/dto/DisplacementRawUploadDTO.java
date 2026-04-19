package com.ansteel.displacement.dto;

import java.math.BigDecimal;

public class DisplacementRawUploadDTO {

    private String sensorId;

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