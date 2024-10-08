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
    public String homePage(Model model){
        model.addAttribute("user" ,new UserEntity());
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
            return "redirect:/";
        }catch (IllegalArgumentException e){
            model.addAttribute("message",e.getMessage());
            return "register";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @PostMapping("/login-user")
    @ResponseBody
    public ResponseEntity<Map<String, String>> login(@RequestBody UserEntity user, HttpSession session) {
        // Logga inkommande email och lösenord för felsökning
        String email = user.getEmail();
        String password = user.getPassword();
        String jwt = userService.loginUser(email, password, session);

        if (jwt != null && !jwt.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Inloggning misslyckades! Kontrollera dina uppgifter.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}
