package com.paiad.smartagriculture.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.paiad.smartagriculture.model.pojo.Device;

public interface DeviceService extends IService<Device> {
    void updateHeartbeat(String deviceId);

    void updateRunningState(String deviceId, Integer running);
}
