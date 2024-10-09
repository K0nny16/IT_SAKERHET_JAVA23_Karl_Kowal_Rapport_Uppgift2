package org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.dto.MessageDTO;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.dto.UserDTO;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.security.KeyStoreUtil;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.service.JwtUtil;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MessageController {

    private final MessageService messageService;
    private final KeyStoreUtil keyStoreUtil;

    @Autowired
    public MessageController(MessageService messageService,KeyStoreUtil keyStoreUtil){
        this.keyStoreUtil = keyStoreUtil;
        this.messageService = messageService;
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<?> sendMessage(@RequestBody MessageDTO message, HttpServletRequest request, HttpSession session){
        // Hämta användarens DTO från sessionen
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ingen användare i sessionen!");
        }
        // Hämta och dekryptera användarens nyckel
        String encryptedKey = userDTO.getEncryptedKey();
        String decryptedKey = keyStoreUtil.decryptUserSecretKey(encryptedKey);
        // Skapa en dynamisk JwtUtil med användarens dekrypterade nyckel
        JwtUtil jwtUtil = new JwtUtil(decryptedKey);
        // Hämta JWT-token från Authorization-headern
        String jwtToken = extractJwtFromRequest(request);
        // Validera JWT-token
        if (jwtUtil.validateToken(jwtToken)) {
            // Spara meddelandet
            String username = jwtUtil.extractUsername(jwtToken);
            boolean saved = messageService.saveMessage(message, username);
            if (saved) {
                return ResponseEntity.ok("Meddelande sparat!");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Kunde inte spara meddelandet!");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token är ogiltig!");
        }
    }

    @GetMapping("/getMessages")
    public ResponseEntity<?> getMessages(@RequestParam("token") String token, HttpSession session){
        // Steg 1: Hämta användarinformation från sessionen
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null) {
            System.out.println("DTO är null!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ingen användare i sessionen.");
        }
        // Steg 2: Dekryptera användarens hemliga nyckel
        String encryptedKey = userDTO.getEncryptedKey();
        String decryptedKey = keyStoreUtil.decryptUserSecretKey(encryptedKey);
        // Steg 3: Skapa en dynamisk JwtUtil med användarens dekrypterade nyckel
        JwtUtil jwtUtil = new JwtUtil(decryptedKey);
        // Steg 4: Validera token
        if (jwtUtil.validateToken(token)) {
            String username = jwtUtil.extractUsername(token);
            List<MessageDTO> messages = messageService.getMessagesForUser(username);
            return ResponseEntity.ok(messages);
        } else {
            System.out.println("Kan inte validera token!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT saknas eller är ogiltig");
        }
    }
    @GetMapping("/messagesPage")
    public String messagesPage(HttpSession session,@RequestParam("token")String token){
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if(userDTO == null){
            System.out.println("DTO är null");
            return "/login";
        }
        String encryptedKey = userDTO.getEncryptedKey();
        String decryptedKey = keyStoreUtil.decryptUserSecretKey(encryptedKey);
        JwtUtil jwtUtil = new JwtUtil(decryptedKey);
        if(jwtUtil.validateToken(token)){
            return "messages";
        }else {
            System.out.println("Kan inte validera token!");
            return "redirect:/";
        }
    }

    @DeleteMapping("/deleteMessage/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long id, @RequestParam("token") String token, HttpSession session) {
        // Steg 1: Hämta användarinformation från sessionen
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null) {
            System.out.println("DTO är null!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ingen användare i sessionen.");
        }
        // Steg 2: Dekryptera användarens hemliga nyckel
        String encryptedKey = userDTO.getEncryptedKey();
        String decryptedKey = keyStoreUtil.decryptUserSecretKey(encryptedKey);
        // Steg 3: Skapa en dynamisk JwtUtil med användarens dekrypterade nyckel
        JwtUtil jwtUtil = new JwtUtil(decryptedKey);
        // Steg 4: Validera token
        if (jwtUtil.validateToken(token)) {
            String username = jwtUtil.extractUsername(token);
            boolean deleted = messageService.deleteMessage(Math.toIntExact(id), username);
            return deleted ? ResponseEntity.ok("Meddelande borttaget")
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Kunde inte hitta eller ta bort meddelandet");
        } else {
            System.out.println("Kan inte validera token!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT saknas eller är ogiltig");
        }
    }

    @GetMapping("/writeMessage")
    public String writeMessagePage(@RequestParam("token") String token, HttpSession session) {
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null) {
            System.out.println("DTO är null!");
            return "redirect:/login";
        }

        String encryptedKey = userDTO.getEncryptedKey();
        String decryptedKey = keyStoreUtil.decryptUserSecretKey(encryptedKey);

        // Skapa en dynamisk JwtUtil med användarens dekrypterade nyckel
        JwtUtil jwtUtil = new JwtUtil(decryptedKey);

        if (jwtUtil.validateToken(token)) {
            return "writeMessages";
        } else {
            System.out.println("Kan inte validera token!");
            return "redirect:/";
        }
    }
    private String extractJwtFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}