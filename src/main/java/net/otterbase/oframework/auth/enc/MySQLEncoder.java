package net.otterbase.oframework.auth.enc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;

import net.otterbase.oframework.common.Hasher;

public class MySQLEncoder implements PasswordEncoder {

	@Override
	public String encode(CharSequence rawPassword) {
		return Hasher.parseMySQLPassword(rawPassword.toString()).toUpperCase();
	}

	@Override
	public boolean matches(CharSequence arg1, String arg0) {
		
		List<String> passwords = new ArrayList<String>();
		passwords.add(Hasher.parseMySQLPassword(arg1.toString()));
		passwords.add(Hasher.parseMySQLPassword(arg1.toString()).toUpperCase());
		passwords.add(Hasher.parseMySQLOldPassword(arg1.toString()));
		passwords.add(Hasher.parseMySQLOldPassword(arg1.toString()).toUpperCase());
		passwords.add(Hasher.parseMySQLNewPassword(arg1.toString()));
		passwords.add(Hasher.parseMySQLNewPassword(arg1.toString()).toUpperCase());
		passwords.add(Hasher.parseMD5(arg1.toString()));
		passwords.add(Hasher.parseMD5(arg1.toString()).toUpperCase());
		passwords.add(arg1.toString());

		return passwords.contains(arg0);
		
	}
	
}
