package com.chuansen.modules.security.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 用户登录的信息
 */
@Getter
@Setter
public class Auth {

    //只能作用在String上，不能为null，而且调用trim()后，长度必须大于0
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private String code;

    private String uuid = "";

    @Override
    public String toString() {
        return "{username=" + username  + ", password= ******}";
    }
}
