package backend.backend.services;

import java.io.StringReader;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.nimbusds.jwt.SignedJWT;

import jakarta.json.Json;
import jakarta.json.JsonObject;

@Service
public class Auth0UserService {

    @Value("${auth0.management.api.client.secret}")
    private String clientSecret;

    @Value("${auth0.management.api.client.id}")
    private String clientId;

    @Value("${auth0.management.api.audience}")
    private String audience;

    @Value("${auth0.management.api.client.grant.type}")
    private String grantType;

    @Value("${auth0.management.api.domain}")
    private String domain;

    private RestTemplate template = new RestTemplate();

    private String accessTokenUrl = "https://%s/oauth/token";
    private String getUserUrl = "https://%s/api/v2/users";

    private JsonObject getAccessToken() {
        JsonObject payload = Json.createObjectBuilder()
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("audience", audience)
                .add("grant_type", grantType)
                .build();
        RequestEntity<String> req = RequestEntity
                .post(accessTokenUrl.formatted(domain))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .body(payload.toString(), String.class);
        ResponseEntity<String> resp = template.exchange(req, String.class);
        return Json.createReader(new StringReader(resp.getBody())).readObject();
    }

    private JsonObject getUserProfileData(String userId) {
        JsonObject accessToken = getAccessToken();
        String authToken = "%s %s".formatted(accessToken.getString("token_type"),
                accessToken.getString("access_token"));
        String url = UriComponentsBuilder.fromUriString(getUserUrl)
                .pathSegment(userId)
                .build().toUriString();
        RequestEntity<Void> req = RequestEntity
                .get(url.formatted(domain))
                .header("Authorization", authToken)
                .build();
        ResponseEntity<String> resp = template.exchange(req, String.class);
        return Json.createReader(new StringReader(resp.getBody())).readObject();
    }

    public String getUserEmail(String userId) {
        JsonObject userProfile = getUserProfileData(userId);
        return userProfile.getString("email");
    }

    public String getUserIdFromJWT(String token) throws ParseException {
        return Json.createReader(new StringReader(parseJWT(token))).readObject().getString("sub");
    }

    public String parseJWT(String accessToken) throws ParseException {
        accessToken = accessToken.replaceFirst("Bearer ", "");
        try {
            var decodedJWT = SignedJWT.parse(accessToken);
            var payload = decodedJWT.getPayload().toString();
            return payload;
        } catch (ParseException e) {
            throw new ParseException("Unable to parse jwt token", e.getErrorOffset());
        }
    }
}
