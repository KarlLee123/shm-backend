package com.ansteel.stress.dto;

import java.math.BigDecimal;

public class StressVerifyDTO {

    private String sensorId;

    /**
     * PASS / FAIL
     */
    private String action;

    private BigDecimal verifiedStress;

    private String verifyRemark;

    /**
     * 对应原始采样时间
     * yyyy-MM-dd HH:mm:ss
     */
    private String collectTime;

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    private String verifyTime;

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public BigDecimal getVerifiedStress() {
        return verifiedStress;
    }

    public void setVerifiedStress(BigDecimal verifiedStress) {
        this.verifiedStress = verifiedStress;
    }

    public String getVerifyRemark() {
        return verifyRemark;
    }

    public void setVerifyRemark(String verifyRemark) {
        this.verifyRemark = verifyRemark;
    }

    public String getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(String collectTime) {
        this.collectTime = collectTime;
    }

    public String getVerifyTime() {
        return verifyTime;
    }

    public void setVerifyTime(String verifyTime) {
        this.verifyTime = verifyTime;
    }
}