package com.chuansen.modules.system.service;
import com.chuansen.modules.system.entity.Menu;
import com.chuansen.modules.system.entity.dto.MenuDTO;
import com.chuansen.modules.system.entity.dto.MenuQueryCriteria;
import com.chuansen.modules.system.entity.dto.RoleSmallDTO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;
import java.util.Set;


@CacheConfig(cacheNames = "menu")
public interface MenuService {

    @Cacheable
    List<MenuDTO> queryAll(MenuQueryCriteria criteria);

    @CacheEvict(allEntries = true)
    MenuDTO save(Menu menu);

    @CacheEvict(allEntries = true)
    void update(Menu menu);

    @CacheEvict(allEntries = true)
    void delete(Set<Menu> menuSet);

    Menu findOne(Long id);

    /**
     * 递归找出待删除的菜单(选择子目录删除或者父目录删除)
     */
    Set<Menu> getDeleteMenus(List<Menu> menuList, Set<Menu> menuSet);

    /**
     * 返回所有菜单
     */
    @Cacheable(key = "'tree'")
    Object getMenuTree(List<Menu> menus);

    @Cacheable(key = "'pid:'+#p0")
    List<Menu> findByPid(long pid);

    /**
     * 根据该菜单获取该菜单下有哪些子菜单
     */
    Map buildTree(List<MenuDTO> menuDTOS);

    /**
     *从角色当中获取可查询的菜单
     */
    List<MenuDTO> findByRoles(List<RoleSmallDTO> roles);

    /**
     * 获取前端路由所需要的菜单
     */
    Object buildMenus(List<MenuDTO> byRoles);


}
