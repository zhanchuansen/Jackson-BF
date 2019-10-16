package com.chuansen.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONObject;
import com.chuansen.entity.Log;
import com.chuansen.entity.dto.LogQueryCriteria;
import com.chuansen.mapper.LogErrorMapper;
import com.chuansen.mapper.LogSmallMapper;
import com.chuansen.repository.LogRepository;
import com.chuansen.service.LogService;
import com.chuansen.utils.PageUtil;
import com.chuansen.utils.QueryHelp;
import com.chuansen.utils.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class LogServiceImpl implements LogService {

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private LogErrorMapper logErrorMapper;

    @Autowired
    private LogSmallMapper logSmallMapper;

    private final String LOGINPATH = "login";

    @Override
    public Object queryAll(LogQueryCriteria criteria, Pageable pageable) {
        Page<Log> page = logRepository.findAll(((root, criteriaQuery, cb) -> QueryHelp.getPredicate(root, criteria, cb)),pageable);
        if ("ERROR".equals(criteria.getLogType())) {
            return PageUtil.toPage(page.map(logErrorMapper::toDto));
        }
        return page;
    }

    @Override
    public Object queryAllByUser(LogQueryCriteria criteria, Pageable pageable) {
        Page<Log> page = logRepository.findAll(((root, criteriaQuery, cb) -> QueryHelp.getPredicate(root, criteria, cb)),pageable);
        return PageUtil.toPage(page.map(logSmallMapper::toDto));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(String username, String ip, ProceedingJoinPoint joinPoint, Log log) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();  //获取方法
        com.chuansen.aop.log.Log aopLog = method.getAnnotation(com.chuansen.aop.log.Log.class);//根据方法上面的日志描述获取描述

        //描述
        if (log != null) {
            log.setDescription(aopLog.value());
        }
        //方法路径+方法名
        String mothodPath = joinPoint.getTarget().getClass().getName() + "." + methodSignature.getName() + "()";
        log.setMethod(mothodPath);
        /**
         * 获取参数
         */
        String params = "{";
        Object[] argValues = joinPoint.getArgs();//参数值
        String[] ParameterName = ((MethodSignature) joinPoint.getSignature()).getParameterNames();  //参数名称
        if (argValues != null) {
            for (int i = 0; i < argValues.length; i++) {
                params += ParameterName[i] + ":" + argValues[i];
            }
        }
        log.setParams(params+"}");

        //获取ip
        log.setRequestIp(ip);

        if(LOGINPATH.equals(methodSignature.getName())){   //如果访问登录的方法
            JSONObject jsonObject=new JSONObject(argValues[0]);
            username=jsonObject.get("username").toString();
        }
        log.setUsername(username);
        log.setAddress(StringUtils.getCityInfo(log.getRequestIp()));
        logRepository.save(log);
    }

    @Override
    public Object findByErrDetail(Long id) {
        return Dict.create().set("exception",logRepository.findExceptionById(id));
    }
}
