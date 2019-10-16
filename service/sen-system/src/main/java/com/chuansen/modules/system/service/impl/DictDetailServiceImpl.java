package com.chuansen.modules.system.service.impl;

import com.chuansen.modules.system.entity.DictDetail;
import com.chuansen.modules.system.entity.dto.DictDetailDTO;
import com.chuansen.modules.system.entity.dto.DictDetailQueryCriteria;
import com.chuansen.modules.system.mapper.DictDetailMapper;
import com.chuansen.modules.system.repository.DictDetailRepository;
import com.chuansen.modules.system.service.DictDetailService;
import com.chuansen.utils.PageUtil;
import com.chuansen.utils.QueryHelp;
import com.chuansen.utils.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;


@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class DictDetailServiceImpl implements DictDetailService {

    @Autowired
    private DictDetailRepository dictDetailRepository;

    @Autowired
    private DictDetailMapper dictDetailMapper;

    @Override
    public Map queryAll(DictDetailQueryCriteria criteria, Pageable pageable) {
        Page<DictDetail> page=dictDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(dictDetailMapper::toDto));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictDetailDTO save(DictDetail dictDetail) {
        return dictDetailMapper.toDto(dictDetailRepository.save(dictDetail));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DictDetail entity) {
        Optional<DictDetail> optionalDictDetail = dictDetailRepository.findById(entity.getId());
        ValidationUtil.isNull( optionalDictDetail,"DictDetail","id",entity.getId());
        DictDetail dictDetail = optionalDictDetail.get();
        entity.setId(dictDetail.getId());
        dictDetailRepository.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        dictDetailRepository.deleteById(id);
    }

}
