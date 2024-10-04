package org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.controller;

import jakarta.servlet.http.HttpSession;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.entitys.UserEntity;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String homePage(){
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/register")
    public String registerForm(Model model){
        model.addAttribute("user",new UserEntity());
        return "register";
    }

    @PostMapping("/register-user")
    public String register(Model model, UserEntity user){
        try{
            userService.registerUser(user);
            model.addAttribute("message","User added successfully!");
            return "login";
        }catch (IllegalArgumentException e){
            model.addAttribute("message",e.getMessage());
            return "register";
        }
    }

    @PostMapping("/login")
    public String login(Model model, String password, String email,HttpSession session){
        boolean auth = userService.loginUser(email,password,session);
        if(auth){
            return "sidan som man ska kunna se sina meddelande";
        }
        model.addAttribute("message","Wrong email or password!");
        return "login";
    }
}
