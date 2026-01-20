package com.paiad.smartagriculture.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.paiad.smartagriculture.model.pojo.ControlCommand;

public interface ControlCommandService extends IService<ControlCommand> {

    /**
     * 根据 requestId 查询指令
     */
    ControlCommand getByRequestId(String requestId);

    /**
     * 扫描并更新超时的指令（status=1 且发送时间超过阈值）
     * 
     * @param timeoutSeconds 超时秒数
     * @return 更新的记录数
     */
    int markTimeoutCommands(int timeoutSeconds);
}
