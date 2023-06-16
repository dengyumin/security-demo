package com.smallmin.security.demo.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author dengyumin
 */
public class WebLoginFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!RequestMethod.POST.name().equals(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported:" + request.getMethod());
        }

        String verifyCode = (String) request.getSession().getAttribute("verify_code");
        if (request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {
            Map<String, String> loginData = new HashMap<>();
            try (InputStream inputStream = request.getInputStream()) {
                loginData = new ObjectMapper().readValue(inputStream, new TypeReference<>() {
                });
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                String code = loginData.get("code");
                // 校验验证码是否正确
                checkCode(verifyCode, code);
            }

            String username = Optional.ofNullable(loginData.get(getUsernameParameter())).orElse("").trim();
            String password = Optional.ofNullable(loginData.get(getPasswordParameter())).orElse("");

            UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
            setDetails(request, authRequest);

            return this.getAuthenticationManager().authenticate(authRequest);
        }
        throw new AuthenticationServiceException("Authentication ContentType not supported:" + request.getContentType());

    }

    /**
     * 校验验证码
     *
     * @param verifyCode 后端生成的验证码
     * @param clientCode 客户端发送的验证码
     */
    private void checkCode(String verifyCode, String clientCode) {
        if (!StringUtils.hasText(verifyCode) || !StringUtils.hasText(clientCode) || !verifyCode.equalsIgnoreCase(clientCode)) {
            throw new AuthenticationServiceException("verifyCode is error !");
        }
    }
}
