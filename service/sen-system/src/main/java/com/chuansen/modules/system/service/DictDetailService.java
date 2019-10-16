package com.chuansen.modules.system.service;
import com.chuansen.modules.system.entity.DictDetail;
import com.chuansen.modules.system.entity.dto.DictDetailDTO;
import com.chuansen.modules.system.entity.dto.DictDetailQueryCriteria;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;

import java.util.Map;

@CacheConfig(cacheNames = "dictDetail")
public interface DictDetailService {

    @Cacheable
    Map queryAll(DictDetailQueryCriteria criteria, Pageable pageable);

    @CacheEvict(allEntries = true)
    DictDetailDTO save(DictDetail dictDetail);

    @CacheEvict(allEntries = true)
    void update(DictDetail dictDetail);

    @CacheEvict(allEntries = true)
    void delete(Long id);


}