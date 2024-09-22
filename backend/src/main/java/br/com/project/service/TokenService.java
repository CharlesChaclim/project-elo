package br.com.project.service;

import br.com.project.dto.LoginRequestDTO;
import br.com.project.dto.LoginResponseDTO;
import br.com.project.entity.Role;
import br.com.project.entity.User;
import br.com.project.util.MessageUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Optional<User> user = userService.findByUsername(loginRequestDTO.username());

        if (user.isEmpty() || !bCryptPasswordEncoder.matches(loginRequestDTO.password(), user.get().getPassword())) {
            throw new BadCredentialsException(MessageUtil.get("usuario.senha.invalida"));
        }

        var scopes = user.get().getRoles()
                .stream()
                .map(Role::getNome)
                .collect(Collectors.joining(" "));


        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.get().getUserId().toString())
                .claim("scope", scopes)
                .build();
        return new LoginResponseDTO(jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue());
    }
}
