package org.sop.ecommerceapp.controllers;

import java.util.List;

import org.sop.ecommerceapp.models.Review;
import org.sop.ecommerceapp.services.ReviewService;
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
@RequestMapping("api/review")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @GetMapping("/{label}/{username}")
    public List<Review> findByProductLabelAndUserUsernameContains(@PathVariable String label,
                                                                  @PathVariable String username) {
        return reviewService.findByProductLabelAndUserUsernameContains(label, username);
    }

    @GetMapping("/{label}")
    public List<Review> findByProductLabel(@PathVariable String label) {
        return reviewService.findByProductLabel(label);
    }

    @DeleteMapping("/{username}/{label}")
    public void deleteByUserUsernameAndProductLabel(@PathVariable String username, @PathVariable String label) {
        reviewService.deleteByUserUsernameAndProductLabel(username, label);
    }

    @PostMapping("/")
    public Review save(@RequestBody Review review) {
        return reviewService.save(review);
    }

    @PutMapping("/")
    public void update(@RequestBody Review review) {
        reviewService.update(review);
        ;
    }
}
