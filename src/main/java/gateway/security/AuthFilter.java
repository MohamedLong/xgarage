package gateway.security;

import gateway.request.Authorities;
import gateway.request.ConnValidationResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@Component
@Slf4j
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {
    private final WebClient.Builder webClientBuilder;

    @Value("${spring.security.permitted-urls}")
    private List<String> urlList;

    public AuthFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        try {
            return (exchange, chain) -> {
                log.info("inside gateway filter");
                if (isValid(urlList, exchange.getRequest().getPath().value())) {
                    return chain.filter(exchange);
                }

                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("Missing auth information.");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                String[] parts = authHeader.split(" ");

                if (parts.length != 2 || !"Bearer".equals(parts[0])) {
                    throw new RuntimeException("Incorrect auth structure.");
                }
                return webClientBuilder
                        .build()
                        .get()
                        .uri("lb://xgarage-kernel-service/api/v1/validateToken")
                        .header(HttpHeaders.AUTHORIZATION, authHeader)
                        .retrieve().bodyToMono(ConnValidationResponse.class)
                        .map(ConnValidationResponse -> {
                            exchange.getRequest().mutate().header(HttpHeaders.AUTHORIZATION, authHeader);
                            exchange.getRequest().mutate().header("id", ConnValidationResponse.getId() + "");
                            exchange.getRequest().mutate().header("tenant", ConnValidationResponse.getTenant() + "");
                            exchange.getRequest().mutate().header("tenantType",
                                    ConnValidationResponse.getTenantType() + "");
                            exchange.getRequest().mutate().header("userId", ConnValidationResponse.getUserId() + "");
                            exchange.getRequest().mutate().header("authorities",
                                    ConnValidationResponse.getAuthorities().stream().map(Authorities::getAuthority)
                                            .reduce("", (a, b) -> !a.isBlank() ? a + "," + b : a + b));
                            exchange.getRequest().mutate().header("status",
                                    ConnValidationResponse.isStatus() ? "true" : "false");
                            return exchange;
                        }).flatMap(chain::filter);
            };
        } catch (Exception e) {
            log.error("Exception inside Gateway Filter: " + e.getMessage());
            return null;
        }
    }

    private boolean isValid(List<String> list, String target) {
        return list.stream().anyMatch(target::startsWith);
    }

    public static class Config {
    }
}
