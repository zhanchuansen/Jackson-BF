package com.chuansen.modules.system.mapper;

import com.chuansen.mapper.EntityMapper;
import com.chuansen.modules.system.entity.Role;
import com.chuansen.modules.system.entity.dto.RoleSmallDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", uses = {}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleSmallMapper extends EntityMapper<RoleSmallDTO, Role> {

}
