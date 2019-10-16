package com.chuansen.modules.system.service.impl;

import com.chuansen.modules.system.entity.Job;
import com.chuansen.modules.system.entity.dto.JobDTO;
import com.chuansen.modules.system.entity.dto.JobQueryCriteria;
import com.chuansen.modules.system.mapper.JobMapper;
import com.chuansen.modules.system.repository.DeptRepository;
import com.chuansen.modules.system.repository.JobRepository;
import com.chuansen.modules.system.service.JobService;
import com.chuansen.utils.PageUtil;
import com.chuansen.utils.QueryHelp;
import com.chuansen.utils.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private DeptRepository deptRepository;

    @Override
    public Object queryAll(JobQueryCriteria criteria, Pageable pageable) {
        Page<Job> page=jobRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        List<JobDTO> jobDTOList = new ArrayList<>();
        for (Job job:page.getContent()){
            jobDTOList.add(jobMapper.toDto(job,deptRepository.findNameById(job.getDept().getPid())));
        }
        return PageUtil.toPage(jobDTOList,page.getTotalElements());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobDTO save(Job job) {
        return jobMapper.toDto(jobRepository.save(job));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Job entity) {
        Optional<Job> jobOptional=jobRepository.findById(entity.getId());
        ValidationUtil.isNull(jobOptional,"Job","id",entity.getId());
        Job job=jobOptional.get();
        entity.setId(job.getId());
        jobRepository.save(entity);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        jobRepository.deleteById(id);
    }

}
