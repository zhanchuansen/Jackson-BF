package com.chuansen.modules.system.controller;

import com.chuansen.aop.log.Log;
import com.chuansen.config.DataScope;
import com.chuansen.exception.BadRequestException;
import com.chuansen.modules.system.entity.User;
import com.chuansen.modules.system.entity.dto.RoleSmallDTO;
import com.chuansen.modules.system.entity.dto.UserQueryCriteria;
import com.chuansen.modules.system.entity.vo.UserPassVo;
import com.chuansen.modules.system.service.DeptService;
import com.chuansen.modules.system.service.RoleService;
import com.chuansen.modules.system.service.UserService;
import com.chuansen.utils.EncryptUtils;
import com.chuansen.utils.PageUtil;
import com.chuansen.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private DataScope dataScope;

    @Autowired
    private DeptService deptService;

    @Autowired
    private RoleService roleService;


    @Log("查询用户")
    @GetMapping(value = "/users")
    @PreAuthorize("hasAnyRole('ADMIN','USER_ALL','USER_SELECT')")
    public ResponseEntity getUsers(UserQueryCriteria criteria, Pageable pageable) {
        Set<Long> deptSet = new HashSet<>();
        Set<Long> result = new HashSet<>();

        if (!ObjectUtils.isEmpty(criteria.getDeptId())) {
            deptSet.add(criteria.getDeptId()); //当选中部门时存入部门Id
            deptSet.addAll(dataScope.getDeptChildren(deptService.findByPid(criteria.getDeptId())));
        }

        Set<Long> deptIds = dataScope.getDeptIds();//数据权限

        //查询条件不为空并且数据权限不为空则取交集
        if (!CollectionUtils.isEmpty(deptIds) && !CollectionUtils.isEmpty(deptSet)) {
            // 取交集
            result.addAll(deptSet);
            result.retainAll(deptIds);

            // 若无交集，则代表无数据权限
            criteria.setDeptIds(result);
            if (result.size() == 0) {
                return new ResponseEntity(PageUtil.toPage(null, 0), HttpStatus.OK);
            } else
                return new ResponseEntity(userService.queryAll(criteria, pageable), HttpStatus.OK);
        } else {
            //否则取并集
            result.addAll(deptSet);
            result.addAll(deptIds);
            criteria.setDeptIds(result);
            return new ResponseEntity(userService.queryAll(criteria, pageable), HttpStatus.OK);
        }
    }

    @Log("新增用户")
    @PostMapping(value = "/users")
    @PreAuthorize("hasAnyRole('ADMIN','USER_ALL','USER_CREATE')")
    public ResponseEntity save(@Validated @RequestBody User user) {
        checkLevel(user);
        return new ResponseEntity(userService.save(user),HttpStatus.CREATED);
    }

    @Log("删除用户")
    @DeleteMapping(value = "/users/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER_ALL','USER_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        Integer currentLevel =  Collections.min(roleService.findByUsers_Id(SecurityUtils.getUserId()).stream().map(RoleSmallDTO::getLevel).collect(Collectors.toList()));
        Integer optLevel=Collections.min(roleService.findByUsers_Id(id).stream().map(RoleSmallDTO::getLevel).collect(Collectors.toList()));
        if(currentLevel > optLevel){
            throw new BadRequestException("角色权限不足");
        }
        userService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Log("修改用户")
    @PutMapping(value = "/users")
    @PreAuthorize("hasAnyRole('ADMIN','USER_ALL','USER_EDIT')")
    public ResponseEntity update(@Validated(User.Update.class) @RequestBody User user){
        checkLevel(user);
        userService.update(user);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("修改密码")
    @PostMapping(value = "/users/updatePass")
    public ResponseEntity updatePassWord(@RequestBody UserPassVo user){
        UserDetails userDetails=SecurityUtils.getUserDetails();
        if(!userDetails.getPassword().equals(EncryptUtils.encryptPassword(user.getOldPass()))){
            throw new BadRequestException("修改失败，旧密码错误");
        }
        if(userDetails.getPassword().equals(EncryptUtils.encryptPassword(user.getNewPass()))){
            throw new BadRequestException("新密码不能与旧密码相同");
        }
        userService.updatePass(userDetails.getUsername(),EncryptUtils.encryptPassword(user.getNewPass()));
        return new ResponseEntity(HttpStatus.OK);
    }

    @Log("修改头像")
    @PostMapping(value = "/users/updateAvatar")
    public ResponseEntity updateAvatar(@RequestParam MultipartFile file){
        userService.updateAvatar(file);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Log("导出用户数据")
    @GetMapping(value = "/users/download")
    @PreAuthorize("hasAnyRole('ADMIN','USER_ALL','USER_SELECT')")
    public void download(HttpServletResponse response, UserQueryCriteria criteria){
        try {
            userService.download(userService.queryAll(criteria),response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 当前用户角色不能低于创建用户的角色级别，否则就抛出异常权限不足级别不够
     */
    public void checkLevel(User user){
        //获取当前用户角色的级别
        Integer currentLevel =  Collections.min(roleService.findByUsers_Id(SecurityUtils.getUserId()).stream().map(RoleSmallDTO::getLevel).collect(Collectors.toList()));
        Integer optLevel = roleService.findByRoles(user.getRoles());//获取创建用户的级别
        if (currentLevel > optLevel) {
            throw new BadRequestException("角色权限不足");
        }

    }

}