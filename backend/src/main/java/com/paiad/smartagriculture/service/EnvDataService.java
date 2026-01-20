package com.paiad.smartagriculture.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.paiad.smartagriculture.model.pojo.EnvData;
import org.springframework.scheduling.annotation.Async;

public interface EnvDataService extends IService<EnvData> {
    @Async("mqttTaskExecutor")
    void processAndSave(EnvData envData);
}
