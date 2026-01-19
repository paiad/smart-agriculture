package com.paiad.smartagriculture.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paiad.smartagriculture.mapper.AlarmMapper;
import com.paiad.smartagriculture.model.pojo.Alarm;
import com.paiad.smartagriculture.service.AlarmService;
import org.springframework.stereotype.Service;

@Service
public class AlarmServiceImpl extends ServiceImpl<AlarmMapper, Alarm> implements AlarmService {
}
