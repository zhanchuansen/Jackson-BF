package com.chuansen.modules.security.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import com.chuansen.exception.BadRequestException;
import com.chuansen.modules.monitor.service.RedisService;
import com.chuansen.modules.security.entity.Auth;
import com.chuansen.modules.security.entity.AuthResultToken;
import com.chuansen.modules.security.security.ImgResult;
import com.chuansen.modules.security.security.JwtUser;
import com.chuansen.modules.security.utils.JwtTokenUtil;
import com.chuansen.modules.security.utils.VerifyCodeUtils;
import com.chuansen.utils.EncryptUtils;
import com.chuansen.utils.SecurityUtils;
import com.chuansen.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RedisService redisService;

    @Autowired
    @Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;

    /**
     * 获取验证码
     */
    @GetMapping(value = "vCode")
    public ImgResult getCode(HttpServletResponse response) throws IOException {

        //生成随机字串
        String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
        String uuid = IdUtil.simpleUUID();
        redisService.saveCode(uuid,verifyCode);  //保存验证码
        // 生成图片
        int w = 111, h = 36;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        VerifyCodeUtils.outputImage(w, h, stream, verifyCode);
        try {
            return new ImgResult(Base64.encode(stream.toByteArray()),uuid);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            stream.close();
        }
    }

    /**
     * 登录
     */
    @PostMapping(value = "${jwt.auth.path}")
    public ResponseEntity login(@Validated @RequestBody Auth auth){
        String code=redisService.getCodeVal(auth.getUuid());//查询验证码
        redisService.delete(auth.getUuid());//清除验证码
        if(StringUtils.isBlank(code)){
            throw new BadRequestException("验证码已过期");
        }
        if(StringUtils.isBlank(auth.getCode()) || !auth.getCode().equalsIgnoreCase(code)){
            throw new BadRequestException("验证码错误");
        }
        final JwtUser jwtUser=(JwtUser) userDetailsService.loadUserByUsername(auth.getUsername());  //根据前端所传的用户名通过JwtUser去获取密码

        if(!jwtUser.getPassword().equals(EncryptUtils.encryptPassword(auth.getPassword()))){
            throw new BadRequestException("密码错误");
        }
        if(!jwtUser.isEnabled()){
            throw new AccountExpiredException("账号已停用，请联系管理员");
        }

        // 生成令牌
        final String token=jwtTokenUtil.generateToken(jwtUser);
        // 返回 token
        return ResponseEntity.ok(new AuthResultToken(token,jwtUser));
    }

    /**
     * 获取用户信息
     */
    @GetMapping(value = "${jwt.auth.account}")
    public ResponseEntity getUserInfo(){
        JwtUser jwtUser=(JwtUser)userDetailsService.loadUserByUsername(SecurityUtils.getUsername());
        return ResponseEntity.ok(jwtUser);
    }
}
