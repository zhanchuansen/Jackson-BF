package com.chuansen.modules.system.controller;

import com.chuansen.aop.log.Log;
import com.chuansen.config.DataScope;
import com.chuansen.exception.BadRequestException;
import com.chuansen.modules.system.entity.Job;
import com.chuansen.modules.system.entity.dto.JobQueryCriteria;
import com.chuansen.modules.system.service.JobService;
import com.chuansen.utils.ThrowableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private DataScope dataScope;

    @Log("查询岗位")
    @GetMapping(value = "/job")
    @PreAuthorize("hasAnyRole('ADMIN','USERJOB_ALL','USERJOB_SELECT','USER_ALL','USER_SELECT')")
    public ResponseEntity getJobs(JobQueryCriteria criteria, Pageable pageable){
        criteria.setDeptIds(dataScope.getDeptIds());  //数据权限
        return new ResponseEntity(jobService.queryAll(criteria,pageable), HttpStatus.OK);
    }

    @Log("新增岗位")
    @PostMapping(value = "/job")
    @PreAuthorize("hasAnyRole('ADMIN','USERJOB_ALL','USERJOB_CREATE')")
    public ResponseEntity save(@Validated @RequestBody Job job){
        if(job.getId()!=null){
            throw new BadRequestException("建立岗位时不具有ID");
        }
        return new ResponseEntity(jobService.save(job),HttpStatus.CREATED);
    }

    @Log("修改岗位")
    @PutMapping(value = "/job")
    @PreAuthorize("hasAnyRole('ADMIN','USERJOB_ALL','USERJOB_EDIT')")
    public ResponseEntity update(@Validated(Job.Update.class) @RequestBody Job job){
        jobService.update(job);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除岗位")
    @DeleteMapping(value = "/job/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USERJOB_ALL','USERJOB_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        try {
            jobService.delete(id);
        }catch (Throwable e){
            ThrowableUtil.throwForeignKeyException(e, "该岗位存在用户关联，请取消关联后再试");
        }
        return new ResponseEntity(HttpStatus.OK);
    }



}
