package com.authentication.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.authentication.entity.User;
import com.authentication.exception.EmailAlreadyExistsException;
import com.authentication.repository.UserRepository;
import com.authentication.utils.PasswordUtils;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(String email, byte[] password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists.");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        return userRepository.save(user);
    }

    public Optional<User> validateUserCredentials(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> PasswordUtils.verifyPassword(password, user.getPassword())); // Verify password
    }

    public boolean isUserVerified(String email) {
        return userRepository.findByEmail(email)
                .map(User::getIsVerified)
                .orElse(false);
    }
    
    public boolean checkIfEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
    
    @Transactional
    public boolean verifyUser(String email) {
        // Check if the email exists in the database
        if (!userRepository.existsByEmail(email)) {
            return false; // Email does not exist
        }

        // Update the `is_verified` column
        userRepository.verifyUserByEmail(email);
        return true; // Verification successful
    }
    
    public Optional<Long> getUserIdByEmail(String email) {
        return userRepository.findIdByEmail(email); // Let the repository return Optional
    }
    
    @Transactional
    public void updatePassword(Long id, String newPassword) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User ID does not exist");
        }
        byte[] hashedPassword = PasswordUtils.hashPassword(newPassword);
        userRepository.updatePasswordById(id, hashedPassword);
    }

}