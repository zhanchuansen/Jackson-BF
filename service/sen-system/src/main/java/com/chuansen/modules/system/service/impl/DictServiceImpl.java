package com.chuansen.modules.system.service.impl;

import com.chuansen.modules.system.entity.Dict;
import com.chuansen.modules.system.entity.dto.DictDTO;
import com.chuansen.modules.system.entity.dto.DictQueryCriteria;
import com.chuansen.modules.system.mapper.DictMapper;
import com.chuansen.modules.system.repository.DictRepository;
import com.chuansen.modules.system.service.DictService;
import com.chuansen.utils.PageUtil;
import com.chuansen.utils.QueryHelp;
import com.chuansen.utils.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class DictServiceImpl implements DictService {

    @Autowired
    private DictRepository dictRepository;

    @Autowired
    private DictMapper dictMapper;

    @Override
    public Object queryAll(DictQueryCriteria criteria, Pageable pageable) {
        Page<Dict> page=dictRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(dictMapper::toDto));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictDTO save(Dict dict) {
        return dictMapper.toDto(dictRepository.save(dict));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Dict entity) {
        Optional<Dict> dictOptional= dictRepository.findById(entity.getId());
        ValidationUtil.isNull(dictOptional,"Dict","id",entity.getId());
        Dict dict=dictOptional.get();
        entity.setId(dict.getId());
        dictRepository.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        dictRepository.deleteById(id);
    }

}
