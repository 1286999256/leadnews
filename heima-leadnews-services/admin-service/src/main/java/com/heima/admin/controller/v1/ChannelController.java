package com.heima.admin.controller.v1;

import com.heima.admin.service.ChannelService;
import com.heima.model.admin.dtos.ChannelDTO;
import com.heima.model.admin.pojos.AdChannel;
import com.heima.model.common.dtos.ResponseResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/channel")
public class ChannelController {

    @Autowired
    private ChannelService channelService;

    @PostMapping("/list")
    public ResponseResult list(@RequestBody ChannelDTO dto,@RequestHeader("userId") String usrId){
        System.out.println(usrId);
        return channelService.findByNameAndPage(dto);
    }

    @ApiOperation(value = "保存频道信息")
    @PostMapping("/save")
    public ResponseResult save(@RequestBody AdChannel channel){

        return channelService.insert(channel);
    }

    @ApiOperation(value = "修改频道信息")
    @PostMapping("/update")
    public ResponseResult update(@RequestBody AdChannel channel){

        return channelService.update(channel);
    }

    @ApiOperation("根据频道ID删除")
    @GetMapping("/del/{id}")
    public ResponseResult deleteById(@PathVariable("id") Integer id) {
        return channelService.deleteById(id);
    }

}
