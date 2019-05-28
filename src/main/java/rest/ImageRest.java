package rest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(value = "/image", produces = MediaType.IMAGE_PNG_VALUE)
public class ImageRest {

  private static final Logger log = LoggerFactory.getLogger(ImageRest.class);

  private static final String DEFAULT_IMAGE = "field_image.png";
  private static Map<String, byte[]> images;
  private static HttpHeaders httpHeaders;

  private final Environment environment;

  @Autowired
  public ImageRest(Environment environment) {
    this.environment = environment;
  }

  @PostConstruct
  public void init() {
    images = new HashMap<>();

    // load images
    try {
      File imageDir = new File(environment.getProperty("ICON_FOLDER_PATH"));
      for (File sizeDir : Objects.requireNonNull(imageDir.listFiles())) {
        for (File imageFile : Objects.requireNonNull(sizeDir.listFiles())) {
          String key = imageFile.getName() + '#' + sizeDir.getName();
          byte[] value = IOUtils.toByteArray(new FileInputStream(imageFile));
          images.put(key, value);
        }
      }
    } catch (Exception e) {
      log.error("Can't load images.", e);
    }
    log.info("Loaded images count: " + images.size());

    // enable image cache on client
    httpHeaders = new HttpHeaders();
    httpHeaders.set("Cache-Control", "public, max-age=86400"); // 24 hours
  }

  @GetMapping("/{filename}/{size}")
  public ResponseEntity<byte[]> getImage(@PathVariable final String filename, @PathVariable final String size) {
    byte[] imageBytes = images.get(filename + "#" + size);
    if (imageBytes == null) {
      imageBytes = images.get(DEFAULT_IMAGE + "#" + size);
      log.warn("Image with name '" + filename + "' not exists");
    }
    return ResponseEntity.ok().headers(httpHeaders).body(imageBytes);
  }
}
