package com.samtakoj.shedule.model;

import com.samtakoj.schedule.common.data.Position;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Keep;

/**
 * Created by artsiom.chuiko on 17/04/2017.
 */
@Entity
public class RouteCsv {
    private String num;
    private String transportType;
    private String name;
    private String weekDays;
    @Id(assignable = true)
    private Long id;
    private String stops;

    @Keep
    public RouteCsv(@Position(1) String num, @Position(4) String transportType, @Position(11) String name,
                    @Position(12) String weekDays, @Position(13) Long id, @Position(15) String stops) {
        this.num = num;
        this.transportType = transportType;
        this.name = name;
        this.weekDays = weekDays;
        this.id = id;
        this.stops = stops;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(String weekDays) {
        this.weekDays = weekDays;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStops() {
        return stops;
    }

    public void setStops(String stops) {
        this.stops = stops;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RouteCsv routeCsv = (RouteCsv) o;

        if (!num.equals(routeCsv.num)) return false;
        if (!transportType.equals(routeCsv.transportType)) return false;
        if (!name.equals(routeCsv.name)) return false;
        if (weekDays != null ? !weekDays.equals(routeCsv.weekDays) : routeCsv.weekDays != null)
            return false;
        if (!id.equals(routeCsv.id)) return false;
        return stops.equals(routeCsv.stops);

    }

    @Override
    public int hashCode() {
        int result = num.hashCode();
        result = 31 * result + transportType.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (weekDays != null ? weekDays.hashCode() : 0);
        result = 31 * result + id.hashCode();
        result = 31 * result + stops.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RouteCsv{");
        sb.append("num='").append(num).append('\'');
        sb.append(", transportType='").append(transportType).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", weekDays='").append(weekDays).append('\'');
        sb.append(", id=").append(id);
        sb.append(", stops='").append(stops).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
