package com.chuansen.modules.security.entity;

import com.chuansen.modules.security.security.JwtUser;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 返回token
 */
@Getter
@AllArgsConstructor
public class AuthResultToken implements Serializable {

    private final String token;

    private final JwtUser user;
}
