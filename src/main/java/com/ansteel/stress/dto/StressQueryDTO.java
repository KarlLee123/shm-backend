package com.ansteel.stress.dto;

public class StressQueryDTO {

    private String sensorId;

    /**
     * 格式固定：
     * yyyy-MM-dd HH:mm:ss
     */
    private String startTime;

    /**
     * 格式固定：
     * yyyy-MM-dd HH:mm:ss
     */
    private String endTime;

    /**
     * 前端可传，后端会做兜底和截断
     */
    private Integer limit;

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}