package in.erail.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.io.BaseEncoding;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import java.util.Map;

/**
 *
 * @author vinay
 */
@JsonInclude(Include.NON_NULL)
public class RequestEvent {

  private String mResource;
  private String mPath;
  private HttpMethod mHttpMethod;
  private Map<String, String> mHeaders;
  private Map<String, String[]> mMultiValueHeaders;
  private Map<String, String> mQueryStringParameters;
  private Map<String, String[]> mMultiValueQueryStringParameters;
  private Map<String, String> mPathParameters;
  private Map<String, String> mStageVariables;
  @SuppressWarnings("rawtypes")
  private Map mRequestContext;
  private byte[] mBody = new byte[0];
  private boolean mIsBase64Encoded = false;
  private Map<String, Object> mPrincipal;
  private Object mSubject;

  public String getResource() {
    return mResource;
  }

  public RequestEvent setResource(String pResource) {
    this.mResource = pResource;
    return this;
  }

  public String getPath() {
    return mPath;
  }

  public RequestEvent setPath(String pPath) {
    this.mPath = pPath;
    return this;
  }

  public HttpMethod getHttpMethod() {
    return mHttpMethod;
  }

  public RequestEvent setHttpMethod(HttpMethod pHttpMethod) {
    this.mHttpMethod = pHttpMethod;
    return this;
  }

  public Map<String, String> getHeaders() {
    return mHeaders;
  }

  public RequestEvent setHeaders(Map<String, String> pHeaders) {
    this.mHeaders = pHeaders;
    return this;
  }

  public Map<String, String[]> getMultiValueHeaders() {
    return mMultiValueHeaders;
  }

  public RequestEvent setMultiValueHeaders(Map<String, String[]> pMultiValueHeaders) {
    this.mMultiValueHeaders = pMultiValueHeaders;
    return this;
  }

  public Map<String, String> getQueryStringParameters() {
    return mQueryStringParameters;
  }

  public RequestEvent setQueryStringParameters(Map<String, String> pQueryStringParameters) {
    this.mQueryStringParameters = pQueryStringParameters;
    return this;
  }

  public Map<String, String[]> getMultiValueQueryStringParameters() {
    return mMultiValueQueryStringParameters;
  }

  public RequestEvent setMultiValueQueryStringParameters(Map<String, String[]> pMultiValueQueryStringParameters) {
    this.mMultiValueQueryStringParameters = pMultiValueQueryStringParameters;
    return this;
  }

  public Map<String, String> getPathParameters() {
    return mPathParameters;
  }

  public RequestEvent setPathParameters(Map<String, String> pPathParameters) {
    this.mPathParameters = pPathParameters;
    return this;
  }

  public Map<String, String> getStageVariables() {
    return mStageVariables;
  }

  public RequestEvent setStageVariables(Map<String, String> pStageVariables) {
    this.mStageVariables = pStageVariables;
    return this;
  }

  @SuppressWarnings("rawtypes")
  public Map getRequestContext() {
    return mRequestContext;
  }

  @SuppressWarnings("rawtypes")
  public RequestEvent setRequestContext(Map pRequestContext) {
    this.mRequestContext = pRequestContext;
    return this;
  }

  public boolean isIsBase64Encoded() {
    return mIsBase64Encoded;
  }

  public RequestEvent setIsBase64Encoded(boolean pIsBase64Encoded) {
    this.mIsBase64Encoded = pIsBase64Encoded;
    return this;
  }

  public byte[] getBody() {
    return mBody;
  }

  public RequestEvent setBody(byte[] pBody) {
    this.mBody = pBody;
    return this;
  }

  public String bodyAsString() {
    if (isIsBase64Encoded()) {
      return new String(BaseEncoding.base64().decode(new String(getBody())));
    }
    return new String(getBody());
  }

  public Map<String, Object> getPrincipal() {
    return mPrincipal;
  }

  public RequestEvent setPrincipal(Map<String, Object> pPrincipal) {
    this.mPrincipal = pPrincipal;
    return this;
  }

  @Override
  public String toString() {
    return JsonObject.mapFrom(this).toString();
  }

  public Object getSubject() {
    return mSubject;
  }

  public RequestEvent setSubject(Object pSubject) {
    this.mSubject = pSubject;
    return this;
  }

}
