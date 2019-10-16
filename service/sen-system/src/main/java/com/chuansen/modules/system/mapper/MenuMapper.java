package com.chuansen.modules.system.mapper;

import com.chuansen.mapper.EntityMapper;
import com.chuansen.modules.system.entity.Menu;
import com.chuansen.modules.system.entity.dto.MenuDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring",uses = {},unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MenuMapper extends EntityMapper<MenuDTO, Menu> {

}
