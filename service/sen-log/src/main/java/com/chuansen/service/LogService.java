package com.chuansen.service;

import com.chuansen.entity.Log;
import com.chuansen.entity.dto.LogQueryCriteria;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;

public interface LogService {

    Object queryAll(LogQueryCriteria criteria, Pageable pageable);


    Object queryAllByUser(LogQueryCriteria criteria, Pageable pageable);

    @Async
    void save(String username, String ip, ProceedingJoinPoint joinPoint, Log log);

    Object findByErrDetail(Long id);
}
