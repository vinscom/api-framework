package in.erail.model;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vinay
 */
public class ResponseEvent {

  private Map<String, String>[] mCookies;
  private boolean mIsBase64Encoded = true;
  private int mStatusCode = 200;
  private Map<String, String> mHeaders;
  private Map<String, String[]> mMultiValueHeaders;
  private byte[] mBody = new byte[0];

  public boolean isIsBase64Encoded() {
    return mIsBase64Encoded;
  }

  public void setIsBase64Encoded(boolean pIsBase64Encoded) {
    this.mIsBase64Encoded = pIsBase64Encoded;
  }

  public int getStatusCode() {
    return mStatusCode;
  }

  public void setStatusCode(int pStatusCode) {
    this.mStatusCode = pStatusCode;
  }

  public Map<String, String> getHeaders() {
    if (mHeaders == null) {
      mHeaders = new HashMap<>();
    }
    return mHeaders;
  }

  public void setHeaders(Map<String, String> pHeaders) {
    this.mHeaders = pHeaders;
  }

  public byte[] getBody() {
    return mBody;
  }

  public void setBody(byte[] pBody) {
    this.mBody = pBody;
  }

  public Map<String, String[]> getMultiValueHeaders() {
    if (mMultiValueHeaders == null) {
      mMultiValueHeaders = new HashMap<>();
    }
    return mMultiValueHeaders;
  }

  public void setMultiValueHeaders(Map<String, String[]> pMultiValueHeaders) {
    this.mMultiValueHeaders = pMultiValueHeaders;
  }

  public Map<String, String>[] getCookies() {
    return mCookies;
  }

  public void setCookies(Map<String, String>[] pCookies) {
    this.mCookies = pCookies;
  }

  public void addHeader(String pHeaderName, String pMediaType) {
    getHeaders().put(HttpHeaders.CONTENT_TYPE, pMediaType);
  }

  public void addHeader(String pHeaderName, MediaType pMediaType) {
    getHeaders().put(HttpHeaders.CONTENT_TYPE, pMediaType.toString());
  }
}
