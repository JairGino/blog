package com.example.blog.controller;

import com.example.blog.model.User;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    void start() {
        userRepository.deleteAll();

        userService.registerUser(new User(0L,
                "Root", "root@root.com", "rootroot", " "));
    }

    @Test
    @DisplayName("Cadastrar um usuário")
    public void shouldCreateAUser() {
        HttpEntity<User> requestBody = new HttpEntity<>(new User(0L, "Paulo Antunes", "paulo_antunes@email.com", "password", "-"));

        ResponseEntity<User> responseEntity = testRestTemplate
                .exchange("/users/signup", HttpMethod.POST, requestBody, User.class);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Não deve permitir duplicação do usuário")
    public void shouldNotDuplicateUser() {
        userService.registerUser(new User(0L,
                "Maria da Silva", "maria_silva@email.com", "password", "-"));

        HttpEntity<User> requestBody = new HttpEntity<>(new User(0L,
                "Maria da Silva", "maria_silva@email.com", "password", "-"));

        ResponseEntity<User> responseEntity = testRestTemplate
                .exchange("/users/signup", HttpMethod.POST, requestBody, User.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Atualizar um usuário")
    public void shouldUpdateUser() {
        Optional<User> userRegistered = userService.registerUser(new User(0L,
                "Juliana Andrews", "juliana_andrews@email.com", "password", "-"));

        User updatedUser = new User(userRegistered.get().getId(),
                "Juliana Andrews Ramos", "juliana_ramos@email.com", "password", "-");

        HttpEntity<User> resquestBody = new HttpEntity<>(updatedUser);

        ResponseEntity<User> responseEntity = testRestTemplate
                .withBasicAuth("root@root.com", "rootroot")
                .exchange("/users/update", HttpMethod.PUT, resquestBody, User.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Listar todos os usuários")
    public void shouldReturnAllUsers() {

        userService.registerUser(new User(0L,
                "Sabrina Sanches", "sabrina_sanches@email.com", "password", "-"));

        userService.registerUser(new User(0L,
                "Ricardo Marques", "ricardo_marques@email.com", "password", "-"));

        ResponseEntity<String> responseEntity = testRestTemplate
                .withBasicAuth("root@root.com", "rootroot")
                .exchange("/users/all", HttpMethod.GET, null, String.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

}
