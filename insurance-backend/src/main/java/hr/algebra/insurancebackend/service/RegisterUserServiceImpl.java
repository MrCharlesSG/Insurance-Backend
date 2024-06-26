package hr.algebra.insurancebackend.service;

import hr.algebra.insurancebackend.security.domain.UserInfo;
import hr.algebra.insurancebackend.security.domain.UserRole;
import hr.algebra.insurancebackend.security.dto.SignUpDTO;
import hr.algebra.insurancebackend.security.repository.RolesRepository;
import hr.algebra.insurancebackend.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@Transactional
public class RegisterUserServiceImpl implements RegisterUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserInfo registerNewUserAccount(SignUpDTO userDto) throws IllegalArgumentException {
        if (usernameExists(userDto.getUsername())) {
            throw new IllegalArgumentException("There is an account with the username address: " + userDto.getUsername());
        }

        // Must determine the role of the user in future.
        Optional<UserRole> roleUser = rolesRepository.findByName("VEHICLE");
        if (roleUser.isPresent()) {
            UserInfo userInfo = UserInfo.builder()
                    //.email(userDto.getEmail())
                    .roles(Collections.singleton(roleUser.get()))
                    .password(passwordEncoder.encode(userDto.getPassword()))
                    .username(userDto.getUsername())
                    .build();
            return userRepository.save(userInfo);
        }
        throw new IllegalArgumentException("Role completely incorrect");
    }

    private boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
