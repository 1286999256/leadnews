package com.heima.wemedia.controller.v1;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.wemedia.service.WmUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vi/user")
@Api("自媒体用户controller")
public class WmUserController {

    @Autowired
    WmUserService wmUserService;

//    业务简单 可以controller 直接调用方法 不用写service
    @ApiOperation("用户名查询自媒体信息")
    @GetMapping("/findByName/{name}")
    public ResponseResult findByName(@PathVariable ("name") String name){
        WmUser wmUser = wmUserService.getOne(Wrappers.<WmUser>lambdaQuery().eq(WmUser::getName, name));
        return ResponseResult.okResult(wmUser);
    }

    @ApiOperation("保存自媒体用户信息")
    @PostMapping("/save")
    public ResponseResult save(@RequestBody WmUser wmUser){
        wmUserService.save(wmUser);
//        自动生成id  mybatisplus
        return ResponseResult.okResult(wmUser);
    }



}
