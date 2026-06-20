package com.walkmates.web;

import com.walkmates.repository.ListingRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Serves the thin demo home page listing the available opportunities. */
@Controller
public class HomeController {

    private final ListingRepository listings;

    public HomeController(ListingRepository listings) {
        this.listings = listings;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("listings", listings.findAll());
        return "index";
    }
}
