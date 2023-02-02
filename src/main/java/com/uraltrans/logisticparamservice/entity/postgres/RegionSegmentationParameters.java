package com.uraltrans.logisticparamservice.entity.postgres;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "region_segmentation_parameters")
public class RegionSegmentationParameters {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String token;

    @Column(name="source_contragent")
    private String sourceContragent;

    @Column(name="min_loaded_flights_amount")
    private Integer minLoadedFlightsAmount;

    @Column(name="min_empty_flights_amount")
    private Integer minEmptyFlightsAmount;

    @Column(name="days_to_retrieve_loaded_flights")
    private Integer daysToRetrieveLoadedFlights;

    @Column(name="days_to_retrieve_empty_flights")
    private Integer daysToRetrieveEmptyFlights;

    @Column(name="load_days")
    private Integer loadDays;

    @Column(name="unload_days")
    private Integer unloadDays;

    @Column(name="max_segments")
    private Integer maxSegments;

    @Column(name="source_data_t15")
    private String sourceDataT15;

    @Column(name="loaded_flights_filename")
    private String loadedFlightsFilename;

    @Column(name="empty_flights_filename")
    private String emptyFlightsFilename;

    @Column(name="load_time")
    private String loadTime;
}
