package com.paiad.smartagriculture.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paiad.smartagriculture.mapper.AlarmRuleMapper;
import com.paiad.smartagriculture.model.pojo.AlarmRule;
import com.paiad.smartagriculture.service.AlarmRuleService;
import org.springframework.stereotype.Service;

@Service
public class AlarmRuleServiceImpl extends ServiceImpl<AlarmRuleMapper, AlarmRule> implements AlarmRuleService {
}
