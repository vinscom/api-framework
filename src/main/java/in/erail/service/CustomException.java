package in.erail.service;

import com.google.common.net.MediaType;

/**
 *
 * @author vinay
 */
public interface CustomException {

  default public MediaType getMediaType() {
    return MediaType.JSON_UTF_8;
  }

  default int getStatusCode() {
    return 400;
  }
}
