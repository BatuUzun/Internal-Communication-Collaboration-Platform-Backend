package com.verification_code_manager.utils;

import java.util.Random;

public class CodeGenerator {
	public static String generateCode() {
		return String.format("%06d", new Random().nextInt(999999));
	}
}
