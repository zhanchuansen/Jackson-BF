package com.chuansen.modules.system.repository;

import com.chuansen.modules.system.entity.UserAvatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserAvatarRepository extends JpaRepository<UserAvatar, Long>, JpaSpecificationExecutor {

}
