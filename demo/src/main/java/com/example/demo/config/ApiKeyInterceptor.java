package com.example.demo.config;

import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    private static final String API_KEY = "my-secret-key";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String key = request.getHeader("x-api-key");

        if (API_KEY.equals(key)) {
            return true;
        }

        response.setStatus(401);
        response.getWriter().write("Unauthorized");
        return false;
    }
}