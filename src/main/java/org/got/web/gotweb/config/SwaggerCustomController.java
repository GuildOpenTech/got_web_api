package org.got.web.gotweb.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerCustomController {

    @GetMapping("/swagger/custom-ui")
    public String customSwaggerUI() {
        return "custom-swagger-ui";
    }
}