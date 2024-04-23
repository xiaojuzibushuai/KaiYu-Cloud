package com.kaiyu.content.service;

import com.kaiyu.content.domain.Device;
import com.kaiyu.content.domain.DeviceGroup;
import org.apache.poi.ss.formula.functions.T;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-19 15:09
 **/
public interface IDeviceService {

    List<Device> getDeviceListBySceneid(String sceneid);

    List getSceneList(String phone);
}
