package org.auth2.oauth2learnapp;


/*
 * @author : rabin
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final OidcUserService oidcUserService = new OidcUserService();


    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("registration id: {}", registrationId);

        OidcUser oidcUser = this.oidcUserService.loadUser(userRequest);

        String name = oidcUser.getAttribute("name");
        String email = oidcUser.getAttribute("email");

        log.info("name = {}, email = {}", name, email);

        return oidcUser;

    }
}
