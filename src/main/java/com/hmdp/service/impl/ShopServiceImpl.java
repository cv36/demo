package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.CACHE_NULL_TTL;
import static com.hmdp.utils.RedisConstants.CACHE_SHOP_TTL;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryById(Long id) {

        String shopJson = stringRedisTemplate.opsForValue().get("cache:shop:" + id);

        if(StrUtil.isNotBlank(shopJson)){
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return Result.ok(shop);
        }
        if(shopJson!=null){
            return Result.fail("店铺信息不存在");
        }

        Shop byId = getById(id);

        if(byId == null){
            stringRedisTemplate.opsForValue().set("cache:shop:" + id, "",CACHE_NULL_TTL, TimeUnit.MINUTES);

            return Result.fail("店铺不存在");
        }

        stringRedisTemplate.opsForValue().set("cache:shop:" + id, JSONUtil.toJsonStr(byId),CACHE_SHOP_TTL, TimeUnit.MINUTES);

        return Result.ok(byId);



    }
}
