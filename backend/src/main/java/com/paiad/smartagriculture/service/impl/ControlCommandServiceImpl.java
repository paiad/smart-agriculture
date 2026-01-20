package com.paiad.smartagriculture.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paiad.smartagriculture.mapper.ControlCommandMapper;
import com.paiad.smartagriculture.model.pojo.ControlCommand;
import com.paiad.smartagriculture.service.ControlCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ControlCommandServiceImpl extends ServiceImpl<ControlCommandMapper, ControlCommand>
                implements ControlCommandService {

        @Override
        public ControlCommand getByRequestId(String requestId) {
                return getOne(new LambdaQueryWrapper<ControlCommand>()
                                .eq(ControlCommand::getRequestId, requestId));
        }

        @Override
        public int markTimeoutCommands(int timeoutSeconds) {
                LocalDateTime threshold = LocalDateTime.now().minusSeconds(timeoutSeconds);

                // 更新所有 status=1(已发送) 且 sentAt < threshold 的记录为 status=4(超时)
                boolean updated = lambdaUpdate()
                                .eq(ControlCommand::getStatus, 1)
                                .lt(ControlCommand::getSentAt, threshold)
                                .set(ControlCommand::getStatus, 4)
                                .set(ControlCommand::getErrorMsg, "指令执行超时，未收到设备确认")
                                .update();

                if (updated) {
                        log.info("已标记超时指令，阈值时间: {}", threshold);
                }
                return updated ? 1 : 0;
        }
}
