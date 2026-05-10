package com.mailtrack.controller;

import com.mailtrack.model.AppUser;
import com.mailtrack.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(OAuth2AuthenticationToken auth) {
        if (auth == null) {
            return ResponseEntity.status(401).build();
        }
        String email  = auth.getPrincipal().getAttribute("email");
        AppUser user  = userService.getByEmail(email);
        Map<String, Object> response = new HashMap<>();
        response.put("id",      user.getId());
        response.put("email",   user.getEmail());
        response.put("name",    user.getName());
        response.put("picture", user.getPicture() != null ? user.getPicture() : "");
        response.put("avatar",  user.getAvatar() != null ? user.getAvatar() : "");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/signout")
    public ResponseEntity<Void> signout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }
}
