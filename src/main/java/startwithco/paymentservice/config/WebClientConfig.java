package startwithco.paymentservice.config;

import com.google.common.net.HttpHeaders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    @Qualifier("tossPaymentWebClient")
    public WebClient tossPaymentWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}