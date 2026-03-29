package com.yas.userservice.service;

import com.yas.userservice.payload.response.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeyClockService {
    private static final String KEYCLOCK_BASE_URL = "http://localhost:8001";
    private static final String KEYCLOCK_ADMIN_API = KEYCLOCK_BASE_URL + "/admin/realms/master/users";

    private static final String TOKEN_URL= KEYCLOCK_BASE_URL + "/realms/master/protocol/openid-connect/token";

    private static final String CLIENT_ID="salon-booking-client";
    private static final String CLIENT_SECRET="C2rZzgWv5cGqMldzpe7QbU5RNa3U9roi";
    private static final String GRANT_TYPE="password";
    private static final String scope ="openid email profile";
    private static final String username="yasindu";
    private static final String password="123456";
    private static final String clientId="8f6bc507-f835-4a7f-b0a7-4c2560f26557";

    private final RestTemplate restTemplate;

    public void createUser(SignupDTO signupDTO) throws Exception {

        String ACCESS_TOKEN=getAdminAccessToken(username,password,GRANT_TYPE,null).getAccessToken();

        Credential credential=new Credential();
        credential.setType("password");
        credential.setValue(signupDTO.getPassword());
        credential.setTemporary(false);

        UserRequest  userRequest=new UserRequest();
        userRequest.setUsername(signupDTO.getUsername());
        userRequest.setEmail(signupDTO.getEmail());
        userRequest.setFirstName(signupDTO.getFirstName());
        userRequest.setLastName(signupDTO.getLastName());
        userRequest.setEnabled(true);
        userRequest.setCredentials(List.of(credential));

        HttpHeaders headers=new org.springframework.http.HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(ACCESS_TOKEN);

        HttpEntity<UserRequest> request=new HttpEntity<>(userRequest, headers);

        ResponseEntity<String> response=restTemplate.exchange(
                KEYCLOCK_ADMIN_API, HttpMethod.POST,request,String.class
        );

        if(response.getStatusCode()==HttpStatus.CREATED){
            System.out.println("User created successfully");

            KeyClockUserDTO user=fetchFirstUserByUserName(signupDTO.getUsername(),ACCESS_TOKEN);

            KeyClockRole role=getRoleByName(clientId,ACCESS_TOKEN, signupDTO.getRole().toString());

            List<KeyClockRole> roles=new ArrayList<>();
            roles.add(role);

            assignRoleToUser(user.getId(),clientId,roles,ACCESS_TOKEN);
        }else{
            System.out.println("User creation failed");
            throw new Exception(response.getBody());
        }
    }

    public TokenResponse getAdminAccessToken(String username, String password, String grantType, String refreshToken){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", CLIENT_ID);
        body.add("client_secret", CLIENT_SECRET);
        body.add("grant_type", grantType);
        body.add("username", username);
        body.add("password", password);
        body.add("scope", scope);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<TokenResponse> response = restTemplate.exchange(
                TOKEN_URL,
                HttpMethod.POST,
                request,
                TokenResponse.class
        );

        return response.getBody();
    }

    public KeyClockRole getRoleByName(String clientId, String token, String role){
        String url = KEYCLOCK_BASE_URL +
                "/admin/realms/master/clients/" + clientId + "/roles/" + role;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<KeyClockRole> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                KeyClockRole.class
        );

        return response.getBody();
    }

    public KeyClockUserDTO fetchFirstUserByUserName(String username, String token){
        String url = KEYCLOCK_ADMIN_API + "?username=" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<KeyClockUserDTO[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                KeyClockUserDTO[].class
        );

        KeyClockUserDTO[] users = response.getBody();

        if (users != null && users.length > 0) {
            return users[0];
        }

        throw new RuntimeException("User not found in Keycloak");
    }
    public void assignRoleToUser(String userId, String clientId, List<KeyClockRole> roles, String token){
        String url = KEYCLOCK_BASE_URL +
                "/admin/realms/master/users/" + userId +
                "/role-mappings/clients/" + clientId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<List<KeyClockRole>> request = new HttpEntity<>(roles, headers);

        restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                Void.class
        );
    }
}
