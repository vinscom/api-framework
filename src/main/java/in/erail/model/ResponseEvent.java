package in.erail.model;

import com.google.common.base.Preconditions;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import io.vertx.reactivex.core.MultiMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author vinay
 */
public class ResponseEvent {

  private Map<String, String>[] mCookies;
  private boolean mIsBase64Encoded = true;
  private int mStatusCode = 200;
  private MultiMap mMultiValueHeaders;
  private byte[] mBody = new byte[0];

  public ResponseEvent() {
    mMultiValueHeaders = MultiMap.caseInsensitiveMultiMap();
  }

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

  public byte[] getBody() {
    return mBody;
  }

  public void setBody(byte[] pBody) {
    this.mBody = pBody;
  }

  public Map<String, String>[] getCookies() {
    return mCookies;
  }

  public void setCookies(Map<String, String>[] pCookies) {
    this.mCookies = pCookies;
  }

  public void setMultiValueHeaders(Map<String, String[]> pValue) {
    Preconditions.checkNotNull(pValue);

    if (pValue.isEmpty()) {
      return;
    }

    mMultiValueHeaders
            = pValue
                    .entrySet()
                    .stream()
                    .reduce(MultiMap.caseInsensitiveMultiMap(), (a, v) -> {
                      Optional
                              .ofNullable(v.getValue())
                              .map(t -> Arrays.stream(t))
                              .orElse(Arrays.stream(new String[0]))
                              .forEach((t) -> a.add(v.getKey(), t));
                      return a;
                    }, (a, b) -> {
                      a.addAll(b);
                      return a;
                    });
  }

  /**
   * Return copy of headers map
   * @return 
   */
  public Map<String, String[]> getMultiValueHeaders() {

    Map<String, String[]> result
            = mMultiValueHeaders
                    .names()
                    .stream()
                    .reduce(new HashMap<>(), (a, v) -> {
                      a.put(v, mMultiValueHeaders.getAll(v).toArray(new String[0]));
                      return a;
                    }, (a, b) -> {
                      a.putAll(b);
                      return a;
                    });

    return Collections.unmodifiableMap(result);
  }

  public void setHeaders(Map<String, String> pValue) {
    Preconditions.checkNotNull(pValue);

    if (pValue.isEmpty()) {
      return;
    }

    mMultiValueHeaders
            = pValue
                    .entrySet()
                    .stream()
                    .reduce(MultiMap.caseInsensitiveMultiMap(), (a, v) -> {
                      Optional
                              .ofNullable(v.getValue())
                              .ifPresent(t -> a.add(v.getKey(), t));
                      return a;
                    }, (a, b) -> {
                      a.addAll(b);
                      return a;
                    });
  }

  /**
   * Return copy of Header Map
   * @return 
   */
  public Map<String, String> getHeaders() {

    Map<String, String> result
            = mMultiValueHeaders
                    .names()
                    .stream()
                    .reduce(new HashMap<>(), (a, v) -> {
                      a.put(v, mMultiValueHeaders.get(v));
                      return a;
                    }, (a, b) -> {
                      a.putAll(b);
                      return a;
                    });

    return Collections.unmodifiableMap(result);
  }

  public void setContentType(String pContentType) {
    if (mMultiValueHeaders.contains(HttpHeaders.CONTENT_TYPE)) {
      mMultiValueHeaders.remove(HttpHeaders.CONTENT_TYPE);
    }
    mMultiValueHeaders.add(HttpHeaders.CONTENT_TYPE, pContentType);
  }

  public void setContentType(MediaType pMediaType) {
    setContentType(pMediaType.toString());
  }

  public void addHeader(String pHeaderName, String pMediaType) {
    mMultiValueHeaders.add(HttpHeaders.CONTENT_TYPE, pMediaType);
  }

  public void addHeader(String pHeaderName, MediaType pMediaType) {
    addHeader(HttpHeaders.CONTENT_TYPE, pMediaType.toString());
  }

  public String headerValue(String pHeaderName) {
    return mMultiValueHeaders.get(pHeaderName);
  }
}
