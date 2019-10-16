package com.chuansen.modules.system.mapper;

import com.chuansen.modules.system.entity.Menu;
import com.chuansen.modules.system.entity.dto.MenuDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2019-10-16T18:56:18+0800",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_131 (Oracle Corporation)"
)
@Component
public class MenuMapperImpl implements MenuMapper {

    @Override
    public Menu toEntity(MenuDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Menu menu = new Menu();

        menu.setId( dto.getId() );
        menu.setName( dto.getName() );
        menu.setSort( dto.getSort() );
        menu.setPath( dto.getPath() );
        menu.setComponent( dto.getComponent() );
        menu.setComponentName( dto.getComponentName() );
        menu.setIcon( dto.getIcon() );
        menu.setCache( dto.getCache() );
        menu.setHidden( dto.getHidden() );
        menu.setPid( dto.getPid() );
        menu.setIFrame( dto.getIFrame() );
        menu.setCreateTime( dto.getCreateTime() );

        return menu;
    }

    @Override
    public MenuDTO toDto(Menu entity) {
        if ( entity == null ) {
            return null;
        }

        MenuDTO menuDTO = new MenuDTO();

        menuDTO.setId( entity.getId() );
        menuDTO.setName( entity.getName() );
        menuDTO.setSort( entity.getSort() );
        menuDTO.setPath( entity.getPath() );
        menuDTO.setComponent( entity.getComponent() );
        menuDTO.setPid( entity.getPid() );
        menuDTO.setIFrame( entity.getIFrame() );
        menuDTO.setCache( entity.getCache() );
        menuDTO.setHidden( entity.getHidden() );
        menuDTO.setComponentName( entity.getComponentName() );
        menuDTO.setIcon( entity.getIcon() );
        menuDTO.setCreateTime( entity.getCreateTime() );

        return menuDTO;
    }

    @Override
    public List<Menu> toEntity(List<MenuDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Menu> list = new ArrayList<Menu>( dtoList.size() );
        for ( MenuDTO menuDTO : dtoList ) {
            list.add( toEntity( menuDTO ) );
        }

        return list;
    }

    @Override
    public List<MenuDTO> toDto(List<Menu> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<MenuDTO> list = new ArrayList<MenuDTO>( entityList.size() );
        for ( Menu menu : entityList ) {
            list.add( toDto( menu ) );
        }

        return list;
    }
}
