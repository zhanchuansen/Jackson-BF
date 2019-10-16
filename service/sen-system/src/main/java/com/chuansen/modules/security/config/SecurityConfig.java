package com.chuansen.modules.security.config;

import com.chuansen.modules.security.security.JwtAuthenticationEntryPoint;
import com.chuansen.modules.security.security.JwtAuthorizationTokenFilter;
import com.chuansen.modules.security.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig配置
 */

@Configuration
@EnableWebSecurity  //这个注解必须加，开启Security
@EnableGlobalMethodSecurity(prePostEnabled = true)   //保证post之前的注解可以使用
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //当用户尝试访问安全的REST资源时，会跳转到错误页面
    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    //SecurityConfig配置中从该类里面去获取认证信息的方法
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    //自定义基于JWT的安全过滤器
    @Autowired
    JwtAuthorizationTokenFilter authenticationTokenFilter;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.auth.path}")
    private String loginPath;


    //先来这里认证一下
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(jwtUserDetailsService)
                .passwordEncoder(passwordEncoderBean());
    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        // Remove the ROLE_ prefix
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public PasswordEncoder passwordEncoderBean() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    //配置拦截
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity

                // 禁用 Spring Security 自带的跨域处理
                .csrf().disable()

                // 授权异常
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()

                // 不创建会话
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()  //因为我们要使用jwt托管安全信息，所以把Session禁止掉。这个是我们用的，不会新建，也不会使用一个HttpSession。

                // 过滤请求
                .authorizeRequests()
                .antMatchers(
                        HttpMethod.GET,
                        "/*.html",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js"
                ).anonymous()

                .antMatchers( HttpMethod.POST,"/auth/"+loginPath).anonymous()
                .antMatchers("/auth/vCode").anonymous()
                // 支付宝回调
                .antMatchers("/api/aliPay/return").anonymous()
                .antMatchers("/api/aliPay/notify").anonymous()

                // swagger start
                .antMatchers("/swagger-ui.html").anonymous()
                .antMatchers("/swagger-resources/**").anonymous()
                .antMatchers("/webjars/**").anonymous()
                .antMatchers("/*/api-docs").anonymous()
                // swagger end

                // 接口限流测试
                .antMatchers("/test/**").anonymous()
                // 文件
                .antMatchers("/avatar/**").anonymous()
                .antMatchers("/file/**").anonymous()

                // 放行OPTIONS请求
                .antMatchers(HttpMethod.OPTIONS, "/**").anonymous()

                .antMatchers("/druid/**").anonymous()
                // 剩下所有的验证都需要验证
                .anyRequest().authenticated()
                // 防止iframe 造成跨域
                .and().headers().frameOptions().disable();
                // 定制我们自己的 session 策略：调整为让 Spring Security 不创建和使用 session
        httpSecurity
                .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
