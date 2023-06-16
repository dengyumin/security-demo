package com.smallmin.security.demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dengyumin
 */
@RestController
public class TestController {

    /**
     * 登录成功后跳转的路径
     */
    @PostMapping("/success")
    @ResponseBody
    public String success() {
        return "success!";
    }

}
