package com.uraltrans.logisticparamservice.service.mapper.mapstruct;

import com.uraltrans.logisticparamservice.entity.postgres.SegmentationAnalysisT14;
import com.uraltrans.logisticparamservice.entity.rjd.SegmentationAnalysisT13;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SegmentationAnalysisT14Mapper {
    List<SegmentationAnalysisT14> mapToSegmentsT14List(List<SegmentationAnalysisT13> segmentsT13);
}
