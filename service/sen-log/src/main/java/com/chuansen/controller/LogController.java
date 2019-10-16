package com.chuansen.controller;

import com.chuansen.aop.log.Log;
import com.chuansen.entity.dto.LogQueryCriteria;
import com.chuansen.service.LogService;
import com.chuansen.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class LogController {

    @Autowired
    private LogService logService;

    @Log("日志查询")
    @GetMapping(value = "/logs")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity getLogs(LogQueryCriteria criteria, Pageable pageable){
        criteria.setLogType("Info");
        return new ResponseEntity(logService.queryAll(criteria,pageable), HttpStatus.OK);
    }

    /**
     * 个人信息操作日志查询
     */
    @GetMapping(value = "/logs/user")
    public ResponseEntity getUserLogs(LogQueryCriteria criteria, Pageable pageable){
        criteria.setLogType("INFO");
        criteria.setBlurry(SecurityUtils.getUsername());
        return new ResponseEntity(logService.queryAllByUser(criteria,pageable), HttpStatus.OK);
    }

    @Log("异常查询")
    @GetMapping(value = "/logs/error")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity getErrorLogs(LogQueryCriteria criteria, Pageable pageable){
        criteria.setLogType("ERROR");
        return new ResponseEntity(logService.queryAll(criteria,pageable), HttpStatus.OK);
    }

    @GetMapping(value = "/logs/error/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity getErrorLogs(@PathVariable Long id){
        return new ResponseEntity(logService.findByErrDetail(id), HttpStatus.OK);
    }


}
