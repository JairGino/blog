package com.example.blog.service;

import com.example.blog.model.User;
import com.example.blog.model.UserLogin;
import com.example.blog.repository.UserRepository;
import com.example.blog.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public Optional<User> registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent())
            return Optional.empty();

        user.setPassword(encryptPassword(user.getPassword()));
        return Optional.of(userRepository.save(user));
    }

    public Optional<User> updateUser(User user) {
        if (userRepository.findById(user.getId()).isPresent()) {
            Optional<User> foundUser = userRepository.findByEmail(user.getEmail());

            if ( (foundUser.isPresent()) && (foundUser.get().getId() != user.getId()) )
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe!", null);

            user.setPassword(encryptPassword(user.getPassword()));
            return Optional.ofNullable(userRepository.save(user));
        }

        return Optional.empty();
    }

    public Optional<UserLogin> authenticateUser(Optional<UserLogin> userLogin) {

        // Gera o Objeto de autenticação
        var credentials = new UsernamePasswordAuthenticationToken(userLogin.get().getEmail(), userLogin.get().getPassword());

        // Autentica o Usuario
        Authentication authentication = authenticationManager.authenticate(credentials);

        // Se a autenticação foi efetuada com sucesso
        if (authentication.isAuthenticated()) {

            // Busca os dados do usuário
            Optional<User> foundUser = userRepository.findByEmail(userLogin.get().getEmail());

            // Se o usuário foi encontrado
            if (foundUser.isPresent()) {

                // Preenche o Objeto usuarioLogin com os dados encontrados
                userLogin.get().setId(foundUser.get().getId());
                userLogin.get().setName(foundUser.get().getName());
                userLogin.get().setUrlPhoto(foundUser.get().getPhotoUrl());
                userLogin.get().setToken(generateToken(userLogin.get().getEmail()));
                userLogin.get().setPassword("");

                // Retorna o Objeto preenchido
                return userLogin;
            }

        }
        return Optional.empty();
    }

    private String encryptPassword(String password) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    private String generateToken(String user) {
        return "Bearer " + jwtService.generateToken(user);
    }

}
