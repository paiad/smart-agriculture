package com.paiad.smartagriculture.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息实体类
 * 用于存储系统用户的账号、密码、联系方式等信息
 */
@Data
@TableName("pure_user")
public class User implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String phone;
    private String openid;
    private String avatar;
    private String nickname;
    private Date createTime;
    private String identity;
}
