package com.paiad.smartagriculture.model.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class UserVO implements Serializable {
    private Integer id;
    private String username;
    private String phone;
    private String company;
    private String website;
    private Integer parentId;
}
