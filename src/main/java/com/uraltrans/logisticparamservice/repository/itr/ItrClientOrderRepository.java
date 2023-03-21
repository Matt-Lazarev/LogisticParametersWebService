package com.uraltrans.logisticparamservice.repository.itr;

import com.uraltrans.logisticparamservice.entity.itr.ItrClientOrder;
import com.uraltrans.logisticparamservice.entity.itr.projection.ItrClientOrderProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItrClientOrderRepository extends JpaRepository<ItrClientOrder, Integer> {
    @Query(value =
        """
            Select ST.CodeForTrain as CarNumber,
            cast (o.CARS as integer) as CarsAmount,
            C.Name  [Cargo],
            C.CODE6 as [CargoCode],
            Kl.Name [Client],
            dbo.Get_Fio(O.ID_User_Ins, 2) [Manager],
            O.Data DateFrom,
            O.DateEnd DateTo,
            CarType = Cast(ct.ShortName as varchar(1000)),
            Dds.name DocumentStatus,
            VolumeFrom = o.V1,
            VolumeTo = o.V2,
            SourceStationCode6 = tool.TryGetCode6ByStation(sf.Code6),
            DestinationStationCode6 = tool.TryGetCode6ByStation(st.Code6),
        
            cast(
                   (select
                        UT_Rate = case when p_.Is_Dep_Dis=3 then kst.X when p_.Is_Dep_Dis=4 then kst2.X else IsNull(u.S_AG, p_.S_AG) end
                    from res_copy r
                             left join dbo.pe_agr_klient_pred p_ on p_.ID = r.PredID
                             outer apply(select top 1 kst.X as X from KL_PRED_STATIONS_TO kst
                                                                          inner join  qstation stto on kst.Id_Station=stto.AID
                                         where kst.ID_KL_PREDMET=p_.ID and stto.Code6=r.StationCode6To) kst
                             outer apply(select top 1 kst.X as X from Calc.KlientPredRoute kst
                                                                          inner join  qstation stto on kst.StationID = stto.AID
                                         where kst.PredID = p_.ID and kst.StationTypeID = 4 and stto.Code6=r.StationCode6To) kst2
                             outer apply
                         (
                             select top 1 p.S_AG, p.type_calc
                             from do_Document do
                                      left join DO_DOCUMENT_ADD da on da.ID_DOCUMENT = do.aid
                                      left join do_Document d on d.aid = da.ID_DOCUMENT_ADD and d.id_doc = 50 -- приложение
                                      left join dbo.PE_AGR_KLIENT_PRED p on p.IDParentDocument = d.aid and p.id=r.PredID /*Трунов 23.04.2019 когда несколько УТ в приложении*/
                             where do.aid = r.DocumentID
                             order by d.DATE_INS desc
                         ) u
                    where aid = O.AID)
               as decimal(18,2)) as UtRate

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
        
                 left join dbo.Do_Document ddOrder on ddOrder.Aid_Doc = o.AID and ddOrder.ID_Doc = 1
                 LEFT JOIN Orders.OrderKindDef OrderKindDef ON O.OrderKindId = OrderKindDef.aid
                 left join DO_Document_Status Dds On ddOrder.State = Dds.AID
                 left join DO_MAR On ddOrder.ID_MAr = DO_MAR.AID
        
                 left join do_document dpril on dpril.aid = o.DcoumentID_Pril
                 left join dCarGroup dcg on dcg.ID = o.GroupID
                 left join dbo.dcontragent dcf on o.factoryId = dcf.id
                 left join dbo.TransportationTypeDef ttd on ttd.ID = o.TransportationTypeId
                 left join Orders.TransportationKindDef tkd on tkd.ID = o.TransportationKindID
                 left join LIST_USER On LIST_USER.AID = O.ID_USER_INS
                 left join dStruct On dStruct.ID = LIST_USER.ID_STRUCT
            WHERE
                 Dds.name = :status and
                 Cast(ct.ShortName as varchar(1000)) = :carType and
                 O.Data >= :dateFrom and
                 Org.name = N'УРАЛЬСКАЯ ТРАНСПОРТНАЯ КОМПАНИЯ'
            """, nativeQuery = true)
    List<ItrClientOrderProjection> getAllOrders(String status, String carType, String dateFrom);
}
