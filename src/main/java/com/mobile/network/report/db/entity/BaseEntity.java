package com.mobile.network.report.db.entity;

import com.mobile.network.report.utils.DateTimeUtils;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    private UUID id;
    private Instant createInstant;
    private Instant updateInstant;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        Instant instant = DateTimeUtils.currentInstant();
        this.createInstant = instant;
        this.updateInstant = instant;
    }

    @PreUpdate
    public void preUpdate() {
        this.updateInstant = DateTimeUtils.currentInstant();
    }

}
