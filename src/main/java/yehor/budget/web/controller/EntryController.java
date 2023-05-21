package yehor.budget.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EntryController {

    @GetMapping(value = "/{path:[^\\.]*}")
    public String redirectToReactApp() {
        return "forward:/index.html";
    }

}