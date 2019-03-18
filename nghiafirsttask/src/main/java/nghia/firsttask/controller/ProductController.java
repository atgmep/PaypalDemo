package nghia.firsttask.controller;


import nghia.firsttask.entity.Product;
import nghia.firsttask.model.Response;
import nghia.firsttask.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@Controller
public class ProductController extends AbstractController {

    private static final String API = "/api/product";
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(API)
    public String viewAllProduct() {
        Response<List<Product>> response = new Response<>(Response.STATUS_FAIL, Response.MESSAGE_FAIL);
        try {
            List<Product> products = productService.findAll();
            response.setResponse(Response.STATUS_SUCCESS, Response.MESSAGE_SUCCESS, products);
        } catch (Exception e) {
            e.printStackTrace();
            response.setResponse(Response.STATUS_SERVER_ERROR, Response.MESSAGE_SERVER_ERROR);
        }
        return gson.toJson(response);
    }

    @PostMapping(API)
    public String createProduct(@RequestParam String name, @RequestParam Double price) {
        Response response = new Response<>(Response.STATUS_FAIL, Response.MESSAGE_FAIL);
        try {
            Product product = productService.createProduct(name, price);
            response.setResponse(Response.STATUS_SUCCESS, Response.MESSAGE_SUCCESS, product.getId());
        } catch (Exception e) {
            e.printStackTrace();
            response.setResponse(Response.STATUS_SERVER_ERROR, Response.MESSAGE_SERVER_ERROR);
        }
        return gson.toJson(response);
    }

    @PutMapping(API)
    public String updateProduct(@RequestParam Integer proId, @RequestParam String name, @RequestParam Double price) {
        Response response = new Response<>(Response.STATUS_FAIL, Response.MESSAGE_FAIL);
        try {
            boolean success = productService.updateProduct(proId, name, price);
            if (success) {
                response.setResponse(Response.STATUS_SUCCESS, Response.MESSAGE_SUCCESS);
            } else {
                response.setResponse(Response.STATUS_NO_RESULT, Response.MESSAGE_NO_RESULT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setResponse(Response.STATUS_SERVER_ERROR, Response.MESSAGE_SERVER_ERROR);
        }
        return gson.toJson(response);
    }

    @DeleteMapping(API)
    public String delProduct(@RequestParam int proId) {
        Response response = new Response<>(Response.STATUS_FAIL, Response.MESSAGE_FAIL);
        try {
            boolean success = productService.hideProduct(proId);
            if (success) {
                response.setResponse(Response.STATUS_SUCCESS, Response.MESSAGE_SUCCESS);
            } else {
                response.setResponse(Response.STATUS_NO_RESULT, Response.MESSAGE_NO_RESULT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setResponse(Response.STATUS_SERVER_ERROR, Response.MESSAGE_SERVER_ERROR);
        }
        return gson.toJson(response);
    }
}
