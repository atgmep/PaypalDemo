package nghia.firsttask.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductFrontController {
    @GetMapping("/")
    public String viewProductList2() {
        return "product";
    }

    @GetMapping("/product")
    public String viewProductList() {
        return "product";
    }
}
