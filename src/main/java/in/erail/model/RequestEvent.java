package in.erail.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.io.BaseEncoding;
import io.vertx.core.http.HttpMethod;
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

  public String getResource() {
    return mResource;
  }

  public void setResource(String pResource) {
    this.mResource = pResource;
  }

  public String getPath() {
    return mPath;
  }

  public void setPath(String pPath) {
    this.mPath = pPath;
  }

  public HttpMethod getHttpMethod() {
    return mHttpMethod;
  }

  public void setHttpMethod(HttpMethod pHttpMethod) {
    this.mHttpMethod = pHttpMethod;
  }

  public Map<String, String> getHeaders() {
    return mHeaders;
  }

  public void setHeaders(Map<String, String> pHeaders) {
    this.mHeaders = pHeaders;
  }

  public Map<String, String[]> getMultiValueHeaders() {
    return mMultiValueHeaders;
  }

  public void setMultiValueHeaders(Map<String, String[]> pMultiValueHeaders) {
    this.mMultiValueHeaders = pMultiValueHeaders;
  }

  public Map<String, String> getQueryStringParameters() {
    return mQueryStringParameters;
  }

  public void setQueryStringParameters(Map<String, String> pQueryStringParameters) {
    this.mQueryStringParameters = pQueryStringParameters;
  }

  public Map<String, String[]> getMultiValueQueryStringParameters() {
    return mMultiValueQueryStringParameters;
  }

  public void setMultiValueQueryStringParameters(Map<String, String[]> pMultiValueQueryStringParameters) {
    this.mMultiValueQueryStringParameters = pMultiValueQueryStringParameters;
  }

  public Map<String, String> getPathParameters() {
    return mPathParameters;
  }

  public void setPathParameters(Map<String, String> pPathParameters) {
    this.mPathParameters = pPathParameters;
  }

  public Map<String, String> getStageVariables() {
    return mStageVariables;
  }

  public void setStageVariables(Map<String, String> pStageVariables) {
    this.mStageVariables = pStageVariables;
  }

  @SuppressWarnings("rawtypes")
	public Map getRequestContext() {
    return mRequestContext;
  }

  @SuppressWarnings("rawtypes")
	public void setRequestContext(Map pRequestContext) {
    this.mRequestContext = pRequestContext;
  }

  public boolean isIsBase64Encoded() {
    return mIsBase64Encoded;
  }

  public void setIsBase64Encoded(boolean pIsBase64Encoded) {
    this.mIsBase64Encoded = pIsBase64Encoded;
  }

  public byte[] getBody() {
    return mBody;
  }

  public void setBody(byte[] pBody) {
    this.mBody = pBody;
  }
  
  public String bodyAsString(){
    if(isIsBase64Encoded()){
      return new String(BaseEncoding.base64().decode(new String(getBody())));
    }
    return new String(getBody());
  }
}
