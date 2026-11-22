package io.github.nader7.dealsapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.Locale;

@Configuration
public class AppConfig {
    private static final String TIME_PATTERN = "[h:mma][H:mm]";

    @Bean
    RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }

    /**
     * Parses a time value according to 24h or 12h time formats
     */
    @Bean
    DateTimeFormatter dateTimeFormatter(){
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(TIME_PATTERN)
                .toFormatter(Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }
}
