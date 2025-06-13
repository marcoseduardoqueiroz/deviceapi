package com.example.deviceapi.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration class to customize API documentation.
 * 
 * This configuration provides metadata for the API documentation such as title,
 * description, version, contact information, and license details. Additionally,
 * it links to external documentation to improve developer experience and ease
 * of integration.
 * 
 * How to access the API documentation: - Once the application is running, the
 * OpenAPI JSON specification is available at:
 * http://localhost:{port}/v3/api-docs - The interactive Swagger UI is
 * accessible at: http://localhost:{port}/swagger-ui.html
 * 
 * Replace {port} with the actual port your application is running on (default
 * 8080).
 */
@Configuration
public class SwaggerConfig {

	/**
	 * Defines the OpenAPI bean that configures the API metadata.
	 * 
	 * @return OpenAPI instance with custom info and external documentation.
	 */
	@Bean
	public OpenAPI deviceApiOpenAPI() {
		return new OpenAPI().info(new Info()
				// The title of the API shown in the Swagger UI
				.title("Device Management API")
				// Brief description of what the API does
				.description("REST API to manage IoT devices")
				// API versioning to allow consumers to know the current release
				.version("v1.0.0")
				// Contact details for support or inquiries
				.contact(new Contact().name("Integration Team").email("devteam@example.com")
						.url("https://www.example.com"))
				// License information, important for open source or usage terms
				.license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")))
				// External documentation link to provide further details or guides
				.externalDocs(new ExternalDocumentation().description("Complete Documentation")
						.url("https://docs.example.com/device-api"));
	}
}
