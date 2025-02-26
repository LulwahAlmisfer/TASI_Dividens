package org.example.tasi_dividens.services;

import org.example.tasi_dividens.helpers.JWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class PushNotificationService {

    @Value("${apple.url}")
    private String appleUrl;

    @Value("${apns.key}")
    private String key;

    @Value("${apns.key-id}")
    private String keyId;

    @Value("${apns.team-id}")
    private String teamId;

    public void sendPush(String deviceToken, String title, String body, String link, String topic) {
        try {

            URI uri = URI.create(appleUrl + deviceToken);
            String jsonRequest = String.format("""
          {
             "aps": {
                "alert": {
                  "title": "%s",
                  "body": "%s"
                },
                "sound": "default"
             },
             "link": "%s"
          }
        """, title, body, link);

            String token = JWT.getApnJwtToken(key, keyId, teamId);

            HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .build();

            HttpRequest httpRequest = HttpRequest.newBuilder(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .header("authorization", "bearer " + token)
                    .header("apns-topic", topic)
                    .header("apns-push-type", "alert")
                    .build();

            var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Success");
            } else {
                System.err.printf("APNs error: status=%d, body=%s%n", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

