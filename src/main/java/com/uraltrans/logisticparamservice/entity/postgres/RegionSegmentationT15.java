package com.uraltrans.logisticparamservice.entity.postgres;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="t15")
public class RegionSegmentationT15 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private BigDecimal volume;

    @Column(name="segment_type")
    private String segmentType;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "region_segmentation_id", referencedColumnName = "id")
    private List<RegionSegment> segments;
}
