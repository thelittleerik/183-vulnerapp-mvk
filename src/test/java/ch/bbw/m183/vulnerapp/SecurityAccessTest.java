package ch.bbw.m183.vulnerapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * >>>>>KI GENERIERT /\Zeit Druck/\<<<<<
 * Security tests asserting the expected access-control matrix from the assignment.
 * Uses a real login + CSRF token flow (no mocked authentication) so the
 * "with CSRF" vs "without CSRF" columns are exercised faithfully.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityAccessTest {

	@Value("${local.server.port}")
	int port;

	private WebTestClient client;

	@BeforeEach
	void setUp() {
		client = WebTestClient.bindToServer()
				.baseUrl("http://localhost:" + port)
				.build();
	}

	/** A logged-in session: the authenticated JSESSIONID plus the (rotated) CSRF token. */
	private record Session(String jSessionId, String csrf) {}

	/**
	 * Performs a safe GET to obtain the current CSRF token from the XSRF-TOKEN cookie.
	 * Pass a JSESSIONID to read the token bound to that authenticated session.
	 */
	private String fetchCsrfToken(String jSessionId) {
		WebTestClient.RequestHeadersSpec<?> spec = client.get().uri("/api/blog");
		if (jSessionId != null) {
			spec = spec.cookie("JSESSIONID", jSessionId);
		}
		EntityExchangeResult<byte[]> result = spec
				.exchange()
				.expectStatus().isOk()
				.expectBody().returnResult();
		return result.getResponseCookies().getOrDefault("XSRF-TOKEN", List.of()).stream()
				.map(ResponseCookie::getValue)
				.filter(v -> !v.isBlank())
				.findFirst()
				.orElseThrow(() -> new AssertionError("XSRF-TOKEN cookie must be set on a safe request"));
	}

	/** Logs in via the real form-login endpoint and returns the authenticated session + its CSRF token. */
	private Session login(String username, String password) {
		String csrf = fetchCsrfToken(null);
		EntityExchangeResult<byte[]> result = client.post().uri("/login")
				.header("X-XSRF-TOKEN", csrf)
				.cookie("XSRF-TOKEN", csrf)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(BodyInserters.fromFormData("username", username).with("password", password))
				.exchange()
				.expectStatus().isOk()
				.expectBody().returnResult();
		var jSessionCookie = result.getResponseCookies().getFirst("JSESSIONID");
		assertThat(jSessionCookie).as("login must create a session").isNotNull();
		String jSessionId = jSessionCookie.getValue();
		// fetch the CSRF token bound to the now-authenticated session
		return new Session(jSessionId, fetchCsrfToken(jSessionId));
	}

	// ---------------------------------------------------------------- anonymous

	@Test
	void anonymous_getRoot_isOk() {
		client.get().uri("/").exchange().expectStatus().isOk();
	}

	@Test
	void anonymous_getBlog_isOk() {
		client.get().uri("/api/blog").exchange().expectStatus().isOk();
	}

	@Test
	void anonymous_postBlog_isForbidden_noCsrf() {
		client.post().uri("/api/blog")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue("{\"title\":\"a valid title\",\"body\":\"a valid body\"}")
				.exchange()
				.expectStatus().isForbidden();
	}

	@Test
	void anonymous_whoami_isUnauthorized() {
		client.get().uri("/api/user/whoami").exchange().expectStatus().isUnauthorized();
	}

	@Test
	void anonymous_admin_isUnauthorized() {
		client.get().uri("/api/admin/users").exchange().expectStatus().isUnauthorized();
	}

	@Test
	void anonymous_health_isOk_withoutDetails() {
		client.get().uri("/actuator/health")
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.value(body -> assertThat(body).doesNotContain("components"));
	}

	// --------------------------------------------------------------- user (fuu)

	@Test
	void user_postBlog_withoutCsrf_isForbidden() {
		Session s = login("fuu", "bar");
		client.post().uri("/api/blog")
				.cookie("JSESSIONID", s.jSessionId())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue("{\"title\":\"a valid title\",\"body\":\"a valid body\"}")
				.exchange()
				.expectStatus().isForbidden();
	}

	@Test
	void user_postBlog_withCsrf_isOk() {
		Session s = login("fuu", "bar");
		client.post().uri("/api/blog")
				.cookie("JSESSIONID", s.jSessionId())
				.cookie("XSRF-TOKEN", s.csrf())
				.header("X-XSRF-TOKEN", s.csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue("{\"title\":\"a valid title\",\"body\":\"a valid body\"}")
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	void user_whoami_isOk() {
		Session s = login("fuu", "bar");
		client.get().uri("/api/user/whoami")
				.cookie("JSESSIONID", s.jSessionId())
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.username").isEqualTo("fuu")
				.jsonPath("$.password").doesNotExist();
	}

	@Test
	void user_admin_isForbidden() {
		Session s = login("fuu", "bar");
		client.get().uri("/api/admin/users")
				.cookie("JSESSIONID", s.jSessionId())
				.cookie("XSRF-TOKEN", s.csrf())
				.header("X-XSRF-TOKEN", s.csrf())
				.exchange()
				.expectStatus().isForbidden();
	}

	// ------------------------------------------------------------- admin

	@Test
	void admin_listUsers_isOk_andHidesPasswords() {
		Session s = login("admin", "super5ecret");
		client.get().uri("/api/admin/users")
				.cookie("JSESSIONID", s.jSessionId())
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.content[0].password").doesNotExist();
	}

	@Test
	void admin_createAndDeleteUser_withCsrf_isOk() {
		Session s = login("admin", "super5ecret");
		// create
		client.post().uri("/api/admin/create")
				.cookie("JSESSIONID", s.jSessionId())
				.cookie("XSRF-TOKEN", s.csrf())
				.header("X-XSRF-TOKEN", s.csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue("{\"username\":\"tempuser\",\"fullname\":\"Temp User\","
						+ "\"password\":\"Valid1Pass!word\",\"role\":\"USER\"}")
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.username").isEqualTo("tempuser")
				.jsonPath("$.password").doesNotExist();
		// delete
		client.method(org.springframework.http.HttpMethod.DELETE).uri("/api/admin/delete/tempuser")
				.cookie("JSESSIONID", s.jSessionId())
				.cookie("XSRF-TOKEN", s.csrf())
				.header("X-XSRF-TOKEN", s.csrf())
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	void admin_createUser_weakPassword_isBadRequest() {
		Session s = login("admin", "super5ecret");
		client.post().uri("/api/admin/create")
				.cookie("JSESSIONID", s.jSessionId())
				.cookie("XSRF-TOKEN", s.csrf())
				.header("X-XSRF-TOKEN", s.csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue("{\"username\":\"weaky\",\"fullname\":\"Weak User\","
						+ "\"password\":\"short\",\"role\":\"USER\"}")
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void admin_health_isOk_withDetails() {
		Session s = login("admin", "super5ecret");
		client.get().uri("/actuator/health")
				.cookie("JSESSIONID", s.jSessionId())
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.value(body -> assertThat(body).contains("components"));
	}
}
