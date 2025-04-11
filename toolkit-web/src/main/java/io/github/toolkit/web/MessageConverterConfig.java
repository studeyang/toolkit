package io.github.toolkit.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jian.xu
 */
@Configuration
public class MessageConverterConfig {

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper _halObjectMapper) {
        List<MediaType> mediaTypes = new ArrayList();
        mediaTypes.addAll(MediaType.parseMediaTypes("text/plain; charset=utf-8,plain/text; charset=utf-8,application/json; charset=utf-8,application/hal+json; charset=UTF-8"));
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setSupportedMediaTypes(mediaTypes);
        jsonConverter.setObjectMapper(_halObjectMapper);
        return jsonConverter;
    }
}
