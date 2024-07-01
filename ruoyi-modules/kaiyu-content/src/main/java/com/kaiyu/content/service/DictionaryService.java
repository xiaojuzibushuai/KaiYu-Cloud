package com.kaiyu.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kaiyu.content.domain.Dictionary;

import java.util.List;

/**
 * @program: KaiYu-Cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-07-01 11:13
 **/
public interface DictionaryService extends IService<Dictionary> {

    /**
     * 查询所有数据字典内容
     * @return
     */
    List<Dictionary> queryAll();

    /**
     * 根据code查询数据字典
     * @param code -- String 数据字典Code
     * @return
     */
    Dictionary getByCode(String code);
}

