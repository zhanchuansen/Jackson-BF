package com.chuansen.modules.system.controller;

import com.chuansen.aop.log.Log;
import com.chuansen.exception.BadRequestException;
import com.chuansen.modules.system.entity.Menu;
import com.chuansen.modules.system.entity.dto.MenuDTO;
import com.chuansen.modules.system.entity.dto.MenuQueryCriteria;
import com.chuansen.modules.system.entity.dto.UserDTO;
import com.chuansen.modules.system.service.MenuService;
import com.chuansen.modules.system.service.RoleService;
import com.chuansen.modules.system.service.UserService;
import com.chuansen.utils.SecurityUtils;
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
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    /**
     * 构建前端路由所需要的菜单
     */
    @GetMapping(value = "/menus/build")
    public ResponseEntity buildMenus(){
      UserDTO userDTO=userService.findByName(SecurityUtils.getUsername());  //获取系统用户
      List<MenuDTO> menuDTOList=menuService.findByRoles(roleService.findByUsers_Id(userDTO.getId()));//查询该用户从角色当中获取可查询的菜单
      List<MenuDTO> menuDTOTree=(List<MenuDTO>)menuService.buildTree(menuDTOList).get("content");//根据该菜单获取该菜单下有哪些子菜单
      List<MenuDTO> resultList=(List)menuService.buildMenus(menuDTOTree);//获取前端路由所需要的菜单
      return new ResponseEntity(resultList, HttpStatus.OK);
  }

    @Log("查询菜单")
    @GetMapping(value = "/menus")
    @PreAuthorize("hasAnyRole('ADMIN','MENU_ALL','MENU_SELECT')")
    public ResponseEntity getMenus(MenuQueryCriteria criteria){
        List<MenuDTO> menuDTOList=menuService.queryAll(criteria);  //查询所有的菜单;
        return new ResponseEntity(menuService.buildTree(menuDTOList), HttpStatus.OK);//根据该菜单获取该菜单下有哪些子菜单
    }

    @Log("新增菜单")
    @PostMapping(value = "/menus")
    @PreAuthorize("hasAnyRole('ADMIN','MENU_ALL','MENU_CREATE')")
    public ResponseEntity save(@Validated @RequestBody Menu menu){
        if (menu.getId() != null) {
            throw new BadRequestException("新增菜单时不具有ID");
        }
        return new ResponseEntity(menuService.save(menu),HttpStatus.CREATED);
    }

    @Log("返回所有菜单")
    @GetMapping(value = "/menus/tree")
    @PreAuthorize("hasAnyRole('ADMIN','MENU_ALL','MENU_CREATE','MENU_EDIT','ROLES_SELECT','ROLES_ALL')")
    public ResponseEntity getMenuTree(){
        return new ResponseEntity(menuService.getMenuTree(menuService.findByPid(0L)),HttpStatus.OK);
    }

    @Log("修改菜单")
    @PutMapping(value = "/menus")
    @PreAuthorize("hasAnyRole('ADMIN','MENU_ALL','MENU_EDIT')")
    public ResponseEntity update(@Validated(Menu.Update.class) @RequestBody Menu menu){
        menuService.update(menu);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除菜单")
    @DeleteMapping(value = "/menus/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MENU_ALL','MENU_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        List<Menu> menuList=menuService.findByPid(id);
        Set<Menu> menuSet=new HashSet<>();
        menuSet.add(menuService.findOne(id));
        menuSet=menuService.getDeleteMenus(menuList,menuSet);
        menuService.delete(menuSet);
        return new ResponseEntity(HttpStatus.OK);
    }
}
