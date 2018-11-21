package com.garow.base.service;

import static org.springframework.cloud.config.client.ConfigClientProperties.AUTHORIZATION;
import static org.springframework.cloud.config.client.ConfigClientProperties.STATE_HEADER;
import static org.springframework.cloud.config.client.ConfigClientProperties.TOKEN_HEADER;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigClientProperties.Credentials;
import org.springframework.cloud.config.client.ConfigClientStateHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.garow.base.pojo.config.TransitionData;
/**
 * 配置客户端，用于读取配置服的资源
 * @author seg
 *
 */
public class ConfigClientService implements ApplicationContextAware{
	private static final Logger LOG = LoggerFactory.getLogger(ConfigClientService.class);
	private RestTemplate restTemplate;
	private ConfigClientProperties defaultProperties;
	private ConfigurableApplicationContext appliaction;

	public ConfigClientService(ConfigClientProperties defaultProperties) {
		super();
		this.defaultProperties = defaultProperties;
	}
	/**
	 * 获取文件，自动填充了/{app}/{profile}/{label}/{path}
	 * @param path
	 * @param clazz
	 * @return
	 */
	public <T> T getFile(String path, Class<T> clazz) {
		ConfigurableEnvironment env = appliaction.getEnvironment();

		ConfigClientProperties properties = this.defaultProperties.override(env);
		RestTemplate restTemplate = this.restTemplate == null
				? getSecureRestTemplate(properties)
				: this.restTemplate;
		Exception error = null;
		String errorBody = null;
		try {
			String[] labels = new String[] { "" };
			if (StringUtils.hasText(properties.getLabel())) {
				labels = StringUtils
						.commaDelimitedListToStringArray(properties.getLabel());
			}
			String state = ConfigClientStateHolder.getState();
			// Try all the labels until one works
			for (String label : labels) {
				T result = getRemoteData(restTemplate, properties,
						label.trim(), state, path, clazz);
				if (result != null) {
					LOG.info("fetch:" + result.getClass().getSimpleName());
					if(result instanceof TransitionData) {
						((TransitionData)result).postInit();
					}

					return result;
				}
			}
		}
		catch (HttpServerErrorException e) {
			error = e;
			if (MediaType.APPLICATION_JSON
					.includes(e.getResponseHeaders().getContentType())) {
				errorBody = e.getResponseBodyAsString();
			}
		}
		catch (Exception e) {
			error = e;
		}
		if (properties.isFailFast()) {
			throw new IllegalStateException(
					"Could not locate PropertySource and the fail fast property is set, failing",
					error);
		}
		LOG.warn("Could not locate PropertySource: " + (errorBody == null
				? error == null ? "label not found" : error.getMessage()
				: errorBody));
		return null;

	
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	private RestTemplate getSecureRestTemplate(ConfigClientProperties client) {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		if (client.getRequestReadTimeout() < 0) {
			throw new IllegalStateException("Invalid Value for Read Timeout set.");
		}
		requestFactory.setReadTimeout(client.getRequestReadTimeout());
		RestTemplate template = new RestTemplate(requestFactory);
		Map<String, String> headers = new HashMap<>(client.getHeaders());
		if (headers.containsKey(AUTHORIZATION)) {
			headers.remove(AUTHORIZATION); // To avoid redundant addition of header
		}
		if (!headers.isEmpty()) {
			template.setInterceptors(Arrays.<ClientHttpRequestInterceptor> asList(
					new GenericRequestHeaderInterceptor(headers)));
		}

		return template;
	}

	private void addAuthorizationToken(ConfigClientProperties configClientProperties,
			HttpHeaders httpHeaders, String username, String password) {
		String authorization = configClientProperties.getHeaders().get(AUTHORIZATION);

		if (password != null && authorization != null) {
			throw new IllegalStateException(
					"You must set either 'password' or 'authorization'");
		}

		if (password != null) {
			byte[] token = Base64Utils.encode((username + ":" + password).getBytes());
			httpHeaders.add("Authorization", "Basic " + new String(token));
		}
		else if (authorization != null) {
			httpHeaders.add("Authorization", authorization);
		}

	}
	
	protected <T> T getRemoteData(RestTemplate restTemplate,
			ConfigClientProperties properties, String label, String state, String file, Class<T> resp) {

		String path = "/{name}/{profile}";
		String name = properties.getName();
		String profile = properties.getProfile();
		String token = properties.getToken();
		int noOfUrls = properties.getUri().length;
		if (noOfUrls > 1) {
			LOG.info("Multiple Config Server Urls found listed.");
		}

		Object[] args = new String[] { name, profile};
		if (StringUtils.hasText(label)) {
			if (label.contains("/")) {
				label = label.replace("/", "(_)");
			}
			args = new String[] { name, profile, label};
			path = path + "/{label}";
		}
		path = path + "/" + file;
		ResponseEntity<T> response = null;

		for (int i = 0; i < noOfUrls; i++) {
			Credentials credentials = properties.getCredentials(i);
			String uri = credentials.getUri();
			String username = credentials.getUsername();
			String password = credentials.getPassword();

			LOG.info("Fetching config from server at : " + uri);

			try {
				HttpHeaders headers = new HttpHeaders();
				addAuthorizationToken(properties, headers, username, password);
				if (StringUtils.hasText(token)) {
					headers.add(TOKEN_HEADER, token);
				}
				if (StringUtils.hasText(state) && properties.isSendState()) {
					headers.add(STATE_HEADER, state);
				}

				final HttpEntity<Void> entity = new HttpEntity<>((Void) null, headers);
				response = restTemplate.exchange(uri + path, HttpMethod.GET, entity,
						resp, args);
			}
			catch (HttpClientErrorException e) {
				if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
					throw e;
				}
			}
			catch (ResourceAccessException e) {
				LOG.info("Connect Timeout Exception on Url - " + uri
						+ ". Will be trying the next url if available");
				if (i == noOfUrls - 1)
					throw e;
				else
					continue;
			}

			if (response == null || response.getStatusCode() != HttpStatus.OK) {
				return null;
			}

			T result = response.getBody();
			return result;
		}

		return null;
	
	}

	public static class GenericRequestHeaderInterceptor
			implements ClientHttpRequestInterceptor {

		private final Map<String, String> headers;

		public GenericRequestHeaderInterceptor(Map<String, String> headers) {
			this.headers = headers;
		}

		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body,
				ClientHttpRequestExecution execution) throws IOException {
			for (Entry<String, String> header : headers.entrySet()) {
				request.getHeaders().add(header.getKey(), header.getValue());
			}
			return execution.execute(request, body);
		}

		protected Map<String, String> getHeaders() {
			return headers;
		}

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.appliaction = (ConfigurableApplicationContext) applicationContext;
	}
	
}
