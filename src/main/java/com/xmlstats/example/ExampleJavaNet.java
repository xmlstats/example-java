package com.xmlstats.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.zip.GZIPInputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

class ExampleJavaNet {

    static final String ACCESS_TOKEN = "access_token";

    static final String TIME_ZONE = "time_zone";

    static final String USER_AGENT = "User-agent";

    static final String USER_AGENT_CONTACT = "user_agent_contact";

    static final String USER_AGENT_NAME = "xmlstats-javanet/%s (%s)";

    static final String VERSION = "version";

    static final String AUTHORIZATION = "Authorization";

    static final String BEARER_AUTH_TOKEN = "Bearer %s";

    static final String ACCEPT_ENCODING = "Accept-encoding";

    static final String GZIP = "gzip";

    // For brevity, the url with api method, format, and parameters
    static final String REQUEST_URL = "https://erikberg.com/events.json?sport=nba&date=20130414";

    public static void main(String[] args) {
        String accessToken = String.format(BEARER_AUTH_TOKEN, getPropertyValue(ACCESS_TOKEN));
        String userAgent = String.format(USER_AGENT_NAME,
                getPropertyValue(VERSION),
                getPropertyValue(USER_AGENT_CONTACT));

        InputStream in = null;
        try {
            URL url = new URL(REQUEST_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Set Authorization header
            connection.setRequestProperty(AUTHORIZATION, accessToken);
            // Set User agent header
            connection.setRequestProperty(USER_AGENT, userAgent);
            // Tell server we can handle gzip content
            connection.setRequestProperty(ACCEPT_ENCODING, GZIP);

            // Check the HTTP status code for "200 OK"
            int statusCode = connection.getResponseCode();
            String encoding = connection.getContentEncoding();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                in = connection.getInputStream();
                if (in != null) {
                    // read in http response
                    String response = readHttpResponse(in, encoding);
                    if (response != null) {
                        // Have the data we want, now call function to parse it
                        printResult(response);
                    }
                }
            } else {
                // handle HTTP error
                System.err.println("Server returned HTTP status: " + statusCode
                        + ". " + connection.getResponseMessage());
                in = connection.getErrorStream();
                if (in != null) {
                    String response = readHttpResponse(in, encoding);
                    System.err.println(response);
                }
                System.exit(1);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static String readHttpResponse(InputStream in, String encoding) {
        StringBuilder sb = new StringBuilder();
        // Verify the response is compressed, before attempting to decompress it
        try {
            if (GZIP.equals(encoding)) {
                in = new GZIPInputStream(in);
            }
        } catch (IOException ex) {
            System.err.println("Error trying to read gzip data.");
            ex.printStackTrace();
            System.exit(1);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException ex) {
            System.err.println("Error reading response.");
            ex.printStackTrace();
            System.exit(1);
        }
        return sb.toString();
    }

    static void printResult(String data) {
        try {
            // These two lines of code take the JSON string and return a POJO. In
            // this case, an Events object (https://erikberg.com/api/methods/events)
            ObjectMapper mapper = new ObjectMapper();
            Events events = mapper.readValue(data, Events.class);

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
        } catch (IOException | DateTimeParseException ex) {
            System.err.println("Could not parse JSON data: " + ex.getMessage());
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

}
