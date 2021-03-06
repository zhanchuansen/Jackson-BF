package com.chuansen.modules.system.repository;

import com.chuansen.modules.system.entity.DictDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DictDetailRepository extends JpaRepository<DictDetail, Long>, JpaSpecificationExecutor {
}