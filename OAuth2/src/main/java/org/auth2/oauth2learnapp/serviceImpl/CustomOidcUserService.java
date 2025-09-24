package org.auth2.oauth2learnapp.serviceImpl;


/*
 * @author : rabin
 */

import lombok.extern.slf4j.Slf4j;
import org.auth2.oauth2learnapp.entity.User;
import org.auth2.oauth2learnapp.enums.Role;
import org.auth2.oauth2learnapp.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final OidcUserService oidcUserService;
    private final UserRepository userRepository;

    public CustomOidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.oidcUserService = new OidcUserService();   // default OIDC service
    }

    // This method is called after successful OAuth2 authentication to load user details
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("registration id: {}", registrationId);

        // load user details fron google (or other OIDC provider).
        // Delegate to the default OidcUserService to load user info from the OAuth2 provider
        OidcUser oidcUser = this.oidcUserService.loadUser(userRequest);

        final String name = oidcUser.getAttribute("name");
        final String email = oidcUser.getAttribute("email");

        log.info("name = {}, email = {}", name, email);  // Logs the user's name and email from the OAuth2 provider

        // // Check if the user exists in the database; if not, create a new user with default role USER
        User user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(User.builder()
                .name(name)
                .email(email)
                .role(Role.USER)
                .build()));

        log.info("user in DB: {}", user);   // display information in the terminal

        // create authorities based on user role from DB
        var authorities = Set.of(new SimpleGrantedAuthority(user.getRole().name()));

        // Return an OidcUser with the user's authorities, ID token, and user info
        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());

    }
}
