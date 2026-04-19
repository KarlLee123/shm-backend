package com.ansteel.stress.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StressDataEntity {

    private Long id;

    private String sensorId;

    private BigDecimal forceValue;

    private BigDecimal displacementValue;

    private BigDecimal calcStress;

    private BigDecimal verifiedStress;

    private Integer verifyStatus;

    private String verifyRemark;

    private LocalDateTime collectTime;

    private LocalDateTime calcTime;

    private LocalDateTime verifyTime;

    private LocalDateTime createTime;

    private Integer deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public BigDecimal getCalcStress() {
        return calcStress;
    }

    public void setCalcStress(BigDecimal calcStress) {
        this.calcStress = calcStress;
    }

    public BigDecimal getVerifiedStress() {
        return verifiedStress;
    }

    public void setVerifiedStress(BigDecimal verifiedStress) {
        this.verifiedStress = verifiedStress;
    }

    public Integer getVerifyStatus() {
        return verifyStatus;
    }

    public void setVerifyStatus(Integer verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

    public String getVerifyRemark() {
        return verifyRemark;
    }

    public void setVerifyRemark(String verifyRemark) {
        this.verifyRemark = verifyRemark;
    }

    public LocalDateTime getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(LocalDateTime collectTime) {
        this.collectTime = collectTime;
    }

    public LocalDateTime getCalcTime() {
        return calcTime;
    }

    public void setCalcTime(LocalDateTime calcTime) {
        this.calcTime = calcTime;
    }

    public LocalDateTime getVerifyTime() {
        return verifyTime;
    }

    public void setVerifyTime(LocalDateTime verifyTime) {
        this.verifyTime = verifyTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}