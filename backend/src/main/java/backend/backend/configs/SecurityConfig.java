package backend.backend.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /*
        This is where we configure the security required for our endpoints and setup our app to serve as
        an OAuth2 Resource Server, using JWT validation.
        */
        return http
                .authorizeHttpRequests((authorize) -> authorize
                    .requestMatchers(HttpMethod.GET, "/api/healthcheck").permitAll()

                    // listing related
                    .requestMatchers(HttpMethod.GET, "/api/listings").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/listing/{listingId}").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/listing").authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/listing/images").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/api/listing/{listingId}").authenticated()
                    .requestMatchers(HttpMethod.PATCH, "/api/listing/{listingId}").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/user/listing").authenticated()

                    // user related
                    .requestMatchers(HttpMethod.GET, "/api/user_check").authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/user").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/user/{userId}").authenticated()

                    // messaging related
                    .requestMatchers(HttpMethod.POST, "/api/message").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/message").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/conversations").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/conversation/{convoId}").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/api/conversation/{convoId}").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/conversation").authenticated()
                    .requestMatchers(HttpMethod.PATCH, "/api/user/messaging").authenticated()

                    // payment related
                    .requestMatchers(HttpMethod.GET, "/api/payment/{listingId}").authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/payment/success").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/user/transactions").authenticated()

                    // favourite related
                    .requestMatchers(HttpMethod.GET, "/api/user/favourites").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/api/user/favourite/{listingId}").authenticated()
                    .requestMatchers(HttpMethod.PATCH, "/api/user/favourite/{listingId}").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/user/favourite/{listingId}").authenticated()
                )
                .cors(withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(withDefaults())
                )
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }
}
