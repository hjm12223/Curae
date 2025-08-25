package com.example.curea.common.util;

import com.example.curea.common.config.WebClientConfig;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebClientUtil {

    private final WebClientConfig webClientConfig;

    public <T> T get(final URI uri, final Class<T> responseDtoClass) {
        return webClientConfig.webClient().get()
            .uri(uri)
            .retrieve()
            .onStatus(HttpStatusCode::isError, this::handleError)
            .bodyToMono(responseDtoClass)
            .block();
    }


    private Mono<? extends Throwable> handleError(ClientResponse clientResponse) {
        log.error("API 에러 응답: Status code {}", clientResponse.statusCode());
        return clientResponse.bodyToMono(String.class)
            .flatMap(body -> {
                    log.error("API 에러 본문: {}", body);
                    return Mono.error(
                        new RuntimeException("API 요청 실패: " + clientResponse.statusCode()));
                }
            );
    }
}
