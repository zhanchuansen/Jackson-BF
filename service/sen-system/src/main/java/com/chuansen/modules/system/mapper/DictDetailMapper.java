package com.chuansen.modules.system.mapper;

import com.chuansen.mapper.EntityMapper;
import com.chuansen.modules.system.entity.DictDetail;
import com.chuansen.modules.system.entity.dto.DictDetailDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring",uses = {},unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DictDetailMapper extends EntityMapper<DictDetailDTO, DictDetail> {

}