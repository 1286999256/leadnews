package com.heima.article.conroller.v1;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.service.AuthorService;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/author")
public class AuthorController {

    @Autowired
    AuthorService authorService;
// 用户id 查询作者id  二表关联
    @GetMapping("/findByUserId/{id}")
    public ResponseResult findByUserId(@PathVariable("id") Integer id){
        ApAuthor one = authorService.getOne(Wrappers.<ApAuthor>lambdaQuery().eq(ApAuthor::getUserId, id));
        return  ResponseResult.okResult(one);
    }

    @PostMapping("/save")
    public ResponseResult save(@RequestBody ApAuthor apAuthor){
         authorService.save(apAuthor);
        return  ResponseResult.okResult();
    }

}
