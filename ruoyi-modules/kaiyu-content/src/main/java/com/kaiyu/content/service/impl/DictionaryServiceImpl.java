package com.kaiyu.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kaiyu.content.domain.Dictionary;
import com.kaiyu.content.mapper.DictionaryMapper;
import com.kaiyu.content.service.DictionaryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: KaiYu-Cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-07-01 11:14
 **/
@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService {


    @Override
    public List<Dictionary> queryAll() {

        List<Dictionary> list = this.list();

        return list;
    }

    @Override
    public Dictionary getByCode(String code) {


        LambdaQueryWrapper<Dictionary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dictionary::getCode, code);

        Dictionary dictionary = this.getOne(queryWrapper);

        return dictionary;
    }
}
