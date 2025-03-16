package org.swiftcodes.restapi;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SwiftCodesRestApiErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        return "{\"message\" : \"Page does not exist\"}";
    }


}
