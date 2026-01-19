package com.paiad.smartagriculture.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paiad.smartagriculture.mapper.EnvDataMapper;
import com.paiad.smartagriculture.model.pojo.Alarm;
import com.paiad.smartagriculture.model.pojo.AlarmRule;
import com.paiad.smartagriculture.model.pojo.Device;
import com.paiad.smartagriculture.model.pojo.EnvData;
import com.paiad.smartagriculture.service.AlarmRuleService;
import com.paiad.smartagriculture.service.AlarmService;
import com.paiad.smartagriculture.service.DeviceService;
import com.paiad.smartagriculture.service.EnvDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class EnvDataServiceImpl extends ServiceImpl<EnvDataMapper, EnvData> implements EnvDataService {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private AlarmRuleService alarmRuleService;

    @Autowired
    private AlarmService alarmService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processAndSave(EnvData envData) {
        // 1. Save Environment Data
        this.save(envData);

        // 2. Check Alarms
        checkAndGenerateAlarms(envData);
    }

    private void checkAndGenerateAlarms(EnvData envData) {
        // Find Device Info
        Device device = deviceService.getOne(new LambdaQueryWrapper<Device>()
                .eq(Device::getDeviceId, envData.getDeviceId()));

        if (device == null) {
            log.warn("Device not found for ID: {}", envData.getDeviceId());
            return;
        }

        // Find Matching Rules
        // Rule: Enabled AND (Specific Device ID match OR (Generic Rule where device_id
        // is null AND device_type matches))
        List<AlarmRule> rules = alarmRuleService.list(new LambdaQueryWrapper<AlarmRule>()
                .eq(AlarmRule::getEnabled, 1)
                .and(w -> w.eq(AlarmRule::getDeviceId, device.getDeviceId())
                        .or(o -> o.isNull(AlarmRule::getDeviceId).eq(AlarmRule::getDeviceType,
                                device.getDeviceType()))));

        for (AlarmRule rule : rules) {
            checkRule(envData, rule, device);
        }
    }

    private void checkRule(EnvData envData, AlarmRule rule, Device device) {
        BigDecimal value = getValueByMetric(envData, rule.getMetric());
        if (value == null) {
            return; // No data for this metric
        }

        boolean triggered = false;
        String message = "";

        // Check Min
        if (rule.getMinValue() != null && value.compareTo(rule.getMinValue()) < 0) {
            triggered = true;
            message = String.format("指标[%s]值[%.2f]低于下限[%.2f]", rule.getMetric(), value, rule.getMinValue());
        }

        // Check Max
        if (rule.getMaxValue() != null && value.compareTo(rule.getMaxValue()) > 0) {
            triggered = true;
            message = String.format("指标[%s]值[%.2f]高于上限[%.2f]", rule.getMetric(), value, rule.getMaxValue());
        }

        if (triggered) {
            createAlarm(envData, rule, value, message);
        }
    }

    private void createAlarm(EnvData envData, AlarmRule rule, BigDecimal value, String message) {
        Alarm alarm = new Alarm();
        alarm.setDeviceId(envData.getDeviceId());
        alarm.setMetric(rule.getMetric());
        alarm.setValue(value);
        alarm.setMinValue(rule.getMinValue());
        alarm.setMaxValue(rule.getMaxValue());
        alarm.setLevel(rule.getLevel());
        alarm.setStatus(0); // 0: Unconfirmed
        alarm.setTriggeredAt(envData.getTs() != null ? envData.getTs() : LocalDateTime.now());
        alarm.setMessage(message);
        alarm.setRuleId(rule.getId());

        alarmService.save(alarm);
        log.info("Alarm generated: {}", message);
    }

    private BigDecimal getValueByMetric(EnvData data, String metric) {
        if (metric == null)
            return null;
        switch (metric) {
            case "sHR":
                return data.getSoilHumidity();
            case "sEC":
                return data.getSoilEc();
            case "sPH":
                return data.getSoilPh();
            case "CO2":
                return data.getCo2();
            case "NN":
                return data.getNitrogen();
            case "PP":
                return data.getPhosphorus();
            case "sTMP":
                return data.getSoilTemp();
            case "KK":
                return data.getPotassium();
            case "ILL":
                return data.getIlluminance();
            case "HR":
                return data.getHumidity();
            case "FS":
                return data.getWindSpeed();
            case "TMP":
                return data.getTemperature();
            case "PRS":
                return data.getPressure();
            case "RVC":
                return data.getRainfall();
            // String types or unsupported types return null for numeric check
            case "FX":
            case "YX":
            default:
                return null;
        }
    }
}
