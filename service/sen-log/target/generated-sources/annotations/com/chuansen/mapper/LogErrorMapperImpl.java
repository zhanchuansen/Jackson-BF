package com.chuansen.mapper;

import com.chuansen.entity.Log;
import com.chuansen.entity.dto.LogErrorDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2019-10-16T14:20:39+0800",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_131 (Oracle Corporation)"
)
@Component
public class LogErrorMapperImpl implements LogErrorMapper {

    @Override
    public Log toEntity(LogErrorDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Log log = new Log();

        log.setId( dto.getId() );
        log.setUsername( dto.getUsername() );
        log.setDescription( dto.getDescription() );
        log.setMethod( dto.getMethod() );
        log.setParams( dto.getParams() );
        log.setRequestIp( dto.getRequestIp() );
        log.setAddress( dto.getAddress() );
        log.setCreateTime( dto.getCreateTime() );

        return log;
    }

    @Override
    public LogErrorDTO toDto(Log entity) {
        if ( entity == null ) {
            return null;
        }

        LogErrorDTO logErrorDTO = new LogErrorDTO();

        logErrorDTO.setId( entity.getId() );
        logErrorDTO.setUsername( entity.getUsername() );
        logErrorDTO.setDescription( entity.getDescription() );
        logErrorDTO.setMethod( entity.getMethod() );
        logErrorDTO.setParams( entity.getParams() );
        logErrorDTO.setRequestIp( entity.getRequestIp() );
        logErrorDTO.setAddress( entity.getAddress() );
        logErrorDTO.setCreateTime( entity.getCreateTime() );

        return logErrorDTO;
    }

    @Override
    public List<Log> toEntity(List<LogErrorDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Log> list = new ArrayList<Log>( dtoList.size() );
        for ( LogErrorDTO logErrorDTO : dtoList ) {
            list.add( toEntity( logErrorDTO ) );
        }

        return list;
    }

    @Override
    public List<LogErrorDTO> toDto(List<Log> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<LogErrorDTO> list = new ArrayList<LogErrorDTO>( entityList.size() );
        for ( Log log : entityList ) {
            list.add( toDto( log ) );
        }

        return list;
    }
}
