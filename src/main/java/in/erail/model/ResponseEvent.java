package in.erail.model;

import com.google.common.base.Preconditions;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import io.vertx.core.json.JsonObject;
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

  public ResponseEvent setIsBase64Encoded(boolean pIsBase64Encoded) {
    this.mIsBase64Encoded = pIsBase64Encoded;
    return this;
  }

  public int getStatusCode() {
    return mStatusCode;
  }

  public ResponseEvent setStatusCode(int pStatusCode) {
    this.mStatusCode = pStatusCode;
    return this;
  }

  public byte[] getBody() {
    return mBody;
  }

  public ResponseEvent setBody(byte[] pBody) {
    this.mBody = pBody;
    return this;
  }

  public Map<String, String>[] getCookies() {
    return mCookies;
  }

  public ResponseEvent setCookies(Map<String, String>[] pCookies) {
    this.mCookies = pCookies;
    return this;
  }

  public ResponseEvent setMultiValueHeaders(Map<String, String[]> pValue) {
    Preconditions.checkNotNull(pValue);

    if (pValue.isEmpty()) {
      return this;
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
    
    return this;
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

  public ResponseEvent setHeaders(Map<String, String> pValue) {
    Preconditions.checkNotNull(pValue);

    if (pValue.isEmpty()) {
      return this;
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
    return this;
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

  public ResponseEvent setContentType(String pContentType) {
    if (mMultiValueHeaders.contains(HttpHeaders.CONTENT_TYPE)) {
      mMultiValueHeaders.remove(HttpHeaders.CONTENT_TYPE);
    }
    mMultiValueHeaders.add(HttpHeaders.CONTENT_TYPE, pContentType);
    return this;
  }

  public ResponseEvent setContentType(MediaType pMediaType) {
    setContentType(pMediaType.toString());
    return this;
  }

  public ResponseEvent addHeader(String pHeaderName, String pMediaType) {
    mMultiValueHeaders.add(HttpHeaders.CONTENT_TYPE, pMediaType);
    return this;
  }

  public ResponseEvent addHeader(String pHeaderName, MediaType pMediaType) {
    addHeader(HttpHeaders.CONTENT_TYPE, pMediaType.toString());
    return this;
  }

  public String headerValue(String pHeaderName) {
    return mMultiValueHeaders.get(pHeaderName);
  }

  @Override
  public String toString() {
    return JsonObject.mapFrom(this).toString();
  }
  
}
