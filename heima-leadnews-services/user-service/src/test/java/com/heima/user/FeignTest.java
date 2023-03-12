package com.heima.user;

import com.heima.feigns.WemediaFeign;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class FeignTest {

        //    feign接口 未注入 ？？？？
        @Autowired
        WemediaFeign wemediaFeign;
        @Test
        public void findByUser(){

            ResponseResult<WmUser> responseResult = wemediaFeign.findByName("admin");
            System.out.println(responseResult);
        }

}