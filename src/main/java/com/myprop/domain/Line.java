package com.myprop.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Line.
 */
@Entity
@Table(name = "mp_line")
public class Line implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "source_id")
    private String sourceId;

    @Column(name = "source_type")
    private String sourceType;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "timestamp")
    private LocalDate timestamp;

    @Column(name = "display_name")
    private String displayName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        if(line.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Line{" +
            "id=" + id +
            ", sourceId='" + sourceId + "'" +
            ", sourceType='" + sourceType + "'" +
            ", active='" + active + "'" +
            ", timestamp='" + timestamp + "'" +
            ", displayName='" + displayName + "'" +
            '}';
    }
}
