package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.common.datasource.annotation.Slave;
import com.ruoyi.system.api.domain.AuditUser;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @program: KaiYu-Cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-06-26 08:59
 **/
@Slave
public interface AuditUserMapper extends BaseMapper<AuditUser> {

    @Select("select * from audit_user")
    List<AuditUser> getAllAuditUser();
}
