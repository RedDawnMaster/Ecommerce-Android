package org.sop.ecommerceapp.controllers;

import org.sop.ecommerceapp.models.Product;
import org.sop.ecommerceapp.models.Response;
import org.sop.ecommerceapp.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/id/{id}")
    public Product findById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @GetMapping("/{label}")
    public List<Product> findByLabelContains(@PathVariable String label) {
        return productService.findByLabelContains(label);
    }

    @GetMapping("/category/{label}")
    public List<Product> findByCategoryLabel(@PathVariable String label) {
        return productService.findByCategoryLabel(label);
    }

    @GetMapping("/{lowerBoundPrice}/{higherBoundPrice}")
    public List<Product> findByPriceBetween(@PathVariable double lowerBoundPrice,
                                            @PathVariable double higherBoundPrice) {
        return productService.findByPriceBetween(lowerBoundPrice, higherBoundPrice);
    }

    @GetMapping("/")
    public List<Product> findAll() {
        return productService.findAll();
    }

    @GetMapping("/countProductsByPrice/{lowerBoundPrice}/{higherBoundPrice}")
    public int countByPriceBetween(@PathVariable double lowerBoundPrice, @PathVariable double higherBoundPrice) {
        return this.productService.countByPriceBetween(lowerBoundPrice, higherBoundPrice);
    }

    @GetMapping("/countByCategoryLabel/{label}")
    public int countByCategoryLabel(@PathVariable String label) {
        return this.productService.countByCategoryLabel(label);
    }

    @DeleteMapping("/{label}")
    public Response deleteByLabel(@PathVariable String label) {
        return productService.deleteByLabel(label);
    }

    @PostMapping("/")
    public Product save(@RequestBody Product product) {
        return productService.save(product);
    }

    @PutMapping("/")
    public Product update(@RequestBody Product product) {
        return productService.update(product);
    }

}
