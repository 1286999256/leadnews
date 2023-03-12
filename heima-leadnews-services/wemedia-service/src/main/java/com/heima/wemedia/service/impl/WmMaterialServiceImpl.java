package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.exception.CustException;
import com.heima.file.service.FileStorageService;
import com.heima.model.WmThreadLocalUtils;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import com.mysql.fabric.xmlrpc.base.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;


public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {


    @Autowired
    FileStorageService fileStorageService;
    @Value("${file.oss.prefix}")
    String prefix;
    @Value("${file.oss.web-site}")
    String webSite;

    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        // 1 参数校验 文件名不为空 文件长度 用户登录  文件正确后缀
        if (multipartFile == null || multipartFile.getSize()==0) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID,"参数非法，文件不为空，文件大小不为0");
        }
        //  1.1 获取当前用户从 ThreadLocal 有工具类 WmThreadLocalUtils
        WmUser user = WmThreadLocalUtils.getUser();

        if (user ==null){
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID,"参数非法，用户未登录");
        }
        //  获取文件原始名 再获取后缀
        String originalFilename = multipartFile.getOriginalFilename();
        if (!CheckFileSuffix(originalFilename)){
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID,"文件后缀错误");
        }
        // 2 上传到oss
        String filedId = null;
        try {
            // 2.1随机生成文件前缀文件名 用uuid 获取文件后缀   1231313-12121 替换横线
            String filename = UUID.randomUUID().toString().replace("-","");
            //  2.2 文件后缀 用原始文件名截取
            String suffix = originalFilename.substring(originalFilename.indexOf("."));
            //  调用上传的 工具类oss  参数 前缀读配置 文件名+后缀  文件流
            filedId = fileStorageService.store(prefix, filename + suffix, multipartFile.getInputStream());
        }catch (IOException e){
            e.printStackTrace();
            log.error("oss上传失败");
        }
        // 3 封装数据并保持到素材库中
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUserId(user.getId());
        wmMaterial.setUrl(filedId);
        wmMaterial.setType((short)0);
        wmMaterial.setIsCollection((short)0);
        wmMaterial.setCreatedTime(new Date());
//        保存结果
        save(wmMaterial);
//        前端回显示 该地址
        wmMaterial.setUrl(webSite+filedId);
        // 4 返回结果
        return ResponseResult.okResult(wmMaterial);
    }

    private boolean CheckFileSuffix(String path) {
        if (StringUtils.isBlank(path)){
            return  false;
        }
//            定义一个List 集合放置 允许文件后缀
        List<String> allowSuffix = Arrays.asList("jpg","jpeg", "png", "gif");
        boolean isAllow = false;
        for (String suffix:allowSuffix){
            if (path.endsWith(suffix)){
                isAllow=true;
            }
        }
        return false;
    }
}
