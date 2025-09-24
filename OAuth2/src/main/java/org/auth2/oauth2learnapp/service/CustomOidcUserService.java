package org.auth2.oauth2learnapp.service;


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
        this.oidcUserService = new OidcUserService();
    }

    // this is used to return name and email from authentication
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("registration id: {}", registrationId);

        OidcUser oidcUser = this.oidcUserService.loadUser(userRequest);

        final String name = oidcUser.getAttribute("name");
        final String email = oidcUser.getAttribute("email");

        log.info("name = {}, email = {}", name, email);

        // to find email from user  repository interface OR implements interface methods
        // checks whether there is email or not
        User user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(User.builder()
                .name(name)
                .email(email)
                .role(Role.USER)
                .build()));

        log.info("user in DB: {}", user);   // display information in the terminal

        var authorities = Set.of(new SimpleGrantedAuthority(user.getRole().name()));

        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());

    }
}
