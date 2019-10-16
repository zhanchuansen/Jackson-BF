package com.chuansen.repository;

import com.chuansen.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface LogRepository extends JpaRepository<Log,Long>, JpaSpecificationExecutor {

    @Query(value = "select exception_detail FROM log where id = ?1",nativeQuery = true)
    String findExceptionById(Long id);


}
