package com.uraltrans.logisticparamservice.service.mapper.mapstruct;

import com.uraltrans.logisticparamservice.entity.itr.projection.ItrDislocationProjection;
import com.uraltrans.logisticparamservice.entity.postgres.NoDetailsWagon;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItrDislocationMapper {

    NoDetailsWagon toDislocation(ItrDislocationProjection itrDislocation);

    default List<NoDetailsWagon> toDislocationList(List<ItrDislocationProjection> itrDislocations){
        return itrDislocations
                .stream()
                .map(this::toDislocation)
                .peek(f -> {
                    if(f.getP02() == null){
                        f.setP02("");
                    }
                    if(f.getP06() == null){
                        f.setP06("");
                    }
                    if(f.getP20() == null){
                        f.setP20("");
                    }
                })
                .toList();
    }
}
