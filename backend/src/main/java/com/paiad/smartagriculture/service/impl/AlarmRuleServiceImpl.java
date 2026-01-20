package com.paiad.smartagriculture.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paiad.smartagriculture.common.constants.RedisConstants;
import com.paiad.smartagriculture.mapper.AlarmRuleMapper;
import com.paiad.smartagriculture.model.pojo.AlarmRule;
import com.paiad.smartagriculture.service.AlarmRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmRuleServiceImpl extends ServiceImpl<AlarmRuleMapper, AlarmRule> implements AlarmRuleService {

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean save(AlarmRule entity) {
        boolean result = super.save(entity);
        if (result) {
            updateRuleInCache(entity);
        }
        return result;
    }

    @Override
    public boolean updateById(AlarmRule entity) {
        boolean result = super.updateById(entity);
        if (result) {
            // 更新通常需要最新的完整对象，如果 entity 字段不全，建议通过 id 重新查一次
            updateRuleInCache(this.getById(entity.getId()));
        }
        return result;
    }

    @Override
    public boolean removeById(Serializable id) {
        boolean result = super.removeById(id);
        if (result) {
            redisTemplate.opsForHash().delete(RedisConstants.ALARM_RULES_KEY, String.valueOf(id));
            log.info("Deleted alarm rule from cache: {}", id);
        }
        return result;
    }

    @Override
    public boolean removeByIds(Collection<?> list) {
        boolean result = super.removeByIds(list);
        if (result) {
            list.forEach(id -> redisTemplate.opsForHash().delete(RedisConstants.ALARM_RULES_KEY, String.valueOf(id)));
            log.info("Deleted multiple alarm rules from cache: {}", list);
        }
        return result;
    }

    @Override
    public void reloadCache() {
        redisTemplate.delete(RedisConstants.ALARM_RULES_KEY);
        List<AlarmRule> enabledRules = this.list(new LambdaQueryWrapper<AlarmRule>().eq(AlarmRule::getEnabled, 1));
        for (AlarmRule rule : enabledRules) {
            updateRuleInCache(rule);
        }
        // 设置 1 小时过期时间，作为最终一致性兜底
        redisTemplate.expire(RedisConstants.ALARM_RULES_KEY, 1, TimeUnit.HOURS);
        log.info("Reloaded all {} enabled alarm rules to cache with 1h TTL", enabledRules.size());
    }

    private void updateRuleInCache(AlarmRule rule) {
        if (rule == null)
            return;
        if (rule.getEnabled() == 1) {
            redisTemplate.opsForHash().put(RedisConstants.ALARM_RULES_KEY, String.valueOf(rule.getId()),
                    JSONUtil.toJsonStr(rule));
            log.info("Updated alarm rule in cache: {}", rule.getId());
        } else {
            redisTemplate.opsForHash().delete(RedisConstants.ALARM_RULES_KEY, String.valueOf(rule.getId()));
            log.info("Removed disabled alarm rule from cache: {}", rule.getId());
        }
    }
}
