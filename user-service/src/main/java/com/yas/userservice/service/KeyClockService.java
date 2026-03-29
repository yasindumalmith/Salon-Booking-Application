package com.yas.userservice.service;

import com.yas.userservice.payload.response.dto.Credential;
import com.yas.userservice.payload.response.dto.SignupDTO;
import com.yas.userservice.payload.response.dto.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    public void createUser(SignupDTO signupDTO){

        String ACCESS_TOKEN="";

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

        HttpHeaders headers=new org.springframework.http.HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(ACCESS_TOKEN);

        HttpEntity<UserRequest> request=new HttpEntity<>(userRequest, headers);

        ResponseEntity<String> response=restTemplate.exchange(
                KEYCLOCK_ADMIN_API, HttpMethod.POST,request,String.class
        );

        if(response.getStatusCode()==HttpStatus.OK){
            System.out.println("User created successfully");
        }
    }
}
