package com.paiad.smartagriculture.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paiad.smartagriculture.mapper.ControlCommandMapper;
import com.paiad.smartagriculture.model.pojo.ControlCommand;
import com.paiad.smartagriculture.service.ControlCommandService;
import org.springframework.stereotype.Service;

@Service
public class ControlCommandServiceImpl extends ServiceImpl<ControlCommandMapper, ControlCommand>
        implements ControlCommandService {
}
