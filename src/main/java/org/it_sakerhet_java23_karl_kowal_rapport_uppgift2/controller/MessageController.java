package org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.dto.MessageDTO;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.service.MessageService;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.service.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")  // Alla endpoints i denna controller har "/api/messages" som prefix
public class MessageController {

    @Autowired
    private MessageService messageService;

    private JwtUtil jwtUtil;  // Används för att validera JWT och extrahera användarnamn

    // POST /api/messages - Spara ett nytt meddelande för användaren
    @PostMapping
    public ResponseEntity<?> createMessage(@RequestBody MessageDTO message, HttpServletRequest request) {
        // Hämta JWT från Authorization-headern och validera
        String jwt = extractJwtFromRequest(request);
        if (jwt == null || !jwtUtil.validateToken(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT saknas eller är ogiltig");
        }

        // Hämta användarnamn från JWT och spara meddelandet
        String username = jwtUtil.extractUsername(jwt);
        boolean result = messageService.saveMessage(message, username);

        if (result) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Meddelande sparat");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Kunde inte spara meddelandet");
        }
    }

    // GET /api/messages - Hämta alla meddelanden för användaren
    @GetMapping
    public ResponseEntity<List<MessageDTO>> getMessages(HttpServletRequest request) {
        // Hämta JWT från Authorization-headern och validera
        String jwt = extractJwtFromRequest(request);
        if (jwt == null || !jwtUtil.validateToken(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // Hämta användarnamn från JWT och hämta meddelanden för användaren
        String username = jwtUtil.extractUsername(jwt);
        List<MessageDTO> messages = messageService.getMessagesForUser(username);

        return ResponseEntity.ok(messages);
    }

    // DELETE /api/messages/{id} - Radera ett specifikt meddelande för användaren
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long id, HttpServletRequest request) {
        // Hämta JWT från Authorization-headern och validera
        String jwt = extractJwtFromRequest(request);
        if (jwt == null || !jwtUtil.validateToken(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT saknas eller är ogiltig");
        }

        // Hämta användarnamn från JWT och radera meddelandet
        String username = jwtUtil.extractUsername(jwt);
        boolean deleted = messageService.deleteMessage(Math.toIntExact(id), username);

        if (deleted) {
            return ResponseEntity.ok("Meddelande borttaget");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Kunde inte hitta eller ta bort meddelandet");
        }
    }

    // Hjälpmetod för att extrahera JWT från Authorization-headern
    private String extractJwtFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}