package com.authentication.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.authentication.dto.TokenGenerateDTO;
import com.authentication.dto.TokenGenerateResponseDTO;

@FeignClient(name = "tokengenerator")
public interface TokenGeneratorProxy {
	
	@GetMapping("/tokengenerator/create-token")
	TokenGenerateResponseDTO createToken(@RequestBody TokenGenerateDTO tokenGenerateDTO);
    
	/*@PostMapping("/tokengenerator/delete-tokens")
    void deleteTokensByUserId(@RequestBody Long userId);*/
	
	@PostMapping("/tokengenerator/delete-token")
    void deleteTokenByValue(@RequestBody String tokenValue);
}