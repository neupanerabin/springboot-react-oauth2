package org.auth2.oauth2learnapp.controller;

/*
 * @author : rabin
 */

import org.auth2.oauth2learnapp.entity.User;
import org.auth2.oauth2learnapp.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
public class AuthController {

    private final UserRepository userRepo;

    public AuthController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("api/user")
    public ResponseEntity<Map<String, Object>> getUserInfo(OAuth2AuthenticationToken auth) {
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        String email = auth.getPrincipal().getAttribute("email");
        String picture = auth.getPrincipal().getAttribute("picture");
        if (picture == null) {
            picture = "default.png";
        }

        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Map<String, Object> map = Map.of("email", user.getEmail(), "picture", picture, "name", user.getName(), "role", user.getRole());
        return ResponseEntity.ok(map);
    }

    //
    @PreAuthorize(value = "hasAuthority('ADMIN')")  // check whether authority or not
    @GetMapping("api/info")
    public ResponseEntity<Map<String, String>> getInfo() {
        var map = Map.of("1", "ADMIN info");
        return ResponseEntity.ok(map);
    }
}
