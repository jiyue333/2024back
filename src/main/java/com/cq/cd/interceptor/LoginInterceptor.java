package com.cq.cd.interceptor;

import com.cq.cd.util.JwtTokenUtil;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取 token
//        String token = request.getHeader("Authorization");
//
//        // 如果没有传递 token，则返回 401 未授权
//        if (token == null || token.isEmpty()) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            return false;
//        }
//
//        // 验证 token 是否有效
//        Boolean isValid = JwtTokenUtil.validateToken(token);
//
//        if (!isValid) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            return false;
//        }

        // token 验证通过，继续执行后续的处理
        return true;
    }
	//default 可以在接口提供默认实现
}
