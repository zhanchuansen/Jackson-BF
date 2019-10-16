package com.chuansen.modules.system.service;
import com.chuansen.modules.system.entity.User;
import com.chuansen.modules.system.entity.dto.UserDTO;
import com.chuansen.modules.system.entity.dto.UserQueryCriteria;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@CacheConfig(cacheNames = "user")
public interface UserService {

    @Cacheable
    Object queryAll(UserQueryCriteria criteria, Pageable pageable);

    @Cacheable
    List<UserDTO> queryAll(UserQueryCriteria criteria);

    @CacheEvict(allEntries = true)
    UserDTO save(User user);

    @CacheEvict(allEntries = true)
    void update(User user);

    @CacheEvict(allEntries = true)
    void delete(Long id);

    /**
     * 查询用户名
     */
    @Cacheable(key = "'loadUserByUsername:'+#p0")
    UserDTO findByName(String userName);

    /**
     * 修改密码
     */
    @CacheEvict(allEntries = true)
    void updatePass(String username, String encryptPassword);

    /**
     * 修改头像
     */
    @CacheEvict(allEntries = true)
    void updateAvatar(MultipartFile file);

    /**
     * 修改邮箱
     */
    @CacheEvict(allEntries = true)
    void updateEmail(String username, String email);


    void download(List<UserDTO> userDTOList, HttpServletResponse response) throws IOException;
}
