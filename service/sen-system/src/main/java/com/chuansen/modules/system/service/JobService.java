package com.chuansen.modules.system.service;
import com.chuansen.modules.system.entity.Job;
import com.chuansen.modules.system.entity.dto.JobDTO;
import com.chuansen.modules.system.entity.dto.JobQueryCriteria;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;



@CacheConfig(cacheNames = "job")
public interface JobService {

    Object queryAll(JobQueryCriteria criteria, Pageable pageable);

    @CacheEvict(allEntries = true)
    JobDTO save(Job job);

    @CacheEvict(allEntries = true)
    void update(Job job);

    @CacheEvict(allEntries = true)
    void delete(Long id);


}