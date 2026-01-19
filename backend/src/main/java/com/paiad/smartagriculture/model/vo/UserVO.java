package com.paiad.smartagriculture.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class UserVO implements Serializable {
    private Integer id;
    private String username;
    private String phone;
    private String avatar;
    private String nickname;
    private String identity;
    private String openid;
    private Date createTime;
}
