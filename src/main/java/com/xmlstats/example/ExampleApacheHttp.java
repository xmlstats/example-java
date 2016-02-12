package com.xmlstats.example;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class ExampleApacheHttp {

    static final String ACCESS_TOKEN = "access_token";

    static final String USER_AGENT_CONTACT = "user_agent_contact";

    static final String USER_AGENT = "xmlstats-javahc/%s (%s)";

    static final String TIME_ZONE = "time_zone";

    static final String VERSION = "version";

    static final String AUTHORIZATION = "Authorization";

    static final String BEARER_AUTH_TOKEN = "Bearer %s";

    static final String ACCEPT_ENCODING = "Accept-encoding";

    static final String GZIP = "gzip";

    // For brevity, the url with api method, format, and parameters
    static final String REQUEST_URL = "https://erikberg.com/events.json?sport=nba&date=20130414";

    public static void main(String[] args) {
        String accessToken = String.format(BEARER_AUTH_TOKEN, getPropertyValue(ACCESS_TOKEN));
        String userAgent = String.format(USER_AGENT, getPropertyValue(VERSION),
                getPropertyValue(USER_AGENT_CONTACT));
        ResponseHandler<Events> responseHandler = new HttpResponseHandler<>(Events.class);
        try (CloseableHttpClient httpClient = HttpClientBuilder
                .create()
                .setUserAgent(userAgent)
                .build()) {
            HttpUriRequest request = new HttpGet(REQUEST_URL);
            request.addHeader(AUTHORIZATION, accessToken);
            request.addHeader(ACCEPT_ENCODING, GZIP);
            Events events = httpClient.execute(request, responseHandler);
            printResult(events);
        } catch (HttpResponseException ex) {
            System.err.println("Server returned HTTP status: " + ex.getStatusCode()
                    + ". " + ex.getMessage());
        } catch (JsonParseException | JsonMappingException ex) {
            System.err.println("Error parsing json at line: " + ex.getLocation().getLineNr()
                    + ", column: " + ex.getLocation().getColumnNr());
            ex.printStackTrace();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    static void printResult(Events events) {
        try {
            ZonedDateTime date = ZonedDateTime.parse(events.getEventsDate());
            DateTimeFormatter full = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
            System.out.printf("Events on %s%n%n", date.format(full));
            System.out.printf("%-36s %5s %33s%n", "Time", "Event", "Status");

            // Set the time zone for output
            String timeZone = getPropertyValue(TIME_ZONE);
            DateTimeFormatter sdf = DateTimeFormatter.ofPattern("h:mm a zzz")
                    .withZone(ZoneId.of(timeZone));
            // Loop through each Event (https://erikberg.com/api/objects/event)
            for (Event evt : events.getEventList()) {
                date = ZonedDateTime.parse(evt.getStartDateTime());
                System.out.printf("%12s %24s vs. %-24s %9s%n",
                        date.format(sdf),
                        evt.getAwayTeam().getFullName(),
                        evt.getHomeTeam().getFullName(),
                        evt.getEventStatus());
            }
        } catch (DateTimeParseException ex) {
            System.err.println("Error parsing date: " + ex.getMessage());
        }
    }

    static String getPropertyValue(String key) {
        String value = null;
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("xmlstats");
            value = resourceBundle.getString(key);
        } catch (MissingResourceException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return value;
    }

    static class HttpResponseHandler<T> implements ResponseHandler<T> {

        private final Class<T> clazz;

        HttpResponseHandler(Class<T> clazz) {
            this.clazz = clazz;
        }

        public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
            ObjectMapper mapper = new ObjectMapper();
            StatusLine status = response.getStatusLine();
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new ClientProtocolException("Response is empty.");
            }

            // Decompress response if it is compressed
            if (GZIP.equals(entity.getContentEncoding())) {
                response.setEntity(new GzipDecompressingEntity(response.getEntity()));
            }

            int statusCode = status.getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                String reason = status.getReasonPhrase();
                // If there is an error and the content type is json, it will be an
                // XmlstatsError. See https://erikberg.com/api/objects/xmlstats-error
                if ("application/json".equals(entity.getContentType().getValue())) {
                    mapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
                    XmlstatsError error = mapper.readValue(entity.getContent(), XmlstatsError.class);
                    reason = error.getDescription();
                }
                throw new HttpResponseException(statusCode, reason);
            }
            return mapper.readValue(entity.getContent(), clazz);
        }
    }

}
