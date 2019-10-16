package com.chuansen.modules.system.service.impl;

import com.chuansen.exception.EntityExistException;
import com.chuansen.modules.system.entity.Role;
import com.chuansen.modules.system.entity.dto.RoleDTO;
import com.chuansen.modules.system.entity.dto.RoleQueryCriteria;
import com.chuansen.modules.system.entity.dto.RoleSmallDTO;
import com.chuansen.modules.system.mapper.RoleMapper;
import com.chuansen.modules.system.mapper.RoleSmallMapper;
import com.chuansen.modules.system.repository.RoleRepository;
import com.chuansen.modules.system.service.RoleService;
import com.chuansen.utils.PageUtil;
import com.chuansen.utils.QueryHelp;
import com.chuansen.utils.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleSmallMapper roleSmallMapper;


    @Override
    public Object queryAll(Pageable pageable) {
        return roleMapper.toDto(roleRepository.findAll(pageable).getContent());
    }

    @Override
    public Object queryAll(RoleQueryCriteria criteria, Pageable pageable) {
        Page<Role> page = roleRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(roleMapper::toDto));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleDTO save(Role role) {
        if (roleRepository.findByName(role.getName()) != null) {
            throw new EntityExistException(Role.class, "username", role.getName());
        }
        return roleMapper.toDto(roleRepository.save(role));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Role entity) {
        Optional<Role> roleOptional=roleRepository.findById(entity.getId());
        ValidationUtil.isNull(roleOptional,"Role","id",entity.getId());
        Role role = roleOptional.get();
        Role roleName=roleRepository.findByName(entity.getName());
        if(roleName!=null && roleName.getId().equals(role.getId())){
            throw new EntityExistException(Role.class,"username",entity.getName());
        }
        role.setName(entity.getName());
        role.setRemark(entity.getRemark());
        role.setDataScope(entity.getDataScope());
        role.setDepts(entity.getDepts());
        role.setLevel(entity.getLevel());
        roleRepository.save(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        roleRepository.deleteById(id);
    }

    @Override
    public RoleDTO findById(long id) {
        Optional<Role> role = roleRepository.findById(id);
        ValidationUtil.isNull(role, "Role", "id", id);
        return roleMapper.toDto(role.get());
    }

    @Override
    public List<RoleSmallDTO> findByUsers_Id(Long id) {
        return roleSmallMapper.toDto(roleRepository.findByUsers_Id(id).stream().collect(Collectors.toList()));//Entity集合转DTO集合
    }

    @Override
    public Integer findByRoles(Set<Role> roles) {
        Set<RoleDTO> roleDTOSet = new HashSet<>();
        for (Role role : roles) {
            roleDTOSet.add(findById(role.getId()));
        }
        return Collections.min(roleDTOSet.stream().map(RoleDTO::getLevel).collect(Collectors.toList()));//返回该级别最小的角色
    }

    @Override
    public void updatePermission(Role role, RoleDTO roleDTO) {
        Role entity=roleMapper.toEntity(roleDTO);//根据前端传的角色ID获取到的角色转换为Entity
        entity.setPermissions(role.getPermissions());
        roleRepository.save(entity);
    }
    @Override
    public void untiedPermission(Long id) {
        roleRepository.untiedPermission(id);//删除权限时同时删除角色与权限之间的关联
    }

    @Override
    public void updateMenu(Role role, RoleDTO roleDTO) {
        Role entity=roleMapper.toEntity(roleDTO);//根据前端传的角色ID获取到的角色转换为Entity
        entity.setMenus(role.getMenus());//将前端传过来的角色菜单赋值到转换Entity后的角色菜单中
        roleRepository.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void untiedMenu(Long id) {
        roleRepository.untiedMenu(id);//删除菜单时同时删除角色与菜单之间的关联
    }
}
