package com.uraltrans.logisticparamservice.entity.itr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ItrFlight {
    @Id
    @Column(name = "AID")
    private Integer aid;

    @Column(name = "PrevFlightID")
    private Integer prevFlightAid;

    @Column(name = "CarNumber")
    private Integer carNumber;

    @Column(name = "InvNumber")
    private String invNumber;

    @Column(name = "PrevInvNumber")
    private String prevInvNumber;

    @Column(name = "NextInvNumber")
    private String nextInvNumber;

    @Column(name = "SendDate")
    private Timestamp sendDate;

    @Column(name = "SourceStation")
    private String sourceStation;

    @Column(name = "FVrh_3")
    private String sourceStationCode;

    @Column(name = "DestStation")
    private String destStation;

    @Column(name = "FVrh_4")
    private String destStationCode;

    @Column(name = "Cargo")
    private String cargo;

    @Column(name = "CargoCode6")
    private String cargoCode6;

    @Column(name = "CarType")
    private String carType;

    @Column(name = "DateInDate")
    private Timestamp arriveToDestStationDate;

    @Column(name = "DateIn")
    private Timestamp arriveToSourceStationDate;

    @Column(name = "DateOut")
    private Timestamp departureFromSourceStationDate;

    @Column(name = "Volume")
    private BigDecimal volume;

    @Column(name = "Distance")
    private BigDecimal distance;

    @Column(name = "Loaded")
    private String loaded;

    @Column(name = "Отправление со ст. назн.")
    private Timestamp departureFromDestStationDate;

    @Column(name = "Выгрузка на ст. назн.")
    private Timestamp unloadOnDestStationDate;

    @Column(name = "FlightKind")
    private String flightKind;

    @Column(name = "Дата нач. след. Рейса (дата оформления вагона порожним)")
    private Timestamp nextFlightStartDate;

    @Column(name = "IsNotFirstEmpty")
    private Boolean isNotFirstEmpty;

    @Column(name = "SourceRailway")
    private String sourceRailway;

    @Column(name = "DestRailway")
    private String destRailway;

    @Column(name = "SourceContragent")
    private String sourceContragent;

    @Column(name = "Klient")
    private String client;

    @Column(name = "Tag2")
    private String tag2;

    @Column(name = "Tag22")
    private String tag22;
}
