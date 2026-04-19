package com.ansteel.stress.dto;

import java.math.BigDecimal;

public class StressResultUploadDTO {

    private String sensorId;

    private BigDecimal calcStress;

    /**
     * 对应原始采样时间
     * yyyy-MM-dd HH:mm:ss
     */
    private String collectTime;

    /**
     * 计算完成时间
     * yyyy-MM-dd HH:mm:ss
     */
    private String calcTime;

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public BigDecimal getCalcStress() {
        return calcStress;
    }

    public void setCalcStress(BigDecimal calcStress) {
        this.calcStress = calcStress;
    }

    public String getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(String collectTime) {
        this.collectTime = collectTime;
    }

    public String getCalcTime() {
        return calcTime;
    }

    public void setCalcTime(String calcTime) {
        this.calcTime = calcTime;
    }
}