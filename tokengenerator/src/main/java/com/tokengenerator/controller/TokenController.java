package com.tokengenerator.controller;

import java.util.Base64;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tokengenerator.dto.RefreshTokenRequestDTO;
import com.tokengenerator.dto.RefreshTokenResponseDTO;
import com.tokengenerator.dto.TokenGenerateDTO;
import com.tokengenerator.dto.TokenGenerateResponseDTO;
import com.tokengenerator.entity.Token;
import com.tokengenerator.service.TokenService;
import com.tokengenerator.utils.JwtTokenUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tokengenerator")
public class TokenController {

	private final TokenService tokenService;

	public TokenController(TokenService tokenService) {
		this.tokenService = tokenService;
	}
  
	@PostMapping("/create-token")
	public ResponseEntity<TokenGenerateResponseDTO> createToken(@Valid @RequestBody TokenGenerateDTO tokenGenerateDTO) {
	    Token token = tokenService.createToken(tokenGenerateDTO.getUserId(), tokenGenerateDTO.getTokenType());

	    // Convert the binary token to a Base64 string for the response
	    String tokenAsString = Base64.getEncoder().encodeToString(token.getToken());

	    // Map to the response DTO
	    TokenGenerateResponseDTO responseDTO = new TokenGenerateResponseDTO(tokenAsString);

	    return ResponseEntity.ok(responseDTO);
	}
	
	
	@PostMapping("/delete-token")
	public ResponseEntity<?> deleteTokenByValue(@RequestBody String tokenValue) {
	    System.out.println("Received tokenValue: " + tokenValue); // Debugging
	    byte[] tokenBinary = Base64.getDecoder().decode(tokenValue); // Decode Base64 token
	    tokenService.deleteTokenByValue(tokenBinary);
	    return ResponseEntity.ok("Token deleted successfully.");
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<RefreshTokenResponseDTO> refreshAccessToken(@RequestBody RefreshTokenRequestDTO request) {
	    try {
	        // Decode and validate refresh token
	        byte[] refreshTokenBinary = Base64.getDecoder().decode(request.getRefreshToken());
	        if (!tokenService.isValidRefreshToken(refreshTokenBinary)) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	        }

	        // Extract user ID and generate new access token
	        Long userId = tokenService.extractUserIdFromToken(refreshTokenBinary);
	        if (userId == null) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	        }

	        String newAccessToken = JwtTokenUtil.generateToken(userId);

	        // Create response DTO with new access token
	        RefreshTokenResponseDTO responseDTO = new RefreshTokenResponseDTO(newAccessToken, null);
	        return ResponseEntity.ok(responseDTO);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}








}