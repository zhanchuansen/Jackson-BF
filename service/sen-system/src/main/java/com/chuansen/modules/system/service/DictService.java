package com.chuansen.modules.system.service;
import com.chuansen.modules.system.entity.Dict;
import com.chuansen.modules.system.entity.dto.DictDTO;
import com.chuansen.modules.system.entity.dto.DictQueryCriteria;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;


@CacheConfig(cacheNames = "dict")
public interface DictService {

    /**
     * 查询  分页接收
     * @param criteria
     * @param pageable
     * @return
     */
    @Cacheable
    Object queryAll(DictQueryCriteria criteria, Pageable pageable);

    @CacheEvict(allEntries = true)
    DictDTO save(Dict dict);

    @CacheEvict(allEntries = true)
    void update(Dict dict);

    @CacheEvict(allEntries = true)
    void delete(Long id);

}