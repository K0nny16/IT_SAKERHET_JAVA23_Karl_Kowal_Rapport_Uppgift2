package org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.controller;

import jakarta.servlet.http.HttpSession;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.entitys.UserEntity;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest,HttpSession session) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        String jwt = userService.loginUser(email, password,session);
        if (jwt != null && !jwt.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Inloggning misslyckades");
        }
    }
}
