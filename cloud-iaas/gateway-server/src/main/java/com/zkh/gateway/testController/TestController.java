package com.zkh.gateway.testController;

import com.zkh.gateway.config.UrlAllowedConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class TestController {
    @Resource
    private UrlAllowedConfig urlAllowedConfig;

    @GetMapping("testGetConfig")
    public String testGetConfig() {
        try {
            List<String> urlAllowed2 = urlAllowedConfig.getUrlAllowed();
            String string = urlAllowed2.get(0);
            String string1 = urlAllowed2.get(1);
//            String string2 = urlAllowedConfig.urlAllowed.toString();
            return string+"\t"+string1;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "testGetConfig 出错了";
    }
}
