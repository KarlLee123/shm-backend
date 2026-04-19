package com.ansteel.acceleration.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccelerationDataEntity {

    private Long id;

    private String sensorId;

    private BigDecimal accelerationValue;

    private LocalDateTime collectTime;

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

    public BigDecimal getAccelerationValue() {
        return accelerationValue;
    }

    public void setAccelerationValue(BigDecimal accelerationValue) {
        this.accelerationValue = accelerationValue;
    }

    public LocalDateTime getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(LocalDateTime collectTime) {
        this.collectTime = collectTime;
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