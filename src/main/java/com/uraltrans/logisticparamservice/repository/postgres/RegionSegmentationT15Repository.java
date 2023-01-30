package com.uraltrans.logisticparamservice.repository.postgres;

import com.uraltrans.logisticparamservice.entity.postgres.RegionSegmentationT15;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RegionSegmentationT15Repository extends JpaRepository<RegionSegmentationT15, Integer> {

    @Query("select distinct rs from RegionSegmentationT15 rs inner join fetch rs.segments")
    List<RegionSegmentationT15> findAllDistinct();

    @Modifying
    @Transactional
    @Query(value = "truncate table t15 restart identity cascade", nativeQuery = true)
    void truncateT15Table();

}
