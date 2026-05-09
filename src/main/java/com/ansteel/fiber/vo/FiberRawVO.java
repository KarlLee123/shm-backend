package com.ansteel.fiber.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FiberRawVO {
    private String sensorId;
    private String deviceNo;
    private String fiberNo;
    private BigDecimal rawValue;
    private BigDecimal wavelength;
    private BigDecimal wavelengthShift;
    private BigDecimal intensity;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime collectTime;

    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
    public String getDeviceNo() { return deviceNo; }
    public void setDeviceNo(String deviceNo) { this.deviceNo = deviceNo; }
    public String getFiberNo() { return fiberNo; }
    public void setFiberNo(String fiberNo) { this.fiberNo = fiberNo; }
    public BigDecimal getRawValue() { return rawValue; }
    public void setRawValue(BigDecimal rawValue) { this.rawValue = rawValue; }
    public BigDecimal getWavelength() { return wavelength; }
    public void setWavelength(BigDecimal wavelength) { this.wavelength = wavelength; }
    public BigDecimal getWavelengthShift() { return wavelengthShift; }
    public void setWavelengthShift(BigDecimal wavelengthShift) { this.wavelengthShift = wavelengthShift; }
    public BigDecimal getIntensity() { return intensity; }
    public void setIntensity(BigDecimal intensity) { this.intensity = intensity; }
    public LocalDateTime getCollectTime() { return collectTime; }
    public void setCollectTime(LocalDateTime collectTime) { this.collectTime = collectTime; }
}
