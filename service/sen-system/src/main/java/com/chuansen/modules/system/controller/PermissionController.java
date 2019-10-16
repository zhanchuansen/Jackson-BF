package com.chuansen.modules.system.controller;

import com.chuansen.aop.log.Log;
import com.chuansen.exception.BadRequestException;
import com.chuansen.modules.system.entity.Permission;
import com.chuansen.modules.system.entity.Role;
import com.chuansen.modules.system.entity.dto.PermissionDTO;
import com.chuansen.modules.system.entity.dto.PermissionQueryCriteria;
import com.chuansen.modules.system.mapper.PermissionMapper;
import com.chuansen.modules.system.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @Log("查询权限")
    @GetMapping(value = "/permissions")
    @PreAuthorize("hasAnyRole('ADMIN','PERMISSION_ALL','PERMISSION_SELECT')")
    public ResponseEntity getPermissions(PermissionQueryCriteria criteria){
        List<PermissionDTO> permissionDTOS = permissionService.queryAll(criteria);
        return new ResponseEntity(permissionService.buildTree(permissionDTOS), HttpStatus.OK);
    }

    @Log("新增权限")
    @PostMapping(value = "/permissions")
    @PreAuthorize("hasAnyRole('ADMIN','PERMISSION_ALL','PERMISSION_CREATE')")
    public ResponseEntity save(@Validated @RequestBody Permission permission){
        if (permission.getId() != null) {
            throw new BadRequestException("新增权限时不具有ID");
        }
        return new ResponseEntity(permissionService.save(permission),HttpStatus.CREATED);
    }

    @Log("修改权限")
    @PutMapping(value = "/permissions")
    @PreAuthorize("hasAnyRole('ADMIN','PERMISSION_ALL','PERMISSION_EDIT')")
    public ResponseEntity update(@Validated(Permission.Update.class)  @RequestBody Permission permission){
        permissionService.update(permission);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除权限")
    @DeleteMapping(value = "/permissions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PERMISSION_ALL','PERMISSION_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        List<Permission> permissions = permissionService.findByPid(id);
        Set<Permission> permissionSet = new HashSet<>();
        permissionSet.add(permissionService.findOne(id));
        permissionSet = permissionService.getDeletePermission(permissions, permissionSet);
        permissionService.delete(permissionSet);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Log("返回全部的权限，新增角色时下拉选择")
    @GetMapping(value = "/permissions/tree")
    @PreAuthorize("hasAnyRole('ADMIN','PERMISSION_ALL','PERMISSION_CREATE','PERMISSION_EDIT','ROLES_SELECT','ROLES_ALL')")
    public ResponseEntity getAll() {
        return new ResponseEntity(permissionService.getPermissionTree(permissionService.findByPid(0L)),HttpStatus.OK);
    }
}
