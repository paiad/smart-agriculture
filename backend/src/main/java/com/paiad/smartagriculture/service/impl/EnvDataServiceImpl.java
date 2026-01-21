package com.paiad.smartagriculture.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paiad.smartagriculture.common.constants.MetricConstants;
import com.paiad.smartagriculture.common.constants.RedisConstants;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EnvDataServiceImpl extends ServiceImpl<EnvDataMapper, EnvData> implements EnvDataService {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private AlarmRuleService alarmRuleService;

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processAndSave(EnvData envData) {
        // 1. 保存环境数据
        this.save(envData);

        // 2. 检查报警
        checkAndGenerateAlarms(envData);
    }

    private void checkAndGenerateAlarms(EnvData envData) {
        // 获取设备信息
        Device device = deviceService.getOne(new LambdaQueryWrapper<Device>()
                .eq(Device::getDeviceId, envData.getDeviceId()));

        if (device == null) {
            log.warn("Device not found for ID: {}", envData.getDeviceId());
            return;
        }

        // 1. 优先从 Redis 获取所有启用的规则
        List<AlarmRule> allEnabledRules;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(RedisConstants.ALARM_RULES_KEY);
        if (entries != null && !entries.isEmpty()) {
            log.info("Alarm rule cache hit, count: {}", entries.size());
            allEnabledRules = entries.values().stream()
                    .map(obj -> JSONUtil.toBean((String) obj, AlarmRule.class))
                    .collect(Collectors.toList());
        } else {
            // 缓存失效，回源并重填
            log.info("Alarm rule cache miss, fallback to database");
            alarmRuleService.reloadCache();
            allEnabledRules = alarmRuleService.list(new LambdaQueryWrapper<AlarmRule>().eq(AlarmRule::getEnabled, 1));
        }

        // 2. 筛选匹配特定设备ID 或 全局规则(设备ID为空) 的规则
        List<AlarmRule> relevantRules = allEnabledRules.stream()
                .filter(r -> r.getDeviceId() == null || r.getDeviceId().equals(device.getDeviceId()))
                .collect(Collectors.toList());

        // 3. 按指标分组并优先考虑特定规则（deviceId 不为空）
        Map<String, AlarmRule> effectiveRules = new HashMap<>();
        for (AlarmRule rule : relevantRules) {
            String metric = rule.getMetric();
            if (!effectiveRules.containsKey(metric)) {
                effectiveRules.put(metric, rule);
            } else {
                AlarmRule existing = effectiveRules.get(metric);
                if (existing.getDeviceId() == null && rule.getDeviceId() != null) {
                    effectiveRules.put(metric, rule);
                }
            }
        }

        // 4. 执行有效规则
        for (AlarmRule rule : effectiveRules.values()) {
            checkRule(envData, rule, device);
        }
    }

    private void checkRule(EnvData envData, AlarmRule rule, Device device) {
        BigDecimal value = getValueByMetric(envData, rule.getMetric());
        if (value == null) {
            return; // 无此指标数据
        }

        boolean triggered = false;
        String message = "";

        // 检查下限
        if (rule.getMinValue() != null && value.compareTo(rule.getMinValue()) < 0) {
            triggered = true;
            message = String.format("指标[%s]值[%.2f]低于下限[%.2f]", rule.getMetric(), value, rule.getMinValue());
        }

        // 检查上限
        if (rule.getMaxValue() != null && value.compareTo(rule.getMaxValue()) > 0) {
            triggered = true;
            message = String.format("指标[%s]值[%.2f]高于上限[%.2f]", rule.getMetric(), value, rule.getMaxValue());
        }

        if (triggered) {
            createAlarm(envData, rule, value, message);
        }
    }

    private void createAlarm(EnvData envData, AlarmRule rule, BigDecimal value, String message) {
        // 告警抑制检查：5分钟内同设备同指标不重复告警
        String suppressionKey = RedisConstants.ALARM_SUPPRESSION_PREFIX
                + envData.getDeviceId() + ":" + rule.getMetric();

        Boolean exists = redisTemplate.hasKey(suppressionKey);
        if (Boolean.TRUE.equals(exists)) {
            log.debug("Alarm suppressed for device={}, metric={} (within suppression window)",
                    envData.getDeviceId(), rule.getMetric());
            return;
        }

        // 创建告警记录
        Alarm alarm = Alarm.builder()
                .deviceId(envData.getDeviceId())
                .metric(rule.getMetric())
                .value(value)
                .minValue(rule.getMinValue())
                .maxValue(rule.getMaxValue())
                .level(rule.getLevel())
                .status(0) // 0: 未确认
                .triggeredAt(envData.getTs() != null ? envData.getTs() : LocalDateTime.now())
                .message(message)
                .ruleId(rule.getId())
                .build();

        alarmService.save(alarm);

        // 设置抑制标记，5分钟后过期
        redisTemplate.opsForValue().set(suppressionKey, "1",
                java.time.Duration.ofMinutes(RedisConstants.ALARM_SUPPRESSION_TTL_MINUTES));

        log.info("Alarm generated: {} (suppression set for {} minutes)",
                message, RedisConstants.ALARM_SUPPRESSION_TTL_MINUTES);
    }

    private BigDecimal getValueByMetric(EnvData data, String metric) {
        if (metric == null)
            return null;
        switch (metric) {
            case MetricConstants.SOIL_HUMIDITY:
                return data.getSoilHumidity();
            case MetricConstants.SOIL_EC:
                return data.getSoilEc();
            case MetricConstants.SOIL_PH:
                return data.getSoilPh();
            case MetricConstants.CO2:
                return data.getCo2();
            case MetricConstants.NITROGEN:
                return data.getNitrogen();
            case MetricConstants.PHOSPHORUS:
                return data.getPhosphorus();
            case MetricConstants.SOIL_TEMP:
                return data.getSoilTemp();
            case MetricConstants.POTASSIUM:
                return data.getPotassium();
            case MetricConstants.ILLUMINANCE:
                return data.getIlluminance();
            case MetricConstants.HUMIDITY:
                return data.getHumidity();
            case MetricConstants.WIND_SPEED:
                return data.getWindSpeed();
            case MetricConstants.TEMPERATURE:
                return data.getTemperature();
            case MetricConstants.PRESSURE:
                return data.getPressure();
            case MetricConstants.RAINFALL:
                return data.getRainfall();
            // 字符串类型或不支持的类型在数值检查时返回 null
            case MetricConstants.WIND_DIR:
            case MetricConstants.PRECIP:
            default:
                return null;
        }
    }
}
