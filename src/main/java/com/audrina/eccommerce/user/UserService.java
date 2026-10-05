package com.audrina.eccommerce.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public UserResponse saveUser(UserRequest userRequest) {
        User saveUser = userMapper.toEntity(userRequest);
        User response = userRepository.save(saveUser);
        return userMapper.toResponse(response);

    }
}
