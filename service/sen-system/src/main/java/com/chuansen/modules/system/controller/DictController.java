package com.chuansen.modules.system.controller;

import com.chuansen.aop.log.Log;
import com.chuansen.exception.BadRequestException;
import com.chuansen.modules.system.entity.Dict;
import com.chuansen.modules.system.entity.dto.DictQueryCriteria;
import com.chuansen.modules.system.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class DictController {

    @Autowired
    private DictService dictService;

    @Log("查询字典")
    @GetMapping(value = "/dict")
    @PreAuthorize("hasAnyRole('ADMIN','DICT_ALL','DICT_SELECT')")
    public ResponseEntity getDicts(DictQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity(dictService.queryAll(criteria,pageable), HttpStatus.OK);
    }

    @Log("新增字典")
    @PostMapping(value = "/dict")
    @PreAuthorize("hasAnyRole('ADMIN','DICT_ALL','DICT_CREATE')")
    public ResponseEntity save(@Validated @RequestBody Dict dict){
        if (dict.getId() != null) {
            throw new BadRequestException("新建字典时不具有ID");
        }
        return new ResponseEntity(dictService.save(dict),HttpStatus.CREATED);
    }

    @Log("修改字典")
    @PutMapping(value = "/dict")
    @PreAuthorize("hasAnyRole('ADMIN','DICT_ALL','DICT_EDIT')")
    public ResponseEntity update(@Validated(Dict.Update.class) @RequestBody Dict dict){
        dictService.update(dict);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除字典")
    @DeleteMapping(value = "/dict/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DICT_ALL','DICT_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        dictService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
