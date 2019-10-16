package com.chuansen.modules.system.controller;

import com.chuansen.aop.log.Log;
import com.chuansen.config.DataScope;
import com.chuansen.exception.BadRequestException;
import com.chuansen.modules.system.entity.Dept;
import com.chuansen.modules.system.entity.dto.DeptDTO;
import com.chuansen.modules.system.entity.dto.DeptQueryCriteria;
import com.chuansen.modules.system.service.DeptService;
import com.chuansen.utils.ThrowableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
public class DeptController {

    @Autowired
    private DeptService deptService;

    @Autowired
    private DataScope dataScope;

    @Log("部门详情")
    @GetMapping(value = "/dept")
    @PreAuthorize("hasAnyRole('ADMIN','USER_ALL','USER_SELECT','DEPT_ALL','DEPT_SELECT')")
    public ResponseEntity getDepts(DeptQueryCriteria criteria){
        criteria.setIds(dataScope.getDeptIds());  //数据权限
        List<DeptDTO> deptDTOList=deptService.queryAll(criteria);//查询可查看的部门
        return new ResponseEntity(deptService.buildTree(deptDTOList), HttpStatus.OK);
    }

    @Log("新增部门")
    @PostMapping(value = "/dept")
    @PreAuthorize("hasAnyRole('ADMIN','DEPT_ALL','DEPT_CREATE')")
    public ResponseEntity save(@Validated @RequestBody Dept dept){
        if(dept.getId()!=null){
            throw new BadRequestException("建立部门时不具有ID");
        }
        return new ResponseEntity(deptService.save(dept),HttpStatus.CREATED);
    }

    @Log("修改部门")
    @PutMapping(value = "/dept")
    @PreAuthorize("hasAnyRole('ADMIN','DEPT_ALL','DEPT_CREATE')")
    public ResponseEntity update(@Validated(Dept.Update.class) @RequestBody Dept dept){
        deptService.update(dept);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除部门")
    @DeleteMapping(value = "/dept/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DEPT_ALL','DEPT_CREATE')")
    public ResponseEntity delete(@PathVariable Long id){
        try {
            deptService.delete(id);
        }catch (Throwable e){
            ThrowableUtil.throwForeignKeyException(e, "该部门存在岗位或者角色关联，请取消关联后再试");
        }
        return new ResponseEntity(HttpStatus.OK);
    }




}
