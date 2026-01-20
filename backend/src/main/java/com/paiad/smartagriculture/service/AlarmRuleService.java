package com.paiad.smartagriculture.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.paiad.smartagriculture.model.pojo.AlarmRule;

public interface AlarmRuleService extends IService<AlarmRule> {
    /**
     * 重载所有启用的告警规则到缓存
     */
    void reloadCache();
}
