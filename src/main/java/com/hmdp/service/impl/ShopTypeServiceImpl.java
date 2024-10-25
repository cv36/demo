package com.hmdp.service.impl;

import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {


    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Override
    public Result queryTypeList() {
        // Redis key, 用来存储和获取数据
        String key = "shopTypeList";

        // 从 Redis 获取数据
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json != null) {
            // 如果缓存中有数据，则将其转换为对象返回
            List<ShopType> list = JSONUtil.toList(json, ShopType.class);
            return Result.ok(list);
        }

        // 如果缓存中没有数据，从数据库查询
        List<ShopType> shopTypes = list();
        if (shopTypes.isEmpty()) {
            return Result.fail("无店铺类型");
        }

        // 查询结果存入 Redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shopTypes));

        return Result.ok(shopTypes);
    }
}

