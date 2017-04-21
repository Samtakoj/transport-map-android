package com.samtakoj.shedule.model;

import com.samtakoj.shedule.model.converter.LongListConverter;
import com.samtakoj.shedule.model.converter.WorkDayConverter;

import java.io.Serializable;
import java.util.List;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Keep;
import io.objectbox.annotation.Generated;

/**
 * Created by artsiom.chuiko on 17/04/2017.
 */
@Entity
public class TimeCsv implements Serializable {

    @Id
    private Long id;
    private Long routeId;
    private Integer intervalCount;
    @Convert(converter = LongListConverter.class, dbType = String.class)
    private List<Long> timeTable;
    @Convert(converter = WorkDayConverter.class, dbType = String.class)
    private List<WorkDay> workDay;

    static final long serialVersionUID = 536871008;

    public TimeCsv() {}

    @Keep
    public TimeCsv(Long routeId, Integer intervalCount, List<Long> timeTable, List<WorkDay> workDay) {
        this.routeId = routeId;
        this.intervalCount = intervalCount;
        this.timeTable = timeTable;
        this.workDay = workDay;
    }

    @Generated(hash = 422421214)
    public TimeCsv(Long id, Long routeId, Integer intervalCount, List<Long> timeTable, List<WorkDay> workDay) {
        this.id = id;
        this.routeId = routeId;
        this.intervalCount = intervalCount;
        this.timeTable = timeTable;
        this.workDay = workDay;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Integer getIntervalCount() {
        return intervalCount;
    }

    public void setIntervalCount(Integer intervalCount) {
        this.intervalCount = intervalCount;
    }

    public List<Long> getTimeTable() {
        return timeTable;
    }

    public void setTimeTable(List<Long> timeTable) {
        this.timeTable = timeTable;
    }

    public List<WorkDay> getWorkDay() {
        return workDay;
    }

    public void setWorkDay(List<WorkDay> workDay) {
        this.workDay = workDay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeCsv timeCsv = (TimeCsv) o;

        if (id != null ? !id.equals(timeCsv.id) : timeCsv.id != null) return false;
        if (routeId != null ? !routeId.equals(timeCsv.routeId) : timeCsv.routeId != null)
            return false;
        if (intervalCount != null ? !intervalCount.equals(timeCsv.intervalCount) : timeCsv.intervalCount != null)
            return false;
        if (timeTable != null ? !timeTable.equals(timeCsv.timeTable) : timeCsv.timeTable != null)
            return false;
        return workDay != null ? workDay.equals(timeCsv.workDay) : timeCsv.workDay == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (routeId != null ? routeId.hashCode() : 0);
        result = 31 * result + (intervalCount != null ? intervalCount.hashCode() : 0);
        result = 31 * result + (timeTable != null ? timeTable.hashCode() : 0);
        result = 31 * result + (workDay != null ? workDay.hashCode() : 0);
        return result;
    }
}
