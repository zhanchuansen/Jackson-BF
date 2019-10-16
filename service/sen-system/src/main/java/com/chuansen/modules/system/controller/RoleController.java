package com.chuansen.modules.system.controller;

import cn.hutool.core.lang.Dict;
import com.chuansen.aop.log.Log;
import com.chuansen.exception.BadRequestException;
import com.chuansen.modules.system.entity.Role;
import com.chuansen.modules.system.entity.dto.RoleQueryCriteria;
import com.chuansen.modules.system.entity.dto.RoleSmallDTO;
import com.chuansen.modules.system.service.RoleService;
import com.chuansen.utils.SecurityUtils;
import com.chuansen.utils.ThrowableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Log("查询角色")
    @GetMapping(value = "/roles")
    @PreAuthorize("hasAnyRole('ADMIN','ROLES_ALL','ROLES_SELECT')")
    public ResponseEntity getRoles(RoleQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity(roleService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @Log("新增角色")
    @PostMapping(value = "/roles")
    @PreAuthorize("hasAnyRole('ADMIN','ROLES_ALL','ROLES_CREATE')")
    public ResponseEntity save(@Validated @RequestBody Role role) {
        if (role.getId() != null) {
            throw new BadRequestException("新增的角色不具有ID");
        }
        return new ResponseEntity(roleService.save(role), HttpStatus.CREATED);
    }

    @Log("修改角色")
    @PutMapping(value = "/roles")
    @PreAuthorize("hasAnyRole('ADMIN','ROLES_ALL','ROLES_EDIT')")
    public ResponseEntity update(@Validated(Role.Update.class) @RequestBody Role role) {
        roleService.update(role);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除角色")
    @DeleteMapping(value = "/roles/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ROLES_ALL','ROLES_DELETE')")
    public ResponseEntity delete(@PathVariable Long id) {
        try {
            roleService.delete(id);
        } catch (Throwable e) {
            ThrowableUtil.throwForeignKeyException(e, "该角色存在用户关联，请取消关联后再试");
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @Log("修改角色权限分配")
    @PutMapping(value = "/roles/permission")
    @PreAuthorize("hasAnyRole('ADMIN','ROLES_ALL','ROLES_EDIT')")
    public ResponseEntity updatePermission(@RequestBody Role role) {
        roleService.updatePermission(role, roleService.findById(role.getId()));
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("修改角色菜单分配")
    @PutMapping(value = "/roles/menu")
    @PreAuthorize("hasAnyRole('ADMIN','ROLES_ALL','ROLES_EDIT')")
    public ResponseEntity updateMenu(@Validated(Role.Update.class) @RequestBody Role role) {
        roleService.updateMenu(role, roleService.findById(role.getId()));
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("获取所有的角色，新增用户时下拉选择")
    @GetMapping(value = "/roles/all")
    @PreAuthorize("hasAnyRole('ADMIN','ROLES_ALL','USER_ALL','USER_CREATE','USER_EDIT')")
    public ResponseEntity getAll(@PageableDefault(value = 2000, sort = {"level"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return new ResponseEntity(roleService.queryAll(pageable), HttpStatus.OK);
    }

    @Log("获取单个角色")
    @GetMapping(value = "/roles/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ROLES_ALL','ROLES_SELECT')")
    public ResponseEntity getRoles(@PathVariable Long id){
        return new ResponseEntity(roleService.findById(id), HttpStatus.OK);
    }

    @Log("获取当前角色级别")
    @GetMapping(value = "/roles/level")
    public ResponseEntity getLevel() {
        List<Integer> levels = roleService.findByUsers_Id(SecurityUtils.getUserId()).stream().map(RoleSmallDTO::getLevel).collect(Collectors.toList());
        return new ResponseEntity(Dict.create().set("level", Collections.min(levels)), HttpStatus.OK);
    }
}
