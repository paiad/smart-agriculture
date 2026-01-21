package com.paiad.smartagriculture.task;

import com.paiad.smartagriculture.common.constants.CommandConstants;
import com.paiad.smartagriculture.service.ControlCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 指令超时定时任务
 * 定期扫描并标记超时的控制指令
 */
@Component
@Slf4j
public class CommandTimeoutTask {

    @Autowired
    private ControlCommandService controlCommandService;

    /**
     * 每10秒检查一次超时指令
     * 将 status=1(已发送) 且发送时间超过30秒的指令标记为 status=4(超时)
     */
    @Scheduled(fixedRate = 10000)
    public void checkCommandTimeout() {
        log.debug("Starting command timeout check...");
        controlCommandService.markTimeoutCommands(CommandConstants.COMMAND_TIMEOUT_SECONDS);
    }
}
