package com.verification_code_manager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "verification.expiration")
public class VerificationExpirationConfig {
    private int change;
    private int reset;
    private int verify;
	public int getChange() {
		return change;
	}
	public void setChange(int change) {
		this.change = change;
	}
	public int getReset() {
		return reset;
	}
	public void setReset(int reset) {
		this.reset = reset;
	}
	public int getVerify() {
		return verify;
	}
	public void setVerify(int verify) {
		this.verify = verify;
	}

    
    
}
