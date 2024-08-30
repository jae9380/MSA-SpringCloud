package com.example.apigatewayservice.filter;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    Environment env;

    public AuthorizationHeaderFilter(Environment env) {
        super(Config.class);
        this.env = env;
    }

    public static class Config{

    }

    // 과정
    // 로그인을 하면 토큰을 반환 받는다. 다음으로 클라이언트에서 사용자의 정보를 요청을 할 때 토큰 정보를 바탕으로 요청을 한다.
    // 서버는 해당 요청이 올 때 토큰을 검증을 한다. 이 때 토큰은 헤더에 위치한다.
    // login -> token -> users (with token) -> header (include token)
    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
           ServerHttpRequest request = (ServerHttpRequest) exchange.getRequest();

           if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) return onError(exchange,"No authorization header", HttpStatus.UNAUTHORIZED);

           String authorizationHeader = request.getHeaders().get(org.springframework.http.HttpHeaders.AUTHORIZATION).get(0);
           String jwt = authorizationHeader.replace("Bearer", "");

           if (!isJwtValid(jwt)) return onError(exchange,"JWT Token is not valid", HttpStatus.UNAUTHORIZED);

            return chain.filter(exchange);
        });
    }

    private boolean isJwtValid(String jwt) {

        byte[] secretKeyBytes = Base64.getEncoder().encode(env.getProperty("token.secret").getBytes());
        SecretKey signingKey = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS512.getJcaName());

        boolean returnValue = true;

        String subject = null;

        try {
            JwtParser jwtParser = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build();

            subject=jwtParser.parseClaimsJwt(jwt).getBody().getSubject();
        }catch (Exception e) {
            returnValue = false;
        }

        if (subject==null || subject.isEmpty()) return false;

        return returnValue;
    }

    // Mono, Flux -> Spring WebFlux
    private Mono<Void> onError(ServerWebExchange exchange, String errMsg, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error(errMsg);

        byte[] bytes = "The requested token is invalid.".getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return response.writeWith(Flux.just(buffer));
    }
}


