package com.paiad.smartagriculture.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.paiad.smartagriculture.model.pojo.EnvData;

public interface EnvDataService extends IService<EnvData> {
    void processAndSave(EnvData envData);
}
