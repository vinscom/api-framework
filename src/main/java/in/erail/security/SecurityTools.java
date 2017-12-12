package in.erail.security;

/**
 *
 * @author vinay
 */
import in.erail.glue.annotation.StartService;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.logging.log4j.Logger;

public class SecurityTools {

  private Logger mLog;
  private SecureRandom mRandom;
  private SecretKeySpec mKeySpec;

  @StartService
  public void startup() {
    try {
      setRandom(new SecureRandom());

      KeyGenerator keygen = KeyGenerator.getInstance("AES");
      keygen.init(128);
      setKeySpec(new SecretKeySpec(keygen.generateKey().getEncoded(), "AES"));
    } catch (NoSuchAlgorithmException ex) {
      getLog().error(ex);
    }
  }

  private byte[] concatenateByteArrays(byte[] a, byte[] b) {
    byte[] result = new byte[a.length + b.length];
    System.arraycopy(a, 0, result, 0, a.length);
    System.arraycopy(b, 0, result, a.length, b.length);
    return result;
  }

  public String encrypt(String value) {

    try {
      byte[] riv = new byte[16];
      mRandom.nextBytes(riv);
      IvParameterSpec iv = new IvParameterSpec(riv);

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
      cipher.init(Cipher.ENCRYPT_MODE, mKeySpec, iv);
      byte[] encrypted = cipher.doFinal(value.getBytes());

      return Base64.getEncoder().encodeToString(concatenateByteArrays(encrypted, riv));
    } catch (InvalidAlgorithmParameterException
            | InvalidKeyException
            | NoSuchAlgorithmException
            | NoSuchPaddingException
            | IllegalBlockSizeException
            | BadPaddingException ex) {
      getLog().error(ex);
    }

    return null;
  }

  public String decrypt(String encrypted) {

    try {
      byte[] data = Base64.getDecoder().decode(encrypted);
      IvParameterSpec iv = new IvParameterSpec(data, data.length - 16, 16);

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
      cipher.init(Cipher.DECRYPT_MODE, getKeySpec(), iv);

      byte[] original = cipher.doFinal(data, 0, data.length - 16);

      return new String(original);

    } catch (NoSuchAlgorithmException
            | NoSuchPaddingException
            | InvalidKeyException
            | InvalidAlgorithmParameterException
            | IllegalBlockSizeException
            | BadPaddingException ex) {
      getLog().error(ex);
    }

    return null;
  }

  public Logger getLog() {
    return mLog;
  }

  public void setLog(Logger pLog) {
    this.mLog = pLog;
  }

  public SecureRandom getRandom() {
    return mRandom;
  }

  public void setRandom(SecureRandom pRandom) {
    this.mRandom = pRandom;
  }

  public SecretKeySpec getKeySpec() {
    return mKeySpec;
  }

  public void setKeySpec(SecretKeySpec pKeySpec) {
    this.mKeySpec = pKeySpec;
  }

}
