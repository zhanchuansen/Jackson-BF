package com.chuansen.modules.system.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.chuansen.annotation.Query;

import java.util.Set;

@Data
@NoArgsConstructor
public class JobQueryCriteria {

    @Query(type = Query.Type.INNER_LIKE)
    private String name;

    @Query
    private Boolean enabled;

    @Query(propName = "id", joinName = "dept")
    private Long deptId;

    @Query(propName = "id", joinName = "dept", type = Query.Type.IN)
    private Set<Long> deptIds;
}