// com.example.charchweb.Router.AdminController.java
package com.example.charchweb.Router;

import com.example.charchweb.DTO.Sermon;
import com.example.charchweb.service.sermonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Controller
@RequestMapping("/andio")
public class adminController {


    @GetMapping("/login")
    public String login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/andio";
        }
        return "admin/login";
    }

    @GetMapping("")
    public String adminPage(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            return "admin/dashBoard";
        }
        return "admin/login";
    }


}