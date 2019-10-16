package com.chuansen.modules.system.repository;
import com.chuansen.modules.system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor {

    User findByUsername(String username);

    User findByEmail(String email);

    /**
     * 修改密码
     */
    @Modifying
    @Query(value = "update user set password = ?2 , last_password_reset_time = ?3 where username = ?1",nativeQuery = true)
    void updatePass(String username, String pass, Date lastPasswordResetTime);

    /**
     * 修改头像
     */
    @Modifying
    @Query(value = "update user set avatar = ?2 where username = ?1",nativeQuery = true)
    void updateAvatar(String username, String url);

    /**
     * 修改邮箱
     */
    @Modifying
    @Query(value = "update user set email = ?2 where username = ?1",nativeQuery = true)
    void updateEmail(String username, String email);
}
