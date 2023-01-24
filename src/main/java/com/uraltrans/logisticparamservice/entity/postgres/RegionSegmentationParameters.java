package com.uraltrans.logisticparamservice.entity.postgres;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "region_segmentation_parameters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegionSegmentationParameters {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String token;
    private String sourceContragent;
    private Integer minLoadedFlightsAmount;
    private Integer minEmptyFlightsAmount;
    private Integer daysToRetrieveLoadedFlights;
    private Integer daysToRetrieveEmptyFlights;
    private Integer loadDays;
    private Integer unloadDays;
    private Integer maxSegments;
}
