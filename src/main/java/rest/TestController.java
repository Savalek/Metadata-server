package rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

  @RequestMapping("/greeting")
  public String greeting(@RequestParam(value = "name", required = false, defaultValue = "incognito") String name) {
    return "Hello, " + name + "!";
  }
}
