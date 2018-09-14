package rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
//@PropertySource("classpath:application.properties")
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
