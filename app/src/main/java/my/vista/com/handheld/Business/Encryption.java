package my.vista.com.handheld.Business;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

//import android.util.Base64;

import java.math.BigInteger;

//import org.apache.commons.codec.binary.Base64;
public class Encryption 
{
	public static boolean VerifyHash(String strDecryptedPassword, String strEncryptedData, String strHashName)
	{
		String strEncryptedDataLoc = "";
		String strEncryptedPass="";
		 String strDecode = "";
        // Convert base64-encoded encrypted data into a byte array.
        byte[] btEncryptedDataWithSalt =  android.util.Base64.decode(strEncryptedData.getBytes(),android.util.Base64.DEFAULT);

        // We must know the length of the encrypted data (without salt).
        int nHashLenInBits =128;
        int nHashLenInBytes;
        if(strHashName == "MD5")
        {
        	nHashLenInBits = 128;
        }
        else if(strHashName == "SHA1")
        {
        	nHashLenInBits = 160;
        }
        else if(strHashName == "SHA256")
        {
        	nHashLenInBits = 256;
        }
        else if(strHashName == "SHA384")
        {
        	nHashLenInBits = 384;
        }
        else if(strHashName == "SHA512")
        {
        	nHashLenInBits = 512;
        }
        // Convert hash length from bits to bytes.
        nHashLenInBytes = nHashLenInBits / 8;

        // Make sure that the specified encrypted data is long enough.
        if (btEncryptedDataWithSalt.length < nHashLenInBytes)
            return false;

        // Allocate array to hold salt retrieved from encrypted data.
        byte[] btSalt = new byte[btEncryptedDataWithSalt.length - nHashLenInBytes];
        System.arraycopy(btEncryptedDataWithSalt, nHashLenInBytes, btSalt, 0, btSalt.length);
        String btTemp = bytesToHex(btSalt);
        String strPassword = AsciiToHex(strDecryptedPassword);//+btTemp;
        String strInput = EmbedSalt(btTemp,strPassword);        
        //Create MessageDigest object for MD5
        MessageDigest digest;
		try
		{
			digest = MessageDigest.getInstance(strHashName);
			digest.update(strInput.getBytes(), 0, strInput.length());
			//Converts message digest value in base 16 (hex) 
			strEncryptedDataLoc = new BigInteger(1, digest.digest()).toString(16);
			strEncryptedDataLoc = (strEncryptedDataLoc + btTemp).toUpperCase().trim();
			strDecode = (bytesToHex(btEncryptedDataWithSalt)).toUpperCase().trim();
      
		}
		catch (NoSuchAlgorithmException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        return (strDecode.equalsIgnoreCase(strEncryptedDataLoc));
	}
	
	 public static byte[] GenerateSalt()
     {
		 final int m_nMinSaltLen = 8;
		 final int m_nMaxSaltLen = 16;
         // Generate a random number for the salt length.
         Random random = new Random(System.currentTimeMillis());
        
         int nSaltLen = random.nextInt(m_nMaxSaltLen-m_nMinSaltLen) + m_nMinSaltLen;

         byte [] btSalt = new byte[nSaltLen];

         random.nextBytes(btSalt);
         return btSalt;
        
     }
	 public static String hexToASCII(String hex){        
         if(hex.length()%2 != 0){
            //System.err.println("requires EVEN number of chars");
            return null;
         }
         StringBuilder sb = new StringBuilder();                
         //Convert Hex 0232343536AB into two characters stream.
         for( int i=0; i < hex.length()-1; i+=2 ){
              /*
               * Grab the hex in pairs
               */
             String output = hex.substring(i, (i + 2));
             /*
              * Convert Hex to Decimal
              */
             int decimal = Integer.parseInt(output, 16);                  
             sb.append((char)decimal);              
         }            
         return sb.toString();
   } 
	 public static byte[] hexStringToByteArray(String s) {
		    int len = s.length();
		    byte[] data = new byte[len / 2];
		    for (int i = 0; i < len; i += 2) {
		        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
		                             + Character.digit(s.charAt(i+1), 16));
		    }
		    return data;
		}
	 public static String AsciiToHex(String ascii){
	        StringBuilder hex = new StringBuilder();
	        
	        for (int i=0; i < ascii.length(); i++) {
	            hex.append(Integer.toHexString(ascii.charAt(i)));
	        }       
	        return hex.toString();
	    } 
	 public static byte[] AscToHex(byte[] ascii){
	        
	        byte[] btAscHex;
	        String strSalt;
	        strSalt = bytesToHex(ascii);	        
	        btAscHex = strSalt.getBytes();
	        return btAscHex;
	    } 
	 public static String bytesToHex(byte[] bytes) {
		    final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		    char[] hexChars = new char[bytes.length * 2];
		    int v;
		    for ( int j = 0; j < bytes.length; j++ ) {
		        v = bytes[j] & 0xFF;
		        hexChars[j * 2] = hexArray[v >>> 4];
		        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		    }
		    return new String(hexChars);
		}
	 public static String EmbedSalt(String strSalt, String strData)
     {         
		 String strTemp = "";
		 int nDataIndex= Integer.parseInt(strData.substring(0, 1));         
         int nSaltIndex = 0;
         // Skip 1st data char.
         nDataIndex++;
         for(char chSalt:strSalt.toCharArray())
         {
        	 if (nDataIndex < strData.length())
             {        		 
        		 //strData = new StringBuffer().insert(nDataIndex, strSalt.substring(nSaltIndex, 1)).toString();
        		 strTemp = strData.substring(nDataIndex);
        		 strData = strData.substring(0, nDataIndex ) + strSalt.substring(nSaltIndex, nSaltIndex+1) + strTemp;
                 nDataIndex++;
                 nSaltIndex++;
                 nDataIndex += Integer.parseInt(strData.substring(nDataIndex, nDataIndex + 1));
                 nDataIndex++;
             }
        	 else
             {
                 strData += strSalt.substring(nSaltIndex, strSalt.length());
                 break;
             }
         }
         // Append salt length
         byte[] btSaltLen = new byte[2];
        
         
         btSaltLen[0] = Byte.parseByte(Integer.toString((strSalt.length() % 256)));
         btSaltLen[1] = Byte.parseByte(Integer.toString(strSalt.length() / 256));
         btSaltLen = AscToHex(btSaltLen);
         String temp = new String(btSaltLen);
         strData = strData +temp;
         return strData;
     }
	 public static boolean ComputeHash(String strDecryptedData, String strHashName, byte[] btSalt, String strEncryptedData)
     {
		 if (btSalt == null)
			 btSalt = GenerateSalt();
		 strDecryptedData = AsciiToHex(strDecryptedData);
		 btSalt = AscToHex(btSalt);
		 String strTemp = new String(btSalt);
		// Salt the decrypted data according to our recipe.
		 strDecryptedData = EmbedSalt(strTemp, strDecryptedData);
		 btSalt = hexStringToByteArray(strTemp);
		 return true;
     }
     
	
}
