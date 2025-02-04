package com.verification_code_manager.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.verification_code_manager.entity.User;
import com.verification_code_manager.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	public String getEmailByUserId(Long userId) {
        return userRepository.findEmailById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
    }
	
	public void updateUserVerificationStatus(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setIsVerified(true);
            userRepository.save(user); // Update the user's verification status
        }
    }
	
	public boolean isUserVerified(Long userId) {
	    return userRepository.findById(userId)
	        .map(User::getIsVerified)
	        .orElseThrow(() -> new IllegalArgumentException("User not found for the given ID."));
	}

}
