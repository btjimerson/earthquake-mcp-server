package io.solo.earthquake.mcp;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.solo.earthquake.mcp.EarthquakeService.Earthquake.Feature;

/**
 * Service that queries the USGS earthquake service.
 */
@Service
public class EarthquakeService {

    private String usgsEarthquakeFormat;

    RestClient restClient;

    /**
     * Default constructor for the <code>EarthquakeService</code>.
     */
    public EarthquakeService(@Value("${usgs.earthquake.baseurl}") String usgsEarthquakeBaseUrl,
            @Value("${usgs.earthquake.format}") String usgsEarthquakeFormat) {

        this.usgsEarthquakeFormat = usgsEarthquakeFormat;

        this.restClient = RestClient.builder()
                .baseUrl(usgsEarthquakeBaseUrl)
                .defaultHeader("Accept", "application/geo+json")
                .defaultHeader("User-Agent", "WeatherApiClient/1.0 (your@email.com)")
                .build();
    }

    /**
     * Record that represents the Earthquake json result from the USGS earthquake
     * query service.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Earthquake(@JsonProperty("features") List<Feature> features) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Feature(@JsonProperty("properties") Properties properties) {

        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Properties(
                @JsonProperty("mag") Double mag,
                @JsonProperty("place") String place,
                @JsonProperty("time") Long time,
                @JsonProperty("detail") String detail,
                @JsonProperty("title") String title,
                @JsonProperty("ids") String ids) {

        }
    }

    /**
     * Gets a list of earthquakes within a date range.
     * 
     * @param startDate The start of the date range to search for earthquakes.
     * @param endDate   The end of the date range to search for earthquakes.
     * @return A list of earthquakes within the given date range.
     */
    @Tool(description = "Gets all earthquakes within a date range.")
    public String getEarthquakesByDate(String startDate, String endDate) {

        var earthquakes = restClient.get()
                .uri("/query?format={format}&starttime={starttime}&endtime={endtime}", this.usgsEarthquakeFormat,
                        startDate, endDate)
                .retrieve()
                .body(Earthquake.class);

        List<Feature> features = earthquakes.features();
        String earthquakesText = features.stream().map(f -> String.format("""
                Title: %s
                Magnitude: %.2f
                Place: %s
                Time: %tF
                Detail: %s
                IDs: %s
                """,
                f.properties().title,
                f.properties().mag,
                f.properties().place,
                f.properties().time,
                f.properties().detail,
                f.properties().ids))
                .collect(Collectors.joining("\n"));

        return earthquakesText.toString();

    }
}
