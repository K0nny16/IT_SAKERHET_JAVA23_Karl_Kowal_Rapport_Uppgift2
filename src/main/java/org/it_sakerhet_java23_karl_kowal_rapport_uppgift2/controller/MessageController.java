package org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.dto.MessageDTO;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.service.MessageService;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.service.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> createMessage(@RequestBody MessageDTO message, HttpServletRequest request) throws Exception {
        String jwt = extractJwtFromRequest(request);
        if (jwt == null || !jwtUtil.validateToken(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT saknas eller är ogiltig");
        }
        String username = jwtUtil.extractUsername(jwt);
        boolean result = messageService.saveMessage(message, username);
        if (result) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Meddelande sparat");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Kunde inte spara meddelandet");
        }
    }

    @GetMapping
    public ResponseEntity<List<MessageDTO>> getMessages(HttpServletRequest request) throws Exception {
        String jwt = extractJwtFromRequest(request);
        if (jwt == null || !jwtUtil.validateToken(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        String username = jwtUtil.extractUsername(jwt);
        List<MessageDTO> messages = messageService.getMessagesForUser(username);
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long id, HttpServletRequest request) {
        String jwt = extractJwtFromRequest(request);
        if (jwt == null || !jwtUtil.validateToken(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT saknas eller är ogiltig");
        }
        String username = jwtUtil.extractUsername(jwt);
        boolean deleted = messageService.deleteMessage(Math.toIntExact(id), username);
        if (deleted) {
            return ResponseEntity.ok("Meddelande borttaget");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Kunde inte hitta eller ta bort meddelandet");
        }
    }
    @GetMapping("/write")
    public String writeMessagePage(@RequestParam("token")String token){
        if(jwtUtil.validateToken(token)){
            return "write-message.html";
        }else {
            return "redirect:/login";
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