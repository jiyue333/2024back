package com.cq.cd.interceptor;

import com.cq.cd.util.JwtTokenUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminInterceptor implements HandlerInterceptor {
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 从请求头中获取 token
		String authHeader = request.getHeader("Authorization");
		String role = JwtTokenUtil.getclaims(authHeader).get("role", String.class);

		String prefix = "Bearer ";
		if (authHeader.startsWith(prefix)) {
			authHeader = authHeader.substring(prefix.length());
		}
		// 如果没有传递 token 则返回 401 未授权
		if (authHeader == null || authHeader.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}

		// 验证 token 是否有效
		Boolean isValid = JwtTokenUtil.validateToken(authHeader);

		if (!isValid && role.equals("1111")) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}

		// token 验证通过，继续执行后续的处理
		return true;

	}
	//default 可以在接口提供默认实现
}
