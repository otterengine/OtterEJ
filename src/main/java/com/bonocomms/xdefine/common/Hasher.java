package com.bonocomms.xdefine.common;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Hasher {

	public static String getPathHash(String message)
	{
	
		try {
			MessageDigest mda = MessageDigest.getInstance("SHA-512");
			byte [] b = mda.digest(message.getBytes());

			String result = "";
			for (int i=0; i < b.length; i++) result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
			
			return message.length() + "." + result;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
			
		}
		
	}

	public static String getMD5Checksum(InputStream inputStream)  throws Exception
	{
		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;

		do {
			numRead = inputStream.read(buffer);
			if (numRead > 0) complete.update(buffer, 0, numRead);
		} 
		while (numRead != -1);

		inputStream.close();
   
		byte[] b = complete.digest();
   
		String result = "";
		for (int i=0; i < b.length; i++) result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		
		return result;
	}


	public static String getSHA1Checksum(InputStream inputStream) throws Exception  {

		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("SHA-1");
		int numRead;

		do {
			numRead = inputStream.read(buffer);
			if (numRead > 0) complete.update(buffer, 0, numRead);
		} 
		while (numRead != -1);

		inputStream.close();
   
		byte[] b = complete.digest();
   
		String result = "";
		for (int i=0; i < b.length; i++) result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		
		return result;
	}
	
	public static String parseMD5(String message)
	{
		try {
			MessageDigest mda = MessageDigest.getInstance("MD5");
			byte [] b = mda.digest(message.getBytes());
	
			String result = "";
			for (int i=0; i < b.length; i++) result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
			
			return result;
		}
		catch(Exception ex) {
			return message;
		}
	}

	public static String parseMD5(byte[] message) throws NoSuchAlgorithmException
	{
		MessageDigest mda = MessageDigest.getInstance("MD5");
		byte [] b = mda.digest(message);

		String result = "";
		for (int i=0; i < b.length; i++) result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		
		return result;
	}

	public static String parseSHA(String message)
	{
		try
		{
			MessageDigest mda = MessageDigest.getInstance("SHA-1");
			byte [] b = mda.digest(message.getBytes());
	
			String result = "";
			for (int i=0; i < b.length; i++) result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
			
			return result;
		}
		catch(Exception ex) {
			return message;
		}
	}

	public static String parseSHA(byte[] message)
	{
		try
		{
			MessageDigest mda = MessageDigest.getInstance("SHA-1");
			byte [] b = mda.digest(message);
	
			String result = "";
			for (int i=0; i < b.length; i++) result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
			
			return result;
		}
		catch(Exception ex) {
			return "-";
		}
	}
	
	public static String parseMySQLPassword(String message)
	{
		return "*" + parseSHA(parseSHA(message));
	}

	public static String parseMySQLOldPassword(String message)
	{
		byte[] msg = message.getBytes();
		long nr = 1345345333L;
		long add = 7;
		long nr2 = 0x12345671L;
		
		for (int i = 0; i < msg.length; i++) {
			if (msg[i] == ' ' || msg[i] == '\t') continue;
			nr ^= (((nr & 63) + add) * msg[i]) + (nr << 8);
			nr2 += (nr2 << 8) ^ nr;
			add += msg[i];
		}
		
		nr = nr & 0x7FFFFFFFL;
		nr2 = nr2 & 0x7FFFFFFFL;
		
		StringBuilder sb = new StringBuilder(16);
		sb.append(Long.toString((nr & 0xF0000000) >> 28, 16));
		sb.append(Long.toString((nr & 0xF000000) >> 24, 16));
		sb.append(Long.toString((nr & 0xF00000) >> 20, 16));
		sb.append(Long.toString((nr & 0xF0000) >> 16, 16));
		sb.append(Long.toString((nr & 0xF000) >> 12, 16));
		sb.append(Long.toString((nr & 0xF00) >> 8, 16));
		sb.append(Long.toString((nr & 0xF0) >> 4, 16));
		sb.append(Long.toString((nr & 0x0F), 16));
		
		sb.append(Long.toString((nr2 & 0xF0000000) >> 28, 16));
		sb.append(Long.toString((nr2 & 0xF000000) >> 24, 16));
		sb.append(Long.toString((nr2 & 0xF00000) >> 20, 16));
		sb.append(Long.toString((nr2 & 0xF0000) >> 16, 16));
		sb.append(Long.toString((nr2 & 0xF000) >> 12, 16));
		sb.append(Long.toString((nr2 & 0xF00) >> 8, 16));
		sb.append(Long.toString((nr2 & 0xF0) >> 4, 16));
		sb.append(Long.toString((nr2 & 0x0F), 16));
		
		return sb.toString();
	}
	
	public static byte[] getHash(byte[] input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			return md.digest(input);
		} catch (NoSuchAlgorithmException e) {
			// 일어날 경우가 없다고 보지만 만약을 위해 Exception 발생
			throw new RuntimeException("SHA1" + " Algorithm Not Found", e);
		}
	}
	
	public static String password(byte[] input) {
		byte[] digest = null;

		// Stage 1
		digest = getHash(input);
		// Stage 2
		digest = getHash(digest);

		StringBuilder sb = new StringBuilder(1 + digest.length);
		sb.append("*");
		for (int i = 0; i < digest.length; i++) {
			String x = Integer.toHexString(digest[i] & 0xff).toUpperCase();
			if (x.length() < 2)
				sb.append("0");
			sb.append(x);
		}
		return sb.toString();
	}
	
	public static String parseMySQLNewPassword(String input) {
		if (input == null) {
			return null;
		}
		return password(input.getBytes());
	}
	
	public static String encodeAES128(String input, String password) throws Exception {
        byte[] plainText = input.toString().getBytes(StandardCharsets.UTF_16LE);
        PasswordDeriveBytes secretKey = new PasswordDeriveBytes(password, String.valueOf(password.length()).getBytes("ASCII"));
        
		SecretKey secureKey = new SecretKeySpec(secretKey.GetBytes(16), "AES");
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(secretKey.GetBytes(16)));

		return new String(Base64.encodeBase64(c.doFinal(plainText)));
	}
	
	public static String decodeAES128(String input, String password) throws Exception {
        byte[] plainText = Base64.decodeBase64(input);
        PasswordDeriveBytes secretKey = new PasswordDeriveBytes(password, String.valueOf(password.length()).getBytes("ASCII"));
        
		SecretKey secureKey = new SecretKeySpec(secretKey.GetBytes(16), "AES");
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(Cipher.DECRYPT_MODE, secureKey, new IvParameterSpec(secretKey.GetBytes(16)));

		return new String(c.doFinal(plainText), StandardCharsets.UTF_16LE);
	}
	
	
}
