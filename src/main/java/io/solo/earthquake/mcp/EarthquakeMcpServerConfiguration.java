package io.solo.earthquake.mcp;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the EarthquakeMcpServer application.
 */
@Configuration
public class EarthquakeMcpServerConfiguration {

    /**
     * Creates a Tool object for the EarthquakeService
     * @param earthquakeService The EarthquakeService to create a Tool from.
     * @return A Tool that exposes the EarthquakeService
     */
    @Bean
    public ToolCallbackProvider earthquakeTools(EarthquakeService earthquakeService) {
        return MethodToolCallbackProvider.builder().toolObjects(earthquakeService).build();
    }
    
}
