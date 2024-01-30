package kr.nadeuli.config;

import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class AppConfig {

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
    configurer.setFileEncoding(StandardCharsets.UTF_8.name());
    return configurer;
  }

  @Bean
  public PropertiesFactoryBean propertyFactoryBean() {
    PropertiesFactoryBean factoryBean = new PropertiesFactoryBean();
    // 별도의 파일 경로를 지정할 필요 없음
    factoryBean.setLocation(new ClassPathResource("application.properties"));
    factoryBean.setFileEncoding(StandardCharsets.UTF_8.name());
    return factoryBean;
  }
}