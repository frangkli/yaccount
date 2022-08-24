package application;

// Import necessary classes
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

// https://codezup.com/string-aes-encryption-decryption-in-java-with-example/

public class AESEncryption {
	// Function to generate key for encryption
	public static SecretKeySpec prepareSecretKey(String myKey) {
		try {
			byte[] key;
			key = myKey.getBytes(StandardCharsets.UTF_8);
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			return new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
    }

	// Function to encrypt raw String
	public static String encrypt(String rawString, String secret) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, prepareSecretKey(secret));
			return Base64.getEncoder().encodeToString(cipher.doFinal(rawString.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Function to decrypt hash String
	public static String decrypt(String hashString, String secret) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, prepareSecretKey(secret));
			return new String(cipher.doFinal(Base64.getDecoder().decode(hashString)));
		} catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}
}