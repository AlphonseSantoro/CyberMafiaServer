import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class Security {
    private static final Random random = new SecureRandom();

    /**
     * Hash password with a salt that is generated for each user.
     * @param password
     * @param salt
     * @return
     */
    public String hashString(String password, String salt){
        String saltPlussPass = salt + password;
        String sha256hex = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(
                    saltPlussPass.getBytes(StandardCharsets.UTF_8)
            );
            sha256hex = bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException err) {
            err.getStackTrace();
        }
        return sha256hex;
    }

    /**
     * Convert bytes to hex
     * @param hash
     * @return Hex in type string
     */
    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Get a random 16 byte salt.
     * @return
     */
    private static byte[] getNextSalt(){
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }
}
