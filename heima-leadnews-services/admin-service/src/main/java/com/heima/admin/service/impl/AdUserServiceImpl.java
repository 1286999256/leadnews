package com.heima.admin.service.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.AdUserMapper;
import com.heima.admin.service.AdUserService;
import com.heima.common.exception.CustException;
import com.heima.model.admin.dtos.AdUserDTO;
import com.heima.model.admin.pojos.AdUser;
import com.heima.model.admin.vos.AdUserVO;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.common.AppJwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdUserServiceImpl extends ServiceImpl<AdUserMapper, AdUser> implements AdUserService{

    @Override
    public ResponseResult login(AdUserDTO dto) {

//        1.校验参数 name password 不为空   dto.get获取不同页面参数  写错了注意 noblank 与blank
        String name = dto.getName();
        String password = dto.getPassword();
        if (StringUtils.isBlank(name)||StringUtils.isBlank(password)) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID,"用户名或者密码不能为空");
        }

//        2.根据name查询用户信息
        AdUser adUser = this.getOne(Wrappers.<AdUser>lambdaQuery().eq(AdUser::getName, name));
        if (adUser ==null){
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST,"用户名不存在");
        }
//        3.判断 页面数据库密码 一致 
//        页面密码+数据库salt盐  页面输入密码
        String dbPwd = adUser.getPassword(); // 数据库密码（加密）
        String salt = adUser.getSalt();
        // 用户输入密码（加密后）
        String newPwd = DigestUtils.md5DigestAsHex((dto.getPassword() + salt).getBytes());
        if (!dbPwd.equals(newPwd)) {
            CustException.cust(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR,"用户名或密码错误");
        }
//        4.判断  用户状态状态正常 9
           if (adUser.getStatus().intValue() != 9){
               CustException.cust(AppHttpCodeEnum.LOGIN_STATUS_ERROR);
           }

//        5.修改最近登录时间
            adUser.setLoginTime(new Date());
            this.updateById(adUser);
//        6.颁发token
        String token = AppJwtUtil.getToken(Long.valueOf(adUser.getId()));
//        7.封装返回结果 token 和用户信息  可能写错 token在上面  把map返回
        Map map = new HashMap<>();
        map.put("token",token);
//     通过vo 拷贝adUser属性  用spring框架的 beanUtils  减少暴露不必要信息给前端
        AdUserVO adUserVO = new AdUserVO();
        BeanUtils.copyProperties(adUser,adUserVO);

        map.put("user",adUserVO);
        return ResponseResult.okResult(map);
    }
}
