package com.chuansen.modules.system.service;
import com.chuansen.modules.system.entity.Role;
import com.chuansen.modules.system.entity.dto.RoleDTO;
import com.chuansen.modules.system.entity.dto.RoleQueryCriteria;
import com.chuansen.modules.system.entity.dto.RoleSmallDTO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

@CacheConfig(cacheNames = "role")
public interface RoleService {

    @Cacheable
    Object queryAll(RoleQueryCriteria criteria, Pageable pageable);

    @Cacheable
    Object queryAll(Pageable pageable);

    @CacheEvict(allEntries = true)
    RoleDTO save(Role role);

    @CacheEvict(allEntries = true)
    void update(Role role);

    @CacheEvict(allEntries = true)
    void delete(Long id);

    @Cacheable(key = "#p0")
    RoleDTO findById(long id);

    /**
     * key的名称如有修改，请同步修改 UserServiceImpl 中的 update 方法
     */
    @Cacheable(key = "'findByUsers_Id:' + #p0")
    List<RoleSmallDTO> findByUsers_Id(Long id);

    @Cacheable
    Integer findByRoles(Set<Role> roles);

    /**
     * 更新角色权限
     */
    @CacheEvict(allEntries = true)
    void updatePermission(Role role, RoleDTO roleDTO);
    /**
     * 删除权限时同时删除角色与权限之间的关联
     */
    @CacheEvict(allEntries = true)
    void untiedPermission(Long id);

    /**
     * 更新角色菜单
     */
    @CacheEvict(allEntries = true)
    void updateMenu(Role role, RoleDTO roleDTO);
    /**
     * 删除菜单时同时删除角色与菜单之间的关联
     */
    @CacheEvict(allEntries = true)
    void untiedMenu(Long id);


}
