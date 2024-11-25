package my.vista.com.handheld.Business;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

// TODO: Auto-generated Javadoc
/**
 * The Class Cryptor.
 */
public class Cryptor
{
	/**
	 * name of the character set to use for converting between characters and
	 * bytes.
	 */
	private static final String CHARSET_NAME = "UTF-8";

	/** random number generator algorithm. */
	// private static final String RNG_ALGORITHM = "SHA1PRNG";

	/**
	 * message digest algorithm (must be sufficiently long to provide the key
	 * and initialization vector)
	 */
	private static final String DIGEST_ALGORITHM = "SHA-1";

	/** key algorithm (must be compatible with CIPHER_ALGORITHM). */
	private static final String KEY_ALGORITHM = "AES";

	/** cipher algorithm (must be compatible with KEY_ALGORITHM). */
	private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

	/**
	 * Check sum.
	 * 
	 * @param Document
	 *            the document
	 * @return the string
	 */
	public static String CheckSum(String Document)
	{
		String result = "";
		try
		{

			// XmlUtility.WriteResponseLogOnSD("CheckSum_", Document);

			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(Document.getBytes("ASCII"), 0, Document.length());
			String signature = new BigInteger(1, md5.digest()).toString(16);
			// XmlUtility.WriteResponseLogOnSD("CheckSum_Result", signature);

			while (signature.length() < 32)
			{
				signature = signature.replace(signature, "0" + signature);
			}

			result = signature;
		} catch (NoSuchAlgorithmException e)
		{
			// TODO Auto-generated catch block

			CacheManager.ErrorLog(e);
			result = e.toString();
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			CacheManager.ErrorLog(e);
			result = e.toString();
		}

		return result;
	}

	/**
	 * Decrypt the specified ciphertext using the given password. With the
	 * correct salt, number of iterations, and password, this method reverses
	 * the effect of the encrypt() method. This method uses the user-specified
	 * salt, number of iterations, and password to recreate the 16-byte secret
	 * key and 16-byte initialization vector. The secret key and initialization
	 * vector are then used in the AES-128 cipher to decrypt the given
	 * ciphertext.
	 * 
	 * @param salt
	 *            salt to be used in decryption
	 * @param iterations
	 *            number of iterations to use in salting
	 * @param iv
	 *            the iv
	 * @param password
	 *            password to be used for decryption
	 * @param ciphertext
	 *            ciphertext to be decrypted
	 * @return cleartext
	 * @throws Exception
	 *             on any error encountered in decryption
	 */
	public static byte[] decrypt(final byte[] salt, final int iterations, final byte[] iv, final String password, final byte[] ciphertext) throws Exception
	{
		/* compute key and initialization vector */
		final MessageDigest shaDigest = MessageDigest.getInstance(DIGEST_ALGORITHM);
		byte[] pw = password.getBytes(CHARSET_NAME);

		if (salt != null)
		{
			byte[] buffer2 = new byte[pw.length + salt.length];
			System.arraycopy(pw, 0, buffer2, 0, pw.length);
			System.arraycopy(salt, 0, buffer2, pw.length, salt.length);
			pw = buffer2;
		}

		int destinationIndex = 0;
		byte[] destinationArray = new byte[32];
		while (destinationIndex < 32)
		{
			for (int i = 0; i < iterations; i++)
			{
				pw = shaDigest.digest(pw);
			}
			int length = 32 - destinationIndex;
			length = (length < pw.length) ? length : pw.length;
			System.arraycopy(pw, 0, destinationArray, destinationIndex, length);
			destinationIndex += length;
		}

		/*
		 * extract the 16-byte key and initialization vector from the SHA-256
		 * digest
		 */
		final byte[] key = new byte[32];
		System.arraycopy(destinationArray, 0, key, 0, 32);
		Arrays.fill(pw, (byte) 0x00);

		/* perform AES-128 decryption */
		final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");

		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, KEY_ALGORITHM), new IvParameterSpec(iv));

		Arrays.fill(key, (byte) 0x00);
		Arrays.fill(iv, (byte) 0x00);

		return cipher.doFinal(ciphertext);
	}

	/**
	 * Dencrypt.
	 * 
	 * @param value
	 *            the value
	 * @return the string
	 */
	public static String Dencrypt(String value)
	{

		String result = "";

		String salt = "s@1tValue";
		// marterkey = "9C8D313F-80B5-49C8-950C-957BF8FFDB7C";

		

		return result;
	}

	/**
	 * Dencrypt.
	 * 
	 * @param value
	 *            the value
	 * @param marterkey
	 *            the marterkey
	 * @return the string
	 */
	public static String Dencrypt(String value, String marterkey)
	{

		String result = "";

		String salt = "s@1tValue";
		// marterkey = "9C8D313F-80B5-49C8-950C-957BF8FFDB7C";
		

		return result;
	}

	/**
	 * Encrypt.
	 * 
	 * @param salt
	 *            the salt
	 * @param iterations
	 *            the iterations
	 * @param iv
	 *            the iv
	 * @param password
	 *            the password
	 * @param cleartext
	 *            the cleartext
	 * @return the byte[]
	 * @throws Exception
	 *             the exception
	 */
	public static byte[] encrypt(final byte[] salt, final int iterations, final byte[] iv, final String password, final byte[] cleartext) throws Exception
	{
		/* generate salt randomly */
		// SecureRandom.getInstance(RNG_ALGORITHM).nextBytes(salt);

		/* compute key and initialization vector */
		final MessageDigest shaDigest = MessageDigest.getInstance(DIGEST_ALGORITHM);
		byte[] pw = password.getBytes(CHARSET_NAME);

		if (salt != null)
		{
			byte[] buffer2 = new byte[pw.length + salt.length];
			System.arraycopy(pw, 0, buffer2, 0, pw.length);
			System.arraycopy(salt, 0, buffer2, pw.length, salt.length);
			pw = buffer2;
		}

		int destinationIndex = 0;
		byte[] destinationArray = new byte[32];
		while (destinationIndex < 32)
		{
			for (int i = 0; i < iterations; i++)
			{
				pw = shaDigest.digest(pw);
			}

			int length = 32 - destinationIndex;
			length = (length < pw.length) ? length : pw.length;
			System.arraycopy(pw, 0, destinationArray, destinationIndex, length);
			destinationIndex += length;
		}

		/*
		 * extract the 16-byte key and initialization vector from the SHA-256
		 * digest
		 */
		final byte[] key = new byte[32];
		System.arraycopy(destinationArray, 0, key, 0, 32);
		Arrays.fill(pw, (byte) 0x00);

		/* perform AES-128 encryption */
		final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, KEY_ALGORITHM), new IvParameterSpec(iv));

		Arrays.fill(key, (byte) 0x00);
		Arrays.fill(iv, (byte) 0x00);

		return cipher.doFinal(cleartext);
	}

	/**
	 * Encrypt.
	 * 
	 * @param value
	 *            the value
	 * @return the string
	 */
	public static String Encrypt(String value)
	{

		String result = "";

		String salt = "s@1tValue";

		

		return result;
	}

	/**
	 * Encrypt the specified cleartext using the given password. With the
	 * correct salt, number of iterations, and password, the decrypt() method
	 * reverses the effect of this method. This method generates and uses a
	 * random salt, and the user-specified number of iterations and password to
	 * create a 16-byte secret key and 16-byte initialization vector. The secret
	 * key and initialization vector are then used in the AES-128 cipher to
	 * encrypt the given cleartext.
	 * 
	 * @param value
	 *            the value
	 * @param marterkey
	 *            the marterkey
	 * @return ciphertext
	 */

	public static String Encrypt(String value, String masterKey)
	{
		String result = "";

		String salt = "s@1tValue";
		
		try
		{
			result = Base64.encodeToString(encrypt(salt.getBytes("ASCII"), 2, "@1B2c3D4e5F6g7H8".getBytes("ASCII"), masterKey, value.getBytes("UTF-8")), Base64.DEFAULT);
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			CacheManager.ErrorLog(e);
			result = e.toString();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			CacheManager.ErrorLog(e);
			result = e.toString();
		}

		return result;
	}

	/**
	 * Private constructor that should never be called.
	 */

	private Cryptor()
	{
	}
}
