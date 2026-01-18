package com.paiad.smartagriculture.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paiad.smartagriculture.common.utils.JWTUtil;
import com.paiad.smartagriculture.mapper.UserMapper;
import com.paiad.smartagriculture.model.dto.LoginDTO;
import com.paiad.smartagriculture.model.pojo.User;
import com.paiad.smartagriculture.model.vo.UserVO;
import com.paiad.smartagriculture.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;

    @Override
    public String login(LoginDTO loginDTO) {
        User user = lambdaQuery()
                .eq(User::getPhone, loginDTO.getPhone())
                .eq(User::getPassword, loginDTO.getPassword())
                .one();
        if (user == null) {
            throw new RuntimeException("手机号或密码错误");
        }

        return JWTUtil.createToken(user.getId());
    }

    @Override
    public UserVO getCurrentUser(String token) {
        Integer userId = JWTUtil.getUserId(token);
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return BeanUtil.copyProperties(user, UserVO.class);
    }
}
