package com.ansteel.strain.dto;

public class StrainQueryDTO {

    private String sensorId;

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    private String startTime;

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    private String endTime;

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