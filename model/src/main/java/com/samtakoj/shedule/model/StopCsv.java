package com.samtakoj.shedule.model;

import com.samtakoj.schedule.common.data.Position;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Generated;
import io.objectbox.annotation.Keep;

/**
 * Created by artsiom.chuiko on 17/04/2017.
 */
@Entity
public class StopCsv {
    @Id(assignable = true)
    private Long id;
    private String name;
    private Long lng;
    private Long ltd;

    @Keep
    public StopCsv(@Position(1) Long id, @Position(5) String name, @Position(7) Long lng, @Position(8) Long ltd) {
        this.id = id;
        this.name = name;
        this.lng = lng;
        this.ltd = ltd;
    }

    @Generated(hash = 132338458)
    public StopCsv() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getLng() {
        return lng;
    }

    public void setLng(Long lng) {
        this.lng = lng;
    }

    public Long getLtd() {
        return ltd;
    }

    public void setLtd(Long ltd) {
        this.ltd = ltd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StopCsv stopCsv = (StopCsv) o;

        if (!id.equals(stopCsv.id)) return false;
        if (!name.equals(stopCsv.name)) return false;
        if (lng != null ? !lng.equals(stopCsv.lng) : stopCsv.lng != null) return false;
        return ltd != null ? ltd.equals(stopCsv.ltd) : stopCsv.ltd == null;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (lng != null ? lng.hashCode() : 0);
        result = 31 * result + (ltd != null ? ltd.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StopCsv{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lng=" + lng +
                ", ltd=" + ltd +
                '}';
    }
}