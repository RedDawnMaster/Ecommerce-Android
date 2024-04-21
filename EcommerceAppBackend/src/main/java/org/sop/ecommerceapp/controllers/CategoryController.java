package org.sop.ecommerceapp.controllers;

import java.util.List;

import org.sop.ecommerceapp.models.Category;
import org.sop.ecommerceapp.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public List<Category> findAll() {
        return categoryService.findAll();
    }

    @DeleteMapping("/{label}")
    public int deleteByLabel(@PathVariable String label) {
        return categoryService.deleteByLabel(label);
    }

    @PostMapping("/")
    public Category save(@RequestBody Category category) {
        return categoryService.save(category);
    }

    @PutMapping("/")
    public int update(@RequestBody Category category) {
        return categoryService.update(category);
    }
}
