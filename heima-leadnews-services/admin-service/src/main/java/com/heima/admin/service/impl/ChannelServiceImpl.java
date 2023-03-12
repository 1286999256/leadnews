package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.ChannelMapper;
import com.heima.admin.service.ChannelService;
import com.heima.model.admin.dtos.ChannelDTO;
import com.heima.model.admin.pojos.AdChannel;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ChannelServiceImpl extends ServiceImpl<ChannelMapper, AdChannel> implements ChannelService {


//    查找 根据名称 分页 重
    @Override
    public ResponseResult findByNameAndPage(ChannelDTO dto) {

//       1校验参数 非空 分页
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "参数错误");
        }

        dto.checkParam();
//        2条件查询 分页条件
        Page<AdChannel> pageReq = new Page<>(dto.getPage(), dto.getSize());
//        3封装page对象 mybatis plus分页查询 查询条件
        LambdaQueryWrapper<AdChannel> wrapper = Wrappers.lambdaQuery();
//        4封装条件参数 name status ord排序
        if (StringUtils.isNoneBlank(dto.getName())) {
//            查询哪个字段 字段值
            wrapper.like(AdChannel::getName, dto.getName());
        }
        if (dto.getStatus() != null) {
//            查询哪个字段 字段值
            wrapper.eq(AdChannel::getStatus, dto.getStatus());
        }
        wrapper.orderByAsc(AdChannel::getOrd);
//        5 执行查询 返回结果 封装 PageResponseResult
        IPage<AdChannel> pageResult = this.page(pageReq, wrapper);


        return new PageResponseResult(dto.getPage(), dto.getSize(), pageResult.getTotal(), pageResult.getRecords());
    }

//    新增 根据频道名称
    @Override
    public ResponseResult insert(AdChannel channel) {
//        1.参数校验（1 name不为空 2不能大于10字符 3不能重复）
        String name = channel.getName();
        if (StringUtils.isBlank(name)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "频道名称不能为空");
        }

        if (name.length() > 10) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "频道名称不能大于10");
        }
//        注意可能写错
        // 查询频道名称是否重复
        int count = this.count(Wrappers.<AdChannel>lambdaQuery().eq(AdChannel::getName, channel.getName()));
        if (count > 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "频道名称重复");
        }
        // 2.保持频道信息（create time 字段补全）
        channel.setCreatedTime(new Date());
        this.save(channel);

//        3.返回结果
        return ResponseResult.okResult();
    }

//    id不为空 频道不能已存在  新频道与老频道名称不一致 且新频道名称不为空  修改频道 重
    @Override
    public ResponseResult update(AdChannel adChannel) {
//        1.校验页面参数(id)  id参数不为空
        if (adChannel.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "频道id不能为空");
        }

//        2. 校验数据库频道是否存在  页面与数据库比对
//        新的频道名称和老的频道名称不一致  检查
//       拿着页面参数id 去数据库查  数据库中的频道名称 oldChannel
        AdChannel oldChannel = this.getById(adChannel.getId());
        if (oldChannel == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "频道不存在");
        }
//        3.校验页面参数(name) name参数不为空  校验数据库频道不一致
//        页面上的频道名称  参数
        String name = adChannel.getName();
//        频道名称 不为空 新的频道名称和老的频道名称不一致  可能出错
        if (StringUtils.isNoneBlank(name)&&!name.equals(oldChannel.getName())){
//            校验频道是否重复
            // 查询频道名称是否重复  频道重复返回页面 错误提示
            int count = this.count(Wrappers.<AdChannel>lambdaQuery().eq(AdChannel::getName, name));
            if (count > 0) {
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "频道名称重复");
            }
        }
//        3.频道不重复,修改频道  注意可能写错
            updateById(adChannel);
//        4.返回结果
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteById(Integer id) {

//        1.校验参数
//        1.校验页面参数(id)  id参数不为空
        //1.检查参数
        if(id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.判断当前频道是否存在 和 是否有效
        AdChannel adChannel = getById(id);
        if(adChannel==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        // 启用状态下不能删除
        if (adChannel.getStatus()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        //3.删除频道
        this.removeById(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }


}