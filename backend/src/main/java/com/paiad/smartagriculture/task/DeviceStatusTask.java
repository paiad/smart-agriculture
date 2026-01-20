package com.paiad.smartagriculture.task;

import com.paiad.smartagriculture.model.pojo.Device;
import com.paiad.smartagriculture.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 设备状态定时任务
 * 定期检查设备最后一次上报时间，判定是否离线
 */
@Component
@Slf4j
public class DeviceStatusTask {

    @Autowired
    private DeviceService deviceService;

    /**
     * 每分钟检查一次设备在线状态
     * 如果设备超过5分钟未上报数据或确认指令，视为离线
     */
    @Scheduled(fixedRate = 60000)
    public void checkDeviceOffline() {
        log.debug("Starting device offline check...");

        // 阈值：5分钟前
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(5);

        // 批量更新：在线 且 最后活跃时间 < 阈值 的设备 -> 设为离线
        boolean updated = deviceService.lambdaUpdate()
                .eq(Device::getOnline, 1) // 只检查当前在线的
                .lt(Device::getLastSeenAt, threshold)
                .set(Device::getOnline, 0)
                .update();

        if (updated) {
            log.info("Found and updated offline devices.");
        }
    }
}
