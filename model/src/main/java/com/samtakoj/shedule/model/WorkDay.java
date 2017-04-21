package com.samtakoj.shedule.model;

import java.io.Serializable;

/**
 * Created by artsiom.chuiko on 17/04/2017.
 */

public class WorkDay implements Serializable {

    private String weekDay;
    private Integer countInterval;
    static final long serialVersionUID = 536871008;

    public WorkDay() {}

    public WorkDay(String weekDay, Integer countInterval) {
        this.weekDay = weekDay;
        this.countInterval = countInterval;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public Integer getCountInterval() {
        return countInterval;
    }

    public void setCountInterval(Integer countInterval) {
        this.countInterval = countInterval;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkDay workDay = (WorkDay) o;

        if (weekDay != null ? !weekDay.equals(workDay.weekDay) : workDay.weekDay != null)
            return false;
        return countInterval != null ? countInterval.equals(workDay.countInterval) : workDay.countInterval == null;

    }

    @Override
    public int hashCode() {
        int result = weekDay != null ? weekDay.hashCode() : 0;
        result = 31 * result + (countInterval != null ? countInterval.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WorkDay{");
        sb.append("weekDay='").append(weekDay).append('\'');
        sb.append(", countInterval=").append(countInterval);
        sb.append('}');
        return sb.toString();
    }
}
