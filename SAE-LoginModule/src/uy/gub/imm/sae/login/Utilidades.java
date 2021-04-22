package uy.gub.imm.sae.login;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.picketbox.commons.cipher.Base64;

public class Utilidades {

	//admin = ISMvKXpXpadDiUoOSoAfww==
	public static String encriptarPassword(String pwd) {
		if(pwd == null) {
			return null;
		}
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(pwd.getBytes());
			byte[] md5 = md.digest();
			String b64 = Base64.encodeBytes(md5);
			return b64;
		}catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
		System.out.println("admin -> ["+encriptarPassword("admin")+"]");
	}

}
