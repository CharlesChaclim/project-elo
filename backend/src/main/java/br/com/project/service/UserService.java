package br.com.project.service;

import br.com.project.entity.User;
import br.com.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public Optional<User> findByUsername(String usuario) {
        return repository.findByUsername(usuario);
    }
}
