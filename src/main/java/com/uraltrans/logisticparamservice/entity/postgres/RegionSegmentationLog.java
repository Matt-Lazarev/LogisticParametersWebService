package com.uraltrans.logisticparamservice.entity.postgres;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name="region_segmentation_logs")
public class RegionSegmentationLog {
    @Id
    private String uuid;

    @Column(length = 10485760)
    private String message;
}
