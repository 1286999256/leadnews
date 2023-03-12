package com.heima.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.heima.gateway.util.AppJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
@Order(0)
public class AuthorizeFilter implements GlobalFilter {

    private static List<String> urlList = new ArrayList<>();
    // 初始化白名单 url路径
    static {
        urlList.add("/login/in");
        urlList.add("/v2/api-docs");
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//       0拿到请求路径
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().getPath();
//        判断白名单   //1 判断当前是否是登录请求，如果是登录则放行
        for (String allowUri : urlList){
           if (path.contains(allowUri)){
                return  chain.filter(exchange);
            }
        }
        //2 获取请求头jwt token信息
        String token = request.getHeaders().getFirst("token");
        if (StringUtils.isBlank(token)){
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            return response.setComplete();
            writeMessage(exchange,"token不为空");
        }

        //3 判断令牌信息是否正确 理解不够
       try {
           Claims claimsBody = AppJwtUtil.getClaimsBody(token);
           int i = AppJwtUtil.verifyToken(claimsBody);
           if (i<1){
               Object id = claimsBody.get("id");
               request.mutate().header("userId",String.valueOf(id));
               return chain.filter(exchange);
           }else {
               log.error("token过期");
               return writeMessage(exchange,"token过期");
           }
       }catch (Exception e){
            log.error("解析token失败 原因：{}",e.getMessage());
            return  writeMessage(exchange,"解析token失败"+e.getMessage());
       }
//        response.setStatusCode(HttpStatus.UNAUTHORIZED);
//        return response.setComplete();
    }


    /**
     * 返回错误提示信息
     * @return
     */
    private Mono<Void> writeMessage(ServerWebExchange exchange, String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", HttpStatus.UNAUTHORIZED.value());
        map.put("errorMessage", message);
        //获取响应对象
        ServerHttpResponse response = exchange.getResponse();
        //设置状态码
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        //response.setStatusCode(HttpStatus.OK);
        //设置返回类型
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        //设置返回数据
        DataBuffer buffer = response.bufferFactory().wrap(JSON.toJSONBytes(map));
        //响应数据回浏览器
        return response.writeWith(Flux.just(buffer));
    }
}
