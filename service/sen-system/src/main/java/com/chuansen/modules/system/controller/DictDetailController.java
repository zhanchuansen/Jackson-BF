package com.chuansen.modules.system.controller;

import com.chuansen.aop.log.Log;
import com.chuansen.exception.BadRequestException;
import com.chuansen.modules.system.entity.DictDetail;
import com.chuansen.modules.system.entity.dto.DictDetailQueryCriteria;
import com.chuansen.modules.system.service.DictDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api")
public class DictDetailController {

    @Autowired
    private DictDetailService dictDetailService;

    @Log("查询字典详情")
    @GetMapping(value = "/dictDetail")
    public ResponseEntity getDictDetail(DictDetailQueryCriteria criteria,
                                        @PageableDefault(value = 10, sort = {"sort"}, direction = Sort.Direction.ASC) Pageable pageable){
        return new ResponseEntity(dictDetailService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("查询多个字典详情")
    @GetMapping(value = "/dictDetail/map")
    public ResponseEntity getDictDetailMaps(DictDetailQueryCriteria criteria,
                                            @PageableDefault(value = 10, sort = {"sort"}, direction = Sort.Direction.ASC) Pageable pageable) {
        String[] names = criteria.getDictName().split(",");
        Map map = new HashMap(names.length);
        for (String name : names) {
            criteria.setDictName(name);
            map.put(name,dictDetailService.queryAll(criteria,pageable).get("content"));
        }
        return new ResponseEntity(map, HttpStatus.OK);
    }

    @Log("新增字典详情")
    @PostMapping(value = "/dictDetail")
    @PreAuthorize("hasAnyRole('ADMIN','DICT_ALL','DICT_CREATE')")
    public ResponseEntity save(@Validated @RequestBody DictDetail dictDetail){
        if (dictDetail.getId() != null) {
            throw new BadRequestException("新建时字典详情不具有ID");
        }
        return new ResponseEntity(dictDetailService.save(dictDetail),HttpStatus.CREATED);
    }

    @Log("修改字典详情")
    @PutMapping(value = "/dictDetail")
    @PreAuthorize("hasAnyRole('ADMIN','DICT_ALL','DICT_EDIT')")
    public ResponseEntity update(@Validated(DictDetail.Update.class) @RequestBody DictDetail resources){
        dictDetailService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除字典详情")
    @DeleteMapping(value = "/dictDetail/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DICT_ALL','DICT_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        dictDetailService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
