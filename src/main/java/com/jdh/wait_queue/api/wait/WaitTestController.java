package com.jdh.wait_queue.api.wait;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WaitTestController {

    @GetMapping("wait")
    public String waitQueue() {
        return "/wait";
    }

}
