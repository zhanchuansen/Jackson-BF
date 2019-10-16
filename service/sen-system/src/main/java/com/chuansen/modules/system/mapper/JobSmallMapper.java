package com.chuansen.modules.system.mapper;
import com.chuansen.mapper.EntityMapper;
import com.chuansen.modules.system.entity.Job;
import com.chuansen.modules.system.entity.dto.JobSmallDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring",uses = {},unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JobSmallMapper extends EntityMapper<JobSmallDTO, Job> {

}