package hr.algebra.insurancebackend.service;

import hr.algebra.insurancebackend.security.domain.UserInfo;
import hr.algebra.insurancebackend.security.dto.SignUpDTO;

public interface RegisterUserService {
    UserInfo registerNewUserAccount(SignUpDTO userDto) throws IllegalArgumentException;
}
