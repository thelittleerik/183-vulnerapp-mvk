package ch.bbw.m183.vulnerapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HealthService {

	private final HealthEndpoint healthEndpoint;

	public String health() {
		// query the actuator health in-process; no outbound HTTP, no credentials
		return healthEndpoint.health().getStatus().getCode();
	}
}
