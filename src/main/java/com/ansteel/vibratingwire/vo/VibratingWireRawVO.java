package com.ansteel.vibratingwire.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VibratingWireRawVO {
    private String sensorId;
    private String deviceNo;
    private String channelNo;
    private BigDecimal frequency;
    private BigDecimal temperature;
    private BigDecimal tension;
    private BigDecimal strainValue;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime collectTime;
    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
    public String getDeviceNo() { return deviceNo; }
    public void setDeviceNo(String deviceNo) { this.deviceNo = deviceNo; }
    public String getChannelNo() { return channelNo; }
    public void setChannelNo(String channelNo) { this.channelNo = channelNo; }
    public BigDecimal getFrequency() { return frequency; }
    public void setFrequency(BigDecimal frequency) { this.frequency = frequency; }
    public BigDecimal getTemperature() { return temperature; }
    public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
    public BigDecimal getTension() { return tension; }
    public void setTension(BigDecimal tension) { this.tension = tension; }
    public BigDecimal getStrainValue() { return strainValue; }
    public void setStrainValue(BigDecimal strainValue) { this.strainValue = strainValue; }
    public LocalDateTime getCollectTime() { return collectTime; }
    public void setCollectTime(LocalDateTime collectTime) { this.collectTime = collectTime; }
}
