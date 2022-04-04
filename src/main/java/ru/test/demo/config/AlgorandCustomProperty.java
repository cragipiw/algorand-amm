package ru.test.demo.config;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "algorand.custom")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class AlgorandCustomProperty {

  @NotBlank
  String host;

  @NotNull
  Integer port;

  @NotBlank
  String token;

}
