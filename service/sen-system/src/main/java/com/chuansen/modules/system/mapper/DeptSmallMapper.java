package com.chuansen.modules.system.mapper;
import com.chuansen.mapper.EntityMapper;
import com.chuansen.modules.system.entity.Dept;
import com.chuansen.modules.system.entity.dto.DeptSmallDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring",uses = {},unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeptSmallMapper extends EntityMapper<DeptSmallDTO, Dept> {

}