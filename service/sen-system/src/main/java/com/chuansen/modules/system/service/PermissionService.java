package com.chuansen.modules.system.service;
import com.chuansen.modules.system.entity.Menu;
import com.chuansen.modules.system.entity.Permission;
import com.chuansen.modules.system.entity.dto.PermissionDTO;
import com.chuansen.modules.system.entity.dto.PermissionQueryCriteria;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Set;


@CacheConfig(cacheNames = "permission")
public interface PermissionService {

    @Cacheable
    List<PermissionDTO> queryAll(PermissionQueryCriteria criteria);

    @CacheEvict(allEntries = true)
    PermissionDTO save(Permission permission);

    @CacheEvict(allEntries = true)
    void update(Permission permission);

    @CacheEvict(allEntries = true)
    void delete(Set<Permission> permissions);

    Permission findOne(Long id);

    /**
     * 返回所有权限
     */
    @Cacheable(key = "'tree'")
    Object getPermissionTree(List<Permission> permissions);

    @Cacheable(key = "'pid:'+#p0")
    List<Permission> findByPid(long pid);

    /**
     * 根据该权限目录获取该权限下有哪些子目录
     */
    @Cacheable
    Object buildTree(List<PermissionDTO> permissionDTOS);

    /**
     * 递归找出待删除的权限(选择子目录删除或者父目录删除)
     */
    Set<Permission> getDeletePermission(List<Permission> permissions, Set<Permission> permissionSet);
}
