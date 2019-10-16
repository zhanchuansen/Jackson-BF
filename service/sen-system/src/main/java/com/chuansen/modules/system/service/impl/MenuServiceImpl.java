package com.chuansen.modules.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.chuansen.exception.BadRequestException;
import com.chuansen.exception.EntityExistException;
import com.chuansen.modules.system.entity.Menu;
import com.chuansen.modules.system.entity.dto.MenuDTO;
import com.chuansen.modules.system.entity.dto.MenuQueryCriteria;
import com.chuansen.modules.system.entity.dto.RoleSmallDTO;
import com.chuansen.modules.system.entity.vo.MenuMetaVo;
import com.chuansen.modules.system.entity.vo.MenuVo;
import com.chuansen.modules.system.mapper.MenuMapper;
import com.chuansen.modules.system.repository.MenuRepository;
import com.chuansen.modules.system.service.MenuService;
import com.chuansen.modules.system.service.RoleService;
import com.chuansen.utils.QueryHelp;
import com.chuansen.utils.StringUtils;
import com.chuansen.utils.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public List<MenuDTO> queryAll(MenuQueryCriteria criteria) {
        return menuMapper.toDto(menuRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    public MenuDTO save(Menu menu) {
        if (menuRepository.findByName(menu.getName()) != null) {
            throw new EntityExistException(Menu.class, "name", menu.getName());
        }
        if (StringUtils.isNotBlank(menu.getComponentName())) {
            if (menuRepository.findByComponentName(menu.getComponentName()) != null) {
                throw new EntityExistException(Menu.class, "componentName", menu.getComponentName());
            }
        }
        if (menu.getIFrame()) {
            if (!(menu.getPath().toLowerCase().startsWith("http://") || menu.getPath().toLowerCase().startsWith("https://"))) {
                throw new BadRequestException("外链必须以http://或者https://开头");
            }
        }
        return menuMapper.toDto(menuRepository.save(menu));
    }

    @Override
    public void update(Menu menu) {
        if (menu.getId().equals(menu.getPid())) {
            throw new BadRequestException("上级不能为自己");
        }
        Optional<Menu> optionalPermission = menuRepository.findById(menu.getId());
        ValidationUtil.isNull(optionalPermission, "Permission", "id", menu.getId());

        if (menu.getIFrame()) {
            if (!(menu.getPath().toLowerCase().startsWith("http://") || menu.getPath().toLowerCase().startsWith("https://"))) {
                throw new BadRequestException("外链必须以http://或者https://开头");
            }
        }

        Menu entity = optionalPermission.get();

        Menu menuName = menuRepository.findByName(menu.getName());

        if (menuName != null && !menuName.getId().equals(entity.getId())) {
            throw new EntityExistException(Menu.class, "name", menu.getName());
        }

        if (StringUtils.isNotBlank(menu.getComponentName())) {

            menuName = menuRepository.findByComponentName(menu.getComponentName());

            if (menuName != null && !menuName.getId().equals(entity.getId())) {
                throw new EntityExistException(Menu.class, "componentName", menu.getComponentName());
            }
        }

        entity.setName(menu.getName());
        entity.setComponent(menu.getComponent());
        entity.setPath(menu.getPath());
        entity.setIcon(menu.getIcon());
        entity.setIFrame(menu.getIFrame());
        entity.setPid(menu.getPid());
        entity.setSort(menu.getSort());
        entity.setCache(menu.getCache());
        entity.setHidden(menu.getHidden());
        entity.setComponentName(menu.getComponentName());
        menuRepository.save(entity);
    }

    @Override
    public void delete(Set<Menu> menuSet) {
        for (Menu menu : menuSet) {
            roleService.untiedMenu(menu.getId());//删除菜单时同时删除角色与菜单之间的关联
            menuRepository.deleteById(menu.getId());
        }
    }

    @Override
    public Menu findOne(Long id) {
        Optional<Menu> menuOptional = menuRepository.findById(id);
        ValidationUtil.isNull(menuOptional, "Menu", "id", id);
        return menuOptional.get();
    }


    @Override
    public Set<Menu> getDeleteMenus(List<Menu> menuList, Set<Menu> menuSet) {
        for (Menu menu : menuList) {  //递归找出待删除的菜单
            menuSet.add(menu);
            List<Menu> menus = menuRepository.findByPid(menu.getId());
            if (menus != null && menus.size() != 0) {
                getDeleteMenus(menus, menuSet);
            }
        }
        return menuSet;
    }

    @Override
    public Object getMenuTree(List<Menu> menus) {
        List<Map<String,Object>> list = new LinkedList<>();
        menus.forEach(menu -> {
                    if (menu!=null){
                        List<Menu> menuList = menuRepository.findByPid(menu.getId());
                        Map<String,Object> map = new HashMap<>();
                        map.put("id",menu.getId());
                        map.put("label",menu.getName());
                        if(menuList!=null && menuList.size()!=0){
                            map.put("children",getMenuTree(menuList));
                        }
                        list.add(map);
                    }
                }
        );
        return list;
    }

    @Override
    public List<Menu> findByPid(long pid) {
        return menuRepository.findByPid(pid);
    }

    /**
     * 根据该菜单获取该菜单下有哪些子菜单
     */
    @Override
    public Map buildTree(List<MenuDTO> menuDTOS) {
        List<MenuDTO> menuDTOList = new ArrayList<MenuDTO>();
        Set<Long> ids = new HashSet<>();
        for (MenuDTO menuDTO : menuDTOS) {
            if (menuDTO.getPid() == 0) {  //当系统管理为一级菜单时Pid=0 则系统管理的信息赋值到集合里
                menuDTOList.add(menuDTO);
            }
            for (MenuDTO menuDTO2 : menuDTOS) {
                if (menuDTO2.getPid().equals(menuDTO.getId())) {  //当用户角色管理为二级菜单时应加入到对应的父级当中
                    if (menuDTO.getChildren() == null) {  //当子级为空时
                        menuDTO.setChildren(new ArrayList<MenuDTO>());//重新初始化一个
                    }
                    menuDTO.getChildren().add(menuDTO2);//获取到当前的信息赋值到集合中
                    ids.add(menuDTO2.getId());
                }
            }
        }
        Map map = new HashMap();
        if (menuDTOList.size() == 0) {
            menuDTOList = menuDTOS.stream().filter(f -> !ids.contains(f.getId())).collect(Collectors.toList());
        }
        map.put("content", menuDTOList);
        map.put("totalElements", menuDTOS != null ? menuDTOS.size() : 0);
        return map;
    }


    /**
     * 从角色当中获取可查询的菜单
     *
     * @param roles
     * @return
     */
    @Override
    public List<MenuDTO> findByRoles(List<RoleSmallDTO> roles) {
        Set<Menu> menus = new LinkedHashSet<>();
        for (RoleSmallDTO roleSmallDTO : roles) {
            List<Menu> menus1 = menuRepository.findByRoles_IdOrderBySortAsc(roleSmallDTO.getId()).stream().collect(Collectors.toList());
            menus.addAll(menus1);
        }
        List<MenuDTO> menuDTOList = menus.stream().map(menuMapper::toDto).collect(Collectors.toList());//从实体Entity获取所得到的结果转DTO集合所接收的
        return menuDTOList;
    }

    /**
     * 获取前端路由所需要的菜单
     */
    @Override
    public List<MenuVo> buildMenus(List<MenuDTO> menuDTOS) {
        List<MenuVo> list = new LinkedList<>();
        menuDTOS.forEach(menuDTO -> {
                    if (menuDTO != null) {
                        List<MenuDTO> menuDTOList = menuDTO.getChildren();
                        MenuVo menuVo = new MenuVo();
                        menuVo.setName(ObjectUtil.isNotEmpty(menuDTO.getComponentName()) ? menuDTO.getComponentName() : menuDTO.getName());
                        // 一级目录需要加斜杠，不然会报警告
                        menuVo.setPath(menuDTO.getPid() == 0 ? "/" + menuDTO.getPath() : menuDTO.getPath());
                        menuVo.setHidden(menuDTO.getHidden());
                        // 如果不是外链
                        if (!menuDTO.getIFrame()) {
                            if (menuDTO.getPid() == 0) {
                                menuVo.setComponent(StrUtil.isEmpty(menuDTO.getComponent()) ? "Layout" : menuDTO.getComponent());
                            } else if (!StrUtil.isEmpty(menuDTO.getComponent())) {
                                menuVo.setComponent(menuDTO.getComponent());
                            }
                        }
                        menuVo.setMeta(new MenuMetaVo(menuDTO.getName(), menuDTO.getIcon(), !menuDTO.getCache()));
                        if (menuDTOList != null && menuDTOList.size() != 0) {
                            menuVo.setAlwaysShow(true);
                            menuVo.setRedirect("noredirect");
                            menuVo.setChildren(buildMenus(menuDTOList));
                            // 处理是一级菜单并且没有子菜单的情况
                        } else if (menuDTO.getPid() == 0) {
                            MenuVo menuVo1 = new MenuVo();
                            menuVo1.setMeta(menuVo.getMeta());
                            // 非外链
                            if (!menuDTO.getIFrame()) {
                                menuVo1.setPath("index");
                                menuVo1.setName(menuVo.getName());
                                menuVo1.setComponent(menuVo.getComponent());
                            } else {
                                menuVo1.setPath(menuDTO.getPath());
                            }
                            menuVo.setName(null);
                            menuVo.setMeta(null);
                            menuVo.setComponent("Layout");
                            List<MenuVo> list1 = new ArrayList<MenuVo>();
                            list1.add(menuVo1);
                            menuVo.setChildren(list1);
                        }
                        list.add(menuVo);
                    }
                }
        );
        return list;
    }


}
