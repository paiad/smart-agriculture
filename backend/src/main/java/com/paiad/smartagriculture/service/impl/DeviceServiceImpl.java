package com.paiad.smartagriculture.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paiad.smartagriculture.mapper.DeviceMapper;
import com.paiad.smartagriculture.model.pojo.Device;
import com.paiad.smartagriculture.service.DeviceService;
import org.springframework.stereotype.Service;

@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {
    @Override
    public void updateHeartbeat(String deviceId) {
        if (deviceId == null) {
            return;
        }
        lambdaUpdate()
                .eq(Device::getDeviceId, deviceId)
                .set(Device::getLastSeenAt, java.time.LocalDateTime.now())
                .set(Device::getOnline, 1)
                .update();
    }
}
