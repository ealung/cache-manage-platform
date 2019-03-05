package org.channel.cache.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author zhangchanglu
 * @since 2018/12/31 14:04.
 */
@Controller
@RequestMapping("/cache")
public class IndexController {
    @RequestMapping({"/", "/index"})
    public ModelAndView index() {
        return new ModelAndView("/index");
    }

    @RequestMapping("/project/index")
    public ModelAndView projectIndex(@RequestParam String appName) {
        ModelAndView modelAndView = new ModelAndView("/projectIndex");
        modelAndView.addObject("appName", appName);
        return modelAndView;
    }

    @RequestMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("/login");
    }
}
