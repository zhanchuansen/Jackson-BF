package com.chuansen.modules.system.service.impl;
import com.chuansen.exception.BadRequestException;
import com.chuansen.exception.EntityExistException;
import com.chuansen.modules.system.entity.Permission;
import com.chuansen.modules.system.entity.dto.PermissionDTO;
import com.chuansen.modules.system.entity.dto.PermissionQueryCriteria;
import com.chuansen.modules.system.mapper.PermissionMapper;
import com.chuansen.modules.system.repository.PermissionRepository;
import com.chuansen.modules.system.service.PermissionService;
import com.chuansen.modules.system.service.RoleService;
import com.chuansen.utils.QueryHelp;
import com.chuansen.utils.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private RoleService roleService;

    @Override
    public List<PermissionDTO> queryAll(PermissionQueryCriteria criteria) {
        return permissionMapper.toDto(permissionRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PermissionDTO save(Permission permission) {
        if (permissionRepository.findByName(permission.getName()) != null) {
            throw new EntityExistException(Permission.class, "name", permission.getName());
        }
        return permissionMapper.toDto(permissionRepository.save(permission));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Permission permission) {
        Optional<Permission> optionalPermission = permissionRepository.findById(permission.getId());
        ValidationUtil.isNull(optionalPermission, "Permission", "id", permission.getId());

        if (permission.getId().equals(permission.getPid())) {
            throw new BadRequestException("上级不能为自己");
        }

        Permission entity=optionalPermission.get();

        Permission permissionName=permissionRepository.findByName(permission.getName());

        if(permissionName!=null && !permissionName.getId().equals(permission.getId())){
            throw new EntityExistException(Permission.class,"name",permission.getName());
        }

        entity.setName(permission.getName());
        entity.setAlias(permission.getAlias());
        entity.setPid(permission.getPid());
        permissionRepository.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Permission> permissions) {
         for(Permission permission:permissions){
           roleService.untiedPermission(permission.getId());  //删除权限时同时删除角色与权限之间的关联
           permissionRepository.delete(permission);
         }
    }

    @Override
    public Permission findOne(Long id) {
        Optional<Permission> permissionOptional= permissionRepository.findById(id);
        ValidationUtil.isNull(permissionOptional,"Permission","id",id);
        return permissionOptional.get();
    }


    @Override
    public Object getPermissionTree(List<Permission> permissions) {
        List<Map<String,Object>> list = new LinkedList<>();
        permissions.forEach(permission -> {
                    if (permission!=null){
                        List<Permission> permissionList = permissionRepository.findByPid(permission.getId());
                        Map<String,Object> map = new HashMap<>();
                        map.put("id",permission.getId());
                        map.put("label",permission.getAlias());
                        if(permissionList!=null && permissionList.size()!=0){
                            map.put("children",getPermissionTree(permissionList));
                        }
                        list.add(map);
                    }
                }
        );
        return list;
    }

    @Override
    public List<Permission> findByPid(long pid) {
        return permissionRepository.findByPid(pid);
    }

    @Override
    public Object buildTree(List<PermissionDTO> permissionDTOS) {
        List<PermissionDTO> permissionDTOList = new ArrayList<>();
        for (PermissionDTO permissionDTO : permissionDTOS) {
            if (permissionDTO.getPid() == 0) {  //当权限为一级时Pid=0 则权限信息赋值到集合里
                permissionDTOList.add(permissionDTO);
            }
            for (PermissionDTO permissionDTO2 : permissionDTOS) {
                if (permissionDTO2.getPid().equals(permissionDTO.getId())) {//当权限管理等级为子级时应加入到属于对应的父级当中
                    if (permissionDTO.getChildren() == null) { //当子级为空时
                        permissionDTO.setChildren(new ArrayList<PermissionDTO>());
                    }
                    permissionDTO.getChildren().add(permissionDTO2);//获取到当前的信息赋值到集合中
                }
            }
        }
        Map map = new HashMap<>();
        map.put("content", permissionDTOList);
        map.put("totalElements", permissionDTOS != null ? permissionDTOS.size() : 0);
        return map;
    }


    @Override
    public Set<Permission> getDeletePermission(List<Permission> permissions, Set<Permission> permissionSet) {
        for(Permission permission : permissions){ //递归找出待删除的菜单
            permissionSet.add(permission);
            List<Permission> permissionList=permissionRepository.findByPid(permission.getId());
            if(permissionList!=null){
                getDeletePermission(permissionList,permissionSet);
            }
        }
        return permissionSet;
    }
}
