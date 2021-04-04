package com.llandolfi.kata.steps;

import cucumber.api.java8.En;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

public class KataSteps implements En {

    private static final String baseResponsesPath = "responses/";
    private static final String baseRequestsPath = "requests/";
    private final HttpClient client = HttpClientBuilder.create().build();
    private HttpResponse response = null;

    public KataSteps() {
        When("a consumer makes an API GET request {string}", (String uri) -> {
            System.setProperty("https.protocols", "TLSv1.1");
            HttpGet httpGet = new HttpGet("http://localhost:8080" + uri);
            response = client.execute(httpGet);
        });

        When("a consumer makes an API POST request {string} with body from file {string}",
                (String uri, String fileWithBody) -> {
                    final InputStream resourceAsStream = ClassLoader.getSystemClassLoader()
                            .getResourceAsStream(baseRequestsPath + fileWithBody);

                    if (resourceAsStream == null) {
                        throw new IllegalArgumentException(
                                "file " + baseRequestsPath + fileWithBody + " is not found!");
                    }
                    try (Scanner scanner = new Scanner(resourceAsStream, StandardCharsets.UTF_8.name())) {
                        final String body = scanner.useDelimiter("\\A").next();

                        System.setProperty("https.protocols", "TLSv1.1");
                        HttpPost httpPost = new HttpPost("http://localhost:8080" + uri);
                        httpPost.setHeader("Accept", "application/json");
                        httpPost.setHeader("Content-type", "application/json");
                        StringEntity entity = new StringEntity(body);
                        httpPost.setEntity(entity);
                        response = client.execute(httpPost);
                    }
                });

        Then("the API returns an {string} status code", (String statusReason) -> {
            assertThat(response.getStatusLine().getReasonPhrase()).isEqualTo(statusReason);
        });

        Then("a valid data response {string}", (String jsonDataShouldFile) -> {

            try (Scanner scannerAsIs = new Scanner(response.getEntity().getContent(), StandardCharsets.UTF_8.name())) {
                final InputStream resourceAsStream = ClassLoader.getSystemClassLoader()
                        .getResourceAsStream(baseResponsesPath + jsonDataShouldFile);

                if (resourceAsStream == null) {
                    throw new IllegalArgumentException(
                            "file " + baseResponsesPath + jsonDataShouldFile + " is not found!");
                }
                try (Scanner scannerShouldBe = new Scanner(resourceAsStream, StandardCharsets.UTF_8.name())) {
                    final String jsonAsShouldBe = scannerShouldBe.useDelimiter("\\A").next();
                    final String jsonAsIS = scannerAsIs.useDelimiter("\\A").next();
                    JSONAssert.assertEquals(jsonAsShouldBe, jsonAsIS, JSONCompareMode.NON_EXTENSIBLE);
                }
            }
        });
    }
}