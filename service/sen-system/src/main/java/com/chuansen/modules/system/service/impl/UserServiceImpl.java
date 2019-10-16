package com.chuansen.modules.system.service.impl;

import com.chuansen.exception.BadRequestException;
import com.chuansen.exception.EntityExistException;
import com.chuansen.exception.EntityNotFoundException;
import com.chuansen.modules.monitor.service.RedisService;
import com.chuansen.modules.system.entity.User;
import com.chuansen.modules.system.entity.UserAvatar;
import com.chuansen.modules.system.entity.dto.RoleSmallDTO;
import com.chuansen.modules.system.entity.dto.UserDTO;
import com.chuansen.modules.system.entity.dto.UserQueryCriteria;
import com.chuansen.modules.system.mapper.UserMapper;
import com.chuansen.modules.system.repository.UserAvatarRepository;
import com.chuansen.modules.system.repository.UserRepository;
import com.chuansen.modules.system.service.UserService;
import com.chuansen.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAvatarRepository userAvatarRepository;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserMapper userMapper;

    @Value("${file.avatar}")
    private String avatar;

    @Override
    public Object queryAll(UserQueryCriteria criteria, Pageable pageable) {
        Page<User> page = userRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(userMapper::toDto));
    }

    @Override
    public List<UserDTO> queryAll(UserQueryCriteria criteria) {
        List<User> userList = userRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        return userMapper.toDto(userList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO save(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new EntityExistException(User.class, "username", user.getUsername());
        }

        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new EntityExistException(User.class, "email", user.getEmail());
        }
        // 默认密码 123456，此密码是加密后的字符
        user.setPassword("e10adc3949ba59abbe56e057f20f883e");
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(User user) {
        Optional<User> userOptional = userRepository.findById(user.getId());
        ValidationUtil.isNull(userOptional, "User", "id", user.getId());

        User entity = userOptional.get();

        User userName = userRepository.findByUsername(entity.getUsername());
        if (userName != null && !entity.getId().equals(userName.getId())) {
            throw new EntityExistException(User.class, "username", user.getUsername());
        }
        User email = userRepository.findByEmail(entity.getEmail());
        if (email != null && !entity.getId().equals(email.getId())) {
            throw new EntityExistException(User.class, "email", user.getEmail());
        }

        //用户角色改变还需手动清理缓存
        if (user.getRoles().equals(entity.getRoles())) {
            String key = "role::loadPermissionByUser:" + user.getUsername();
            redisService.delete(key);
            key = "role::findByUsers_Id:" + user.getId();
            redisService.delete(key);
        }
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setEnabled(user.getEnabled());
        entity.setRoles(user.getRoles());
        entity.setDept(user.getDept());
        entity.setJob(user.getJob());
        entity.setPhone(user.getPhone());
        userRepository.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        userRepository.deleteById(id);
    }


    @Override
    public UserDTO findByName(String userName) {
        User user = null;
        if (ValidationUtil.isEmail(userName)) {
            user = userRepository.findByEmail(userName);
        } else {
            user = userRepository.findByUsername(userName);
        }
        if (user == null) {
            throw new EntityNotFoundException(User.class, "name", userName);
        } else {
            return userMapper.toDto(user);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePass(String username, String encryptPassword) {
        userRepository.updatePass(username, encryptPassword, new Date());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAvatar(MultipartFile multipartFile) {
        User user = userRepository.findByUsername(SecurityUtils.getUsername());
        UserAvatar entity = user.getUserAvatar();
        String oldPath = "";
        if(entity != null){
            oldPath = entity.getPath();
        }
        //将文件名解析成文件的上传路径
        File file = FileUtil.upload(multipartFile, avatar);   //文件，文件存放的路径
        entity=userAvatarRepository.save(new UserAvatar(entity,file.getName(),file.getPath(),FileUtil.getSize(multipartFile.getSize())));
        user.setUserAvatar(entity);
        userRepository.save(user);
        if(StringUtils.isNotBlank(oldPath)){
            FileUtil.del(oldPath);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmail(String username, String email) {
        userRepository.updateEmail(username,email);
    }


    @Override
    public void download(List<UserDTO> userDTOList, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (UserDTO userDTO : userDTOList) {
            List roles=userDTO.getRoles().stream().map(RoleSmallDTO::getName).collect(Collectors.toList());//只取角色名称
            Map map=new LinkedHashMap();
            map.put("用户名", userDTO.getUsername());
            //map.put("头像", userDTO.getAvatar());
            map.put("邮箱", userDTO.getEmail());
            map.put("状态", userDTO.getEnabled() ? "启用" : "禁用");
            map.put("手机号码", userDTO.getPhone());
            map.put("角色", roles);
            map.put("部门", userDTO.getDept().getName());
            map.put("岗位", userDTO.getJob().getName());
            map.put("最后修改密码的时间", userDTO.getLastPasswordResetTime());
            map.put("创建日期", userDTO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list,"用户列表", response);
    }
}
