package com.chuansen.modules.system.service;
import com.chuansen.modules.system.entity.Dept;
import com.chuansen.modules.system.entity.dto.DeptDTO;
import com.chuansen.modules.system.entity.dto.DeptQueryCriteria;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Set;

@CacheConfig(cacheNames = "dept")
public interface DeptService {
    /**
     * 查询 返回集合
     */
    @Cacheable
    List<DeptDTO> queryAll(DeptQueryCriteria criteria);

    @CacheEvict(allEntries = true)
    DeptDTO save(Dept dept);

    @CacheEvict(allEntries = true)
    void update(Dept dept);

    @CacheEvict(allEntries = true)
    void delete(Long id);

    /**
     * 部门详情 返回map
     */
    @Cacheable
    Object buildTree(List<DeptDTO> deptDTOS);

    /**
     * 根据父级ID查询 返回集合
     */
    @Cacheable
    List<Dept> findByPid(long pid);

    /**
     * 根据角色Id获取部门
     */
    Set<Dept> findByRoleIds(Long id);
}