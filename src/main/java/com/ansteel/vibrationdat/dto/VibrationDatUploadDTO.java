package com.ansteel.vibrationdat.dto;

import java.math.BigDecimal;

public class VibrationDatUploadDTO {
    private String sensorId;
    private String deviceNo;
    private String fileName;
    private String filePath;
    private String collectTime;
    private Long fileSize;
    private String rawContent;
    private BigDecimal sampleRate;
    private Integer channelCount;
    private Integer pointCount;
    private BigDecimal durationSeconds;
    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
    public String getDeviceNo() { return deviceNo; }
    public void setDeviceNo(String deviceNo) { this.deviceNo = deviceNo; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getCollectTime() { return collectTime; }
    public void setCollectTime(String collectTime) { this.collectTime = collectTime; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getRawContent() { return rawContent; }
    public void setRawContent(String rawContent) { this.rawContent = rawContent; }
    public BigDecimal getSampleRate() { return sampleRate; }
    public void setSampleRate(BigDecimal sampleRate) { this.sampleRate = sampleRate; }
    public Integer getChannelCount() { return channelCount; }
    public void setChannelCount(Integer channelCount) { this.channelCount = channelCount; }
    public Integer getPointCount() { return pointCount; }
    public void setPointCount(Integer pointCount) { this.pointCount = pointCount; }
    public BigDecimal getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(BigDecimal durationSeconds) { this.durationSeconds = durationSeconds; }
}
