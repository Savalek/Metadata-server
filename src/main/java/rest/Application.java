package rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
public class Application {

  private static final String PROG_PARAM_NAME = "--connections.config";

  public static void main(String[] args) {
    for (String arg : args) {
      if (arg.startsWith(PROG_PARAM_NAME)) {
        MetaSettings.CONNECTIONS_CONFIG_FILE_PATH = arg.substring(arg.indexOf(PROG_PARAM_NAME) + PROG_PARAM_NAME.length() + 1);
        break;
      }
    }
    SpringApplication.run(Application.class, args);
  }
}
