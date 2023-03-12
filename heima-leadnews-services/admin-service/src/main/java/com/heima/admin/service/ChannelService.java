package com.heima.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.dtos.ChannelDTO;
import com.heima.model.admin.pojos.AdChannel;
import com.heima.model.common.dtos.ResponseResult;

public interface ChannelService extends IService<AdChannel> {

    /**
     * 根据名称分页查询频道列表
     * @param dto
     * @return
     */
    public ResponseResult findByNameAndPage(ChannelDTO dto);

    /**
     * 根据频道对象 新增频道
     * @param AdChannel channel
     * @return
     */
    public ResponseResult insert(AdChannel channel);
    /**
     * 根据频道对象 修改频道
     * @param AdChannel channel
     * @return
     */
    public ResponseResult update(AdChannel channel);


    public ResponseResult deleteById(Integer id);

}
