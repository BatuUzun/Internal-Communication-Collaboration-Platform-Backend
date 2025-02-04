package com.authentication.controller;

import java.time.Duration;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.authentication.dto.CreateAccountDTO;
import com.authentication.dto.EmailVerificationRequest;
import com.authentication.dto.TokenGenerateDTO;
import com.authentication.dto.TokenGenerateResponseDTO;
import com.authentication.dto.UpdatePasswordRequest;
import com.authentication.dto.UserLoginDTO;
import com.authentication.dto.UserLoginResponseDTO;
import com.authentication.entity.User;
import com.authentication.proxy.TokenGeneratorProxy;
import com.authentication.service.UserService;
import com.authentication.utils.JwtTokenUtil;
import com.authentication.utils.PasswordUtils;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

@RestController
@RequestMapping("/authentication")
public class UserController {

	@Autowired
	private UserService userService;
 
	@Autowired
	private TokenGeneratorProxy tokenGeneratorProxy;

	@PostMapping("/create-user")
	public ResponseEntity<String> createUser(@RequestBody @Valid CreateAccountDTO createAccountDTO) {
		byte[] passwordHash = PasswordUtils.hashPassword(createAccountDTO.getPassword());
		try {
			userService.createUser(createAccountDTO.getEmail(), passwordHash);
			return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully.");
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists.");
		}
	}

	@PostMapping("/check-user-login")
	public ResponseEntity<?> checkUserLogin(@RequestBody @Valid UserLoginDTO userLoginDTO,
			HttpServletResponse response) {
		Optional<User> user = userService.validateUserCredentials(userLoginDTO.getEmail(), userLoginDTO.getPassword());

		if (user.isPresent()) {
			// Generate JWT Access Token
			String accessToken = JwtTokenUtil.generateToken(user.get().getId());

			// Set the Access Token as an HTTP-only cookie
			ResponseCookie accessCookie = ResponseCookie.from("authToken", accessToken).httpOnly(true).secure(false) 
					.path("/").maxAge(Duration.ofDays(1)) 					
					.sameSite("Strict").build();
			
			response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

			String refreshToken = null;

			if (userLoginDTO.isRememberMe()) {
				// Call the TokenGeneratorProxy to create a refresh token
				TokenGenerateDTO tokenGenerateDTO = new TokenGenerateDTO(user.get().getId(), "REFRESH_TOKEN");
				TokenGenerateResponseDTO tokenResponse = tokenGeneratorProxy.createToken(tokenGenerateDTO);

				refreshToken = tokenResponse.getToken(); // Get the Base64 encoded token from response

				// Set the Refresh Token as an HTTP-only cookie
				ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken).httpOnly(true)
						.secure(false) // Set true in production
						.path("/").maxAge(Duration.ofDays(7)) // Refresh token expiry
						.sameSite("Strict").build();
				response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
			}

			// Create the response DTO without the token
			UserLoginResponseDTO responseDTO = new UserLoginResponseDTO(user.get().getId(), user.get().getEmail(),
					user.get().getIsVerified());

			return ResponseEntity.ok(responseDTO);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password. Please try again."); 
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(
	        @CookieValue(name = "authToken", required = false) String authToken,
	        @CookieValue(name = "refreshToken", required = false) String refreshToken,
	        @CookieValue(name = "userId", required = false) String userId,
	        @CookieValue(name = "userEmail", required = false) String userEmail,
	        HttpServletResponse response) {

	    // Utility method to clear multiple cookies
	    clearCookies(response, "authToken", "refreshToken", "userId", "userEmail");

	    // Attempt to delete the refresh token from the database if it exists
	    if (refreshToken != null && !refreshToken.isEmpty()) {
	        try {
	            byte[] tokenBinary = Base64.getDecoder().decode(refreshToken); // Decode Base64
	            tokenGeneratorProxy.deleteTokenByValue(Base64.getEncoder().encodeToString(tokenBinary));
	        } catch (IllegalArgumentException e) {
	            // Handle invalid refresh token encoding gracefully
	            return ResponseEntity.badRequest().body("Invalid refresh token encoding.");
	        }
	    }

	    // Return success response regardless of token validation
	    return ResponseEntity.ok("Logged out successfully from this session.");
	}

	// Utility method to clear cookies
	private void clearCookies(HttpServletResponse response, String... cookieNames) {
	    for (String cookieName : cookieNames) {
	        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
	                .httpOnly(true)
	                .secure(false) // Set true in production
	                .path("/")
	                .maxAge(0) // Expire immediately
	                .sameSite("Strict")
	                .build();
	        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	    }
	}






	@GetMapping("/check-email")
	public ResponseEntity<Long> checkEmail(@RequestParam @Email(message = "Invalid email format") String email,
	                                       HttpServletResponse response) {
	    Optional<Long> userId = userService.getUserIdByEmail(email);

	    if (userId.isPresent()) {
	        // Generate JWT for the user
	        String accessToken = JwtTokenUtil.generateToken(userId.get());

	        // Create a secure, HTTP-only cookie with the JWT
	        ResponseCookie accessTokenCookie = ResponseCookie.from("authToken", accessToken)
	                .httpOnly(true)
	                .secure(false) // Set true in production
	                .path("/")
	                .maxAge(3600) // Token expiry in seconds (e.g., 1 hour)
	                .sameSite("Strict")
	                .build();

	        // Add the cookie to the response
	        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

	        // Return user ID with 200 OK
	        return ResponseEntity.ok(userId.get());
	    }

	    // Return -1 with 404 Not Found if email does not exist
	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(-1L);
	}


	@PostMapping("/verify-email")
	public ResponseEntity<Boolean> verifyUser(@RequestBody @Valid EmailVerificationRequest emailVerificationRequest) {
		Boolean verified = userService.verifyUser(emailVerificationRequest.getEmail());
		if (verified) {
			return ResponseEntity.ok(true);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
		}
	}

	@PostMapping("/update-password")
	public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest,
	                                             HttpServletResponse response) {
	    try {
	        // Update the password
	        userService.updatePassword(updatePasswordRequest.getId(), updatePasswordRequest.getNewPassword());

	        // Clear all cookies (e.g., authToken, refreshToken)
	        ResponseCookie authCookie = ResponseCookie.from("authToken", "")
	                .httpOnly(true)
	                .secure(false) // Set true in production
	                .path("/")
	                .maxAge(0) // Expire immediately
	                .sameSite("Strict")
	                .build();

	        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
	                .httpOnly(true)
	                .secure(false) // Set true in production
	                .path("/")
	                .maxAge(0) // Expire immediately
	                .sameSite("Strict")
	                .build();

	        // Add cookies to the response
	        response.addHeader(HttpHeaders.SET_COOKIE, authCookie.toString());
	        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

	        // Return success response
	        return ResponseEntity.ok("Password updated successfully.");
	    } catch (IllegalArgumentException e) {
	        // Handle validation errors
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    } catch (Exception e) {
	        // Handle unexpected errors
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update password.");
	    }
	}


}