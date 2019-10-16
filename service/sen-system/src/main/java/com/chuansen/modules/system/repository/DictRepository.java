package com.chuansen.modules.system.repository;

import com.chuansen.modules.system.entity.Dict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface DictRepository extends JpaRepository<Dict, Long>, JpaSpecificationExecutor {
}