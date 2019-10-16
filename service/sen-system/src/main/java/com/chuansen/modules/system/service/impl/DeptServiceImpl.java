package com.chuansen.modules.system.service.impl;

import com.chuansen.exception.BadRequestException;
import com.chuansen.modules.system.entity.Dept;
import com.chuansen.modules.system.entity.dto.DeptDTO;
import com.chuansen.modules.system.entity.dto.DeptQueryCriteria;
import com.chuansen.modules.system.mapper.DeptMapper;
import com.chuansen.modules.system.repository.DeptRepository;
import com.chuansen.modules.system.service.DeptService;
import com.chuansen.utils.QueryHelp;
import com.chuansen.utils.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptRepository deptRepository;

    @Autowired
    private DeptMapper deptMapper;


    @Override
    public List<DeptDTO> queryAll(DeptQueryCriteria criteria) {
        return deptMapper.toDto(deptRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeptDTO save(Dept dept) {
        return deptMapper.toDto(deptRepository.save(dept));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Dept entity) {
        if(entity.getId().equals(entity.getPid())){
            throw new BadRequestException("上级不能为自己");
        }
        Optional<Dept> deptOptional=deptRepository.findById(entity.getId());//通过Id获取当前的部门
        ValidationUtil.isNull(deptOptional, "Dept", "id", entity.getId());//验证是否为空
        Dept dept=deptOptional.get();//获取当前的部门信息
        entity.setId(dept.getId());
        deptRepository.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        deptRepository.deleteById(id);
    }

    /**
     * 部门详情
     */
    @Override
    public Object buildTree(List<DeptDTO> deptDTOS) {
        Set<DeptDTO> deptDTOSet = new LinkedHashSet<>();
        Set<DeptDTO> deptDTOSet2 = new LinkedHashSet<>();
        List<String> deptNames = deptDTOS.stream().map(DeptDTO::getName).collect(Collectors.toList());//获取所有部门名称
        Boolean isChild;
        for (DeptDTO deptDTO : deptDTOS) {
            isChild = false;
            if ("0".equals(deptDTO.getPid().toString())) {   //当一级部门pid为0时先把查询出来的信息放到新的集合中去deptDTOSet
                deptDTOSet.add(deptDTO);
            }
            for (DeptDTO deptDTO2 : deptDTOS) {
                if (deptDTO2.getPid().equals(deptDTO.getId())) {   //当二级部门三级部门pid等于父级的id时则把查询出来的信息放到当一级部门pid为0时先把查询出来的信息放到新的集合中去deptDTOSet2
                    isChild = true;
                    if (deptDTO.getChildren() == null) {
                        deptDTO.setChildren(new ArrayList<DeptDTO>());
                    }
                    deptDTO.getChildren().add(deptDTO2);
                }
            }
            if (isChild)
                deptDTOSet2.add(deptDTO);
            else if (!deptNames.contains(deptRepository.findNameById(deptDTO.getPid())))
                deptDTOSet2.add(deptDTO);
        }
        if (CollectionUtils.isEmpty(deptDTOSet)) {//判断deptDTOSet集合是否为空
            deptDTOSet = deptDTOSet2;
        }
        Map map = new HashMap<>();
        map.put("totalElements", deptDTOS != null ? deptDTOS.size() : 0);
        map.put("content",CollectionUtils.isEmpty(deptDTOSet)?deptDTOS:deptDTOSet);
        return map;
    }

    @Override
    public List<Dept> findByPid(long pid) {
        return deptRepository.findByPid(pid);
    }

    @Override
    public Set<Dept> findByRoleIds(Long id) {
        return deptRepository.findByRoles_Id(id);
    }
}
