declare @DefaultCountryID int = 8
Select O.AID,ddOrder.STATE as [Статус],
       O.NUM [Номер заявки],
       O.DOCDATA [Дата заявки],
       SF.Name [Станция отправления],
       SF.Name [Ст. отправления],
       ST.CodeForTrain as CarNumber,
       ST.Name as [Станция назначения],
    [Количество вагонов] = o.CARS,
        case when KS.Name is not null and len(crs.CargoSenderNames) > 0 then KS.Name + ', ' else IsNull(KS.Name,'') end + crs.CargoSenderNames [Грузоотправитель],
    KS.OKPO as [Грузоотправитель ОКПО],
    KR.NAME [Грузополучатель],
    KD.Name [Дислокатор],
    C.Name  [Груз],
    Kl.Name [Клиент],
    AddDocs.Num [Номера счетов],
    AddDocs.itogo [Выставлено],
    AddDocs.upl [Оплачено],
        AddDocs.itogo - AddDocs.Upl as [Разница (Выставлено-Оплачено)],
    Round((SELECT Cast(getDate() - MIN((DOPLATA) ) - 1 AS float)
           FROM vAccount
           WHERE vAccount.Debet > 0 and
                   vAccount.IDParentDocument = ddOrder.AID),0)   [Просрочено дней],
    /**/
    -- dbo.Договор_Сальдо(O.ID_AGR,GetDate(), 0, default) [Текущее сальдо],
    cp.Name [Проект],
    'Предоплата 100%' [Условия оплаты],
    dbo.Get_Fio(O.ID_User_Ins, 2) [Менеджер],
    O.DATE_INS,
    O.DATE_LAST,
    o.ID_DocState,
    O.Data Date_B,
    O.DateEnd Date_e,
    O.ID_Agr, O.ID_KLIENT, ddOrder.AID as IDDocument, ddOrder.AID as DocumentID,
    O.OrderKindId,
    OrderKindDef.Name OrderKindName,Org.name as Firma,
    o.id_cartype,
    CarTypeName = Cast(ct.ShortName as varchar(1000)),
    CodeRailWayFrom = rw_f.code,
    RailWayFrom = rw_f.Name,
    CodeRailWayTo = rw_t.code,
    RailWayTo = rw_t.Name,
    StationCodeFrom = sf.Code,Agr.NUM as AGR_NUM,
    Dds.name DocumentStateName,
    DO_MAR.name RouteName,
    isNull(Ev1.Flag,0) SsignedFromContractor ,
    isNull(Ev2.Flag,0) SsignedFromOur,
    CurrentSL.Fio,o.VES,
    MinusSumma,
    MinusSummaNds,
    PlusSumma,
    PlusSummaNds,
        isnull(PlusSumma, 0) - isnull(MinusSumma, 0) MergSumma,
    isnull(o.IsFlowDeposit,0) as IsFlowDeposit,
    case when o.type_payer=0 then 'Наша фирма' when o.type_payer=1 then 'Клиент' else '3-е лицо' end as typepayer,
    Case when Stat.AID is not null then IsNull(Stat.AVGTurnEmptyCar,0) + IsNull(Stat.AVGTurnLoadedCar,0) when o.Oborot <> 0 then o.Oborot End AVGTurnCar ,
    Round(Case when Stat.AID is not null then (IsNull(PlusSumma, 0) - IsNull(MinusSumma, 0))/(IsNull(Stat.AVGTurnEmptyCar,0) + IsNull(Stat.AVGTurnLoadedCar,0)) End,0)  Merg1Day,
    DocVersion,
    case when Cor.AID is Null then 0 else 1 end IsCorrect,
    -- 03/08/2015 Ванина, ИД самого первого родителя по скорректированным заявкам
    IsNull(parent.aid, o.aid) as ParentOrderAID, -- 11/08/2015 Ванина, для ВСТ по просьбе Апасовой, если нет родителя, то указываем саму себя
    -- 08/09/2015 Ванина,
    lastchange_all.IsAdd as IsAdd,
    abs(lastchange_all.IsMin) as IsMin,  ddOrder.Note,
    O.AddTovarList,
    Cast(0 as decimal(12,3)) FactWeight,
    Cast(0 as int) FactCars,
    Cast(0 as varchar(5000)) GU12,
    AddDocs.nds_name as acc_nds_name,
    AddDocs.data as acc_data,
    cast(null as varchar(2000)) as claim_DB,
    cast(null as varchar(2000)) as claim_DE,
    dpril.ShortAbout as PrilAbout,
    cast(null as decimal(18,2)) as UT_Rate,
    cast(null as varchar(400)) as UT_TypeObject,
    cast(null as varchar(200)) as Flow_About,
    AddDocs.flow_note,
    cast(null as decimal(18,2)) as [Разница (Начислено-Оплачено)],
    cast(null as decimal(18,2)) as Calc_Summa,
    Cast(0 as decimal(12,3)) DeltaWeight,
    Cast(0 as int) DeltaCars,
    o.PredID,
    --dcg.Name as GroupName,
    GroupName = dcg.Name,
    dcf.name as Factory,
    o.DryTransShipment,
    cast(0 as bit) ISGrafikExists,
    VolumeFrom = o.V1,
    VolumeTo = o.V2,
    StationCode6From = tool.TryGetCode6ByStation(sf.Code6),
    StationCode6To = tool.TryGetCode6ByStation(st.Code6),
    PunktNameTo = adrTo.name,
    ttd.Name as [Тип перевозки],
    case when o.TransportationTypeId = 2 then o.PlannedPlatformsQTY else 0 end as [Плановое количество платформ],--только для перевозки "В контейнере"
    Distance.DISTANCE,
    cast(null as varchar(2048)) Grafik,
    C.CODE6 as [Код груза],
    AddDocs.flow_num,
    AddDocs.flow_data,
    o.IsPlan,
    SourceRoadDepartmentName = ssdep.Name,
    gu11.NumberGU11,
    tkd.Name as TransportationKind,
    --14042020
    Case
           /*07092021
           в заявках на перевозку необходимо определять вид перевозки со станций отправления
                Алтынколь (эксп.), Алтынколь (эксп. перев. авто), Достык (эксп.), Достык (эксп. перев.), Достык (эксп. перев. авто):
           на станции назначения КЗХ и Петропавловск, Мамлютка, Булаево (дорога ЮУР) как импорт
           на станции назначения  не КЗХ и Илецк I, Неверовская, Третьяково, Маймак, Мактаарал (дорога КЗХ) как транзит
           */
        when @DefaultCountryID = 8 and SF.Code in (70780,70770,71010,70850,71040) and (St.LocationCountryID = @DefaultCountryID or St.Code in (82000,82620,82520)) then 'импорт'
        when @DefaultCountryID = 8 and SF.Code in (70780,70770,71010,70850,71040) And (St.LocationCountryID <> @DefaultCountryID or ST.Code in (66690,71170,71190,70600, 69700)) then 'транзит'
        when @DefaultCountryID = 8 and Isnull(SF.RealCountryID, SF.LocationCountryID) = @DefaultCountryID and Isnull(SF.RealCountryID, St.LocationCountryID) = @DefaultCountryID then 'внутриказахстанская'
           --Select * from dbo.QStation where Name in  ( 'Алтынколь (эксп.)', 'Алтынколь (эксп. перев. авто)','Достык (эксп.)', 'Достык (эксп., перев.)', 'Достык (эксп. перев. авто)')
           --Select * from dbo.QStation where Code in (70780,70770,71010,70850,71040)
        when Isnull(SF.RealCountryID, SF.LocationCountryID) = @DefaultCountryID and Isnull(SF.RealCountryID, St.LocationCountryID) = @DefaultCountryID then 'внутрироссийская'
        when Isnull(SF.RealCountryID, SF.LocationCountryID) = @DefaultCountryID and Isnull(SF.RealCountryID, St.LocationCountryID) <> @DefaultCountryID then 'экспорт'
        when Isnull(SF.RealCountryID, SF.LocationCountryID) <> @DefaultCountryID and Isnull(SF.RealCountryID, St.LocationCountryID) = @DefaultCountryID then 'импорт'
        when Isnull(SF.RealCountryID, SF.LocationCountryID) <> @DefaultCountryID and Isnull(SF.RealCountryID, St.LocationCountryID) <> @DefaultCountryID then 'транзит'
           end as TranKindName,
    Cast(0 as int) SanctionedCargoCount,
    PunktNameFrom = adrFrom.name, -- Трунов 27.08.2020
    /*ToloknovDV 31.08.2020*/
    CountryFromName = convert(nvarchar(512), rcf.ShortName),
    CountryToName = convert(nvarchar(512), rct.ShortName),
    ChinaTrainNumber = o.ChinaTrainNumber,
    CarrierInPoland  = cip.Name,
    loaded = cast('Гр.' as varchar(50)),
    o.DateLastManual, o.ID_USER_INS,
    Cast(null as int) as ContCount, --количество контейнеров
    o.IsPreferentialRate,
    CAST(null as nvarchar(1000)) Note2,
    CAST(null as nvarchar(2000)) Note3,

    /*ToloknovDV 26.03.2021*/
    CarCount_Decade1 = fnGrafik.CarCountD1,
    CarCount_Decade2 = fnGrafik.CarCountD2,
    CarCount_Decade3 = fnGrafik.CarCountD3,
    /*СабировТЖ 12042021*/
    --Cfm.Limit [Лимит, договор],
    Cast(null as decimal(18,2)) as [Лимит, договор],
    --Cfm.Limit - Cfm.Sld [Лимит - Сальдо],
    Cast(null as decimal(18,2)) as [Лимит - Сальдо],
    --Cfm.CurrentOrderBlockSum []
    Cast(null as decimal(18,2)) as [Заблокировано,по ЗНП],
    Cast(null as decimal(18,2)) as [Заблокировано,по прочим ЗНП],
    Cast(null as decimal(18,2)) as [ДФЭ, план],
    Cast(null as decimal(18,2)) as [ДФЭ, факт],
    Cur.Name [Валюта договора],
    dStruct.Name [Подразделение, инициатор],
    Cast(null as varchar(20)) as PredCur,
    Cast(O.IsNotOwnPark as bit) [Чужой парк],
    convert(bit, case when fnUnionSending.Number is not null then 1 else 0 end) as UnionSendingIsParent,
    convert(nvarchar(512), fnUnionSending.Number) as UnionSendingNumber,
    convert(nvarchar(max), fnUnionSending.OrderNumbers) as UnionSendingOrderNumbers,
    /*
        2021-12-30, Михайлов С.А. Добавлены по заявке З-0190/21 в OTLK
        SalesBillAmount AmountPaymentDif PreliminaryAmount AcntPreliminaryAmount IsOverpayment
    */
    cast(null as decimal(18,2)) as SalesBillAmount,
    cast(null as decimal(18,2)) as AmountPaymentDif,
    cast(null as decimal(18,2)) as PreliminaryAmount,
    cast(null as decimal(18,2)) as AcntPreliminaryAmount,
    cast(0 as bit) as IsOverpayment,
    o.NumTELF,
    o.FactSendDate,
    o.tag1,
    o.InAktsRub,
    o.InAktsNoRub,
    o.DivAktsAccRub,
    o.DivAktsAccNoRub,
    cast(null as decimal(18,2)) as AmountOverpayment,
    cast(null as nvarchar(128)) as OverpaymentByOrders,

    /*ToloknovDV 02.06.2022*/
    AmountPlannedCrediting = convert(decimal(18,2), null),
    ActDates = convert(varchar(2064), null),

    cast(null as nvarchar(255)) as [Статус вагонов]

    ---Select * from Orders.CheckingForMoney where OrderID = 7039
    --Update QStation Set RealCountryID = 8 where Code6 in (715500,820001,825202, 826205)
    --Select * from QStation where Name like 'Мамлю%'
    --Select * from QStation where Code6 in (715500,820001,825202, 826205)

from dbo.AGR_KLIENT_ORDERS O
         left join Cont.BalancingDef Cbd On Cbd.aid = O.BalancingID
         left join dkrinterop.gu11 gu11 on gu11.id = o.id_gu11
         LEFT JOIN CN_PROJECTS cp On o.ID_GROUP = cp.ID
         left join dbo.dCarType ct on ct.Id = o.ID_CARTYPE
         Left Join dbo.QStation SF On O.ID_ST_FROM = SF.AID
         left join dbo.qRoadDepartment ssdep on ssdep.AID = SF.RoadDepartmentID
         Left Join dbo.qRailway rw_f On rw_f.Code = sf.CodeRailway
         Left Join dbo.QStation ST On O.ID_ST_TO = ST.AID
         Left Join dbo.qRailway rw_t On rw_t.Code = st.CodeRailway
         left join Distance On Distance.ID_ST_FROM = Sf.AID and Distance.ID_ST_TO = st.AID
         Left Join dbo.dContrAgent KS On O.ID_CargoSender = KS.ID
         Left Join dbo.dContrAgent KR On O.ID_CargoReciver = KR.ID
         Left Join dbo.dContrAgent KD On O.ID_Dislokator = KD.ID
         Left Join dbo.qCargo C On O.ID_Cargo = C.CODE
         Left Join dbo.dAgreements Agr On O.ID_Agr = Agr.ID
         left join dbo.Currencies Cur On Cur.Code = Agr.CodeCurrency
         left join RefBook.address adrTo on adrTo.ID = o.PunktID_To
         left join RefBook.address adrFrom on adrFrom.ID = o.PunktID_From
         Left Join dbo.dContragent Kl On O.ID_Klient = Kl.ID
         Left Join dbo.dContragent Org On O.ID_FIRM = ORG.ID
         left join Orders.CarrierInPolandDef cip on cip.Id = o.CarrierInPolandID
         left join Refbook.Country rcf on rcf.Id = o.CountryID_From
         left join Refbook.Country rct on rct.Id = o.CountryID_To

    /*ToloknovDV 07.10.2021*/
         outer apply(select Number = max(_ush.Number), OrderNumbers = Tool.ConcatDelimited(distinct _ako.Num, ', ')
                     from Orders.UnionSendingBody _usb
                              inner join Orders.UnionSendingHead _ush on _ush.AID = _usb.UnionSendingHeadID
                              inner join Orders.UnionSendingBody _usb2 on _usb2.UnionSendingHeadID = _ush.AID
                              inner join dbo.AGR_KLIENT_ORDERS _ako on _ako.AID = _usb2.OrderID
                     where
                             _usb.OrderID = o.AID)fnUnionSending

    /*Заявка документ*/
         left join dbo.Do_Document ddOrder on ddOrder.Aid_Doc = o.AID and ddOrder.ID_Doc = 1
         LEFT JOIN Orders.OrderKindDef OrderKindDef ON O.OrderKindId = OrderKindDef.aid
         left join DO_Document_Status Dds On ddOrder.State = Dds.AID
         left join DO_MAR On ddOrder.ID_MAr = DO_MAR.AID

    /*ToloknovDV 26.03.2021*/
         outer apply(
    select
        CarCountD1 = sum(case
                             when _g.db between tool.StartOfTheMonth(_o.Data) and dateadd(day, 9, tool.StartOfTheMonth(_o.Data)) --and
                             /*_g.de between tool.StartOfTheMonth(_o.Data) and dateadd(day, 9, tool.StartOfTheMonth(_o.Data))*/ then _g.QTY else 0 end),
        CarCountD2 = sum(case
                             when _g.db between dateadd(day, 10, tool.StartOfTheMonth(_o.Data)) and dateadd(day, 19, tool.StartOfTheMonth(_o.Data)) --and
                             /*_g.de between dateadd(day, 10, tool.StartOfTheMonth(_o.Data)) and dateadd(day, 19, tool.StartOfTheMonth(_o.Data))*/ then _g.QTY else 0 end),
        CarCountD3 = sum(case
                             when _g.db between dateadd(day, 20, tool.StartOfTheMonth(_o.Data)) and tool.EndOfTheMonth(_o.Data) --and
                             /*_g.de between dateadd(day, 20, tool.StartOfTheMonth(_o.Data)) and tool.EndOfTheMonth(_o.Data)*/ then _g.QTY else 0 end)
    from dbo.AKO_GRAFIK _g
             inner join dbo.AGR_KLIENT_ORDERS _o on _o.AID = _g.ID_ORDER
    where
            _g.ID_ORDER = o.AID)fnGrafik

         outer apply(Select top 1 1 Flag from DO_DOCUMENT_FILES Files where Files.AID_DOC = ddOrder.AID and Files.Status in (11,10) ) Ev1
         outer apply(Select top 1 1 Flag from DO_DOCUMENT_FILES Files where Files.AID_DOC = ddOrder.AID and Files.Status in (12,10) ) Ev2
         outer apply(Select top 1 dbo.GET_FIO(Sl.ID_RECIVER, 2) FIO from DO_DOCUMENT_SL SL where SL.ID_DOC = ddOrder.AID and Sl.APPLY = 0 ) CurrentSL
         outer apply(Select tool.concat(DD.NUM) as num, sum(ad.Itogo) Itogo, sum(ad.UPL) upl, tool.concat(distinct ad.nds_name) as nds_name, tool.concat(distinct ad.data) as data,
                            tool.concat(distinct ad.flow_num) as flow_num, tool.concat(distinct ad.flow_data) as flow_data, tool.concat(distinct ad.flow_note) as flow_note
                     from DO_DOCUMENT_ADD [Add]
                              inner join DO_DOCUMENT DD on [Add].ID_DOCUMENT_ADD = DD.AID and DD.ID_DOC = 18
                              outer apply
                          (
                              select ad.SUMMA as Itogo, ad.UPL upl, ad.nds_name, convert(varchar, ad.data, 104) as data,
                                     convert(varchar, ad.flow_data, 104) as flow_data,
                                     ad.flow_num, ad.flow_note
                              FROM [dbo].[vAccount] ad
                              where ad.AID = DD.AID_DOC
                          ) ad
                     where [Add].ID_DOCUMENT = ddOrder.AID
) AddDocs
    --select * from Orders.OrderCharge
         outer apply(Select Sum(Case when IsIncome = 0 and (IsNull(ExpWhoPayID, 1) = 1 /*or db_Name() in ('RAS','RAS2')*/) then Summa end) MinusSumma, -- RAS сделал Трунов по просьбе Крупенина
                            Sum(Case when IsIncome = 0 and (IsNull(ExpWhoPayID, 1) = 1 /*or db_Name() in ('RAS','RAS2')*/) then Summands end) MinusSummaNds,
                            Sum(Case IsIncome when 1 then Summa end) PlusSumma,
                            Sum(Case IsIncome when 1 then Summands end) PlusSummaNds
                     from Orders.OrderCharge
                     where documentID = ddOrder.AID
                       and (TypeCostsID<>21 or db_name() not in ('RAS','RAS2'))) [Plan]
         outer apply(select top 1 * from Orders.OrderStatistics Stat where Stat.OrderID = O.AID and Stat.IsActual = 1) Stat
         outer apply(select top 1 a.AID from AGR_KLIENT_ORDERS a
                                                 inner join DO_DOCUMENT dd on dd.AID_DOC = a.AID and dd.ID_DOC = 1 and dd.STATE <> 4 --26.01.2016 Ванина. отклоненные не учитываем
                     where a.ParentDocumentID  = ddOrder.aid and IsNull(a.DocVersion,0) > 0) Cor
         outer apply(select top 1 ako.AID, dd.aid as MainDocAID from DO_DOCUMENT_ADD dda
                                                                         inner join DO_DOCUMENT dd on dd.AID = dda.ID_DOCUMENT_ADD and dd.ID_DOC = 1
                                                                         inner join AGR_KLIENT_ORDERS ako on ako.aid = dd.aid_doc and IsNull(ako.DocVersion, 0) = 0
                     where dda.ID_DOCUMENT = ddOrder.aid
                       and IsNull(o.DocVersion,0) > 0) parent
         outer apply  /*
        (
            select
                sum(case when ako.CARS - prev.CARS > 0 then ako.CARS - prev.CARS else 0 end) as IsAdd,
                sum(case when ako.CARS - prev.CARS < 0 then ako.CARS - prev.CARS else 0 end) as IsMin
            from DO_DOCUMENT_ADD dda
            inner join DO_DOCUMENT dd on dd.AID = dda.ID_DOCUMENT_ADD and dd.ID_DOC = 1
            inner join AGR_KLIENT_ORDERS ako on ako.aid = dd.aid_doc and IsNull(ako.DocVersion, 0) <= IsNull(o.DocVersion, 0)
            cross apply
                (
                    select top 1 o_.CARS
                    from DO_DOCUMENT dd_
                    inner join AGR_KLIENT_ORDERS o_ on o_.aid = dd_.aid_doc and dd_.id_doc = 1
                    where dd_.aid = ako.ParentDocumentID
                    order by o_.date_ins desc
                ) prev -- накопленные снято и добавлено
            where dda.ID_DOCUMENT = parent.MainDocAID
        ) lastchange_all
        */
    ( --24022021 ALN, относительно пред версии
        select
            case when o.CARS - prev.CARS > 0 then o.CARS - prev.CARS else 0 end as IsAdd,
            case when o.CARS - prev.CARS < 0 then o.CARS - prev.CARS else 0 end as IsMin
        from DO_DOCUMENT dd_
                 inner join AGR_KLIENT_ORDERS prev on prev.aid = dd_.aid_doc and dd_.id_doc = 1
        where dd_.aid = o.ParentDocumentID
    ) lastchange_all
         outer apply
     (
         select tool.concat(crs_dc.name) as CargoSenderNames
         from dbo.строкавтаблицу(o.CargoSenderIds) crs
                  inner join dContragent crs_dc on crs_dc.id = crs.Number
     ) crs
         left join do_document dpril on dpril.aid = o.DcoumentID_Pril
         left join dCarGroup dcg on dcg.ID = o.GroupID
         left join dbo.dcontragent dcf on o.factoryId = dcf.id
         left join dbo.TransportationTypeDef ttd on ttd.ID = o.TransportationTypeId
         left join Orders.TransportationKindDef tkd on tkd.ID = o.TransportationKindID
         left join LIST_USER On LIST_USER.AID = O.ID_USER_INS
         left join dStruct On dStruct.ID = LIST_USER.ID_STRUCT
WHERE
    Dds.name = ? and
    Cast(ct.ShortName as varchar(1000)) = ? and
    O.Data >= ? and
    Org.name = N'УРАЛЬСКАЯ ТРАНСПОРТНАЯ КОМПАНИЯ';