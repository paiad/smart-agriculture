package com.paiad.smartagriculture.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.paiad.smartagriculture.model.dto.LoginDTO;
import com.paiad.smartagriculture.model.pojo.User;
import com.paiad.smartagriculture.model.vo.UserVO;

public interface UserService extends IService<User> {

    String login(LoginDTO loginDTO);

    UserVO getCurrentUser(String token);
}
