package com.chuansen.modules.system.mapper;

import com.chuansen.mapper.EntityMapper;
import com.chuansen.modules.system.entity.Permission;
import com.chuansen.modules.system.entity.dto.PermissionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionMapper extends EntityMapper<PermissionDTO, Permission> {

}
