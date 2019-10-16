package com.chuansen.modules.system.repository;

import com.chuansen.modules.system.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor {

    Permission findByName(String name);

    List<Permission> findByPid(long pid);
}
