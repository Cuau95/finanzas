package com.cuau.finanzas.infrastructure.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import com.cuau.finanzas.domain.exception.ResourceNotFoundException;
import com.cuau.finanzas.domain.model.ConcurrentValue;
import com.cuau.finanzas.domain.service.ConcurrentValueService;
import com.cuau.finanzas.infrastructure.dto.ConcurrentValueRequest;
import com.cuau.finanzas.infrastructure.mapper.ConcurrentValueMapper;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(value = ConcurrentValueController.class)
@Import(ConcurrentValueMapper.class)
public class ConcurrentValueControllerMvcTest {

	private final static String TEST_NAME = "Concurrent Value Test";
	private final static String SOURCE_PATH = "/concurrentvalues";

	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private ConcurrentValueService service;
	@MockitoSpyBean
	private ConcurrentValueMapper mapper;

	@Test
	void shouldCreateConcurrentValueWhenRequestIsValid() throws Exception {
		when(service.saveConcurrentValue(any(ConcurrentValue.class)))
				.thenReturn(new ConcurrentValue(1L, TEST_NAME, BigDecimal.ONE, LocalDateTime.of(2026, 3, 3, 2, 56)));

		String json = objectMapper.writeValueAsString(new ConcurrentValueRequest(TEST_NAME, BigDecimal.ONE));

		mvc.perform(post(SOURCE_PATH).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(redirectedUrl(SOURCE_PATH + "/1")).andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value(TEST_NAME)).andExpect(jsonPath("$.amount").value(1))
				.andExpect(jsonPath("$.lastUpdateDate").value("2026-03-03T02:56:00"));

		validateVerifies(1);
	}

	@Test
	void shouldCreateListOfConcurrentValues() throws Exception {
		when(service.saveConcurrentValues(anyList())).then(returnsFirstArg());

		String json = objectMapper
				.writeValueAsString(Arrays.asList(new ConcurrentValueRequest(TEST_NAME, BigDecimal.ONE)));

		mvc.perform(post(SOURCE_PATH + "/batch").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(1));

		assertAll(() -> verify(service).saveConcurrentValues(anyList()), () -> verify(mapper).toModel(anyList()),
				() -> verify(mapper).toResponse(anyList()));
	}

	@ParameterizedTest
	@CsvSource(value = { "null, 1.01", " , 1.01", "name, null" }, nullValues = "null")
	void shouldReturnBadRequestWhenIsAnInvalidRequest(String name, BigDecimal amount) throws Exception {
		String json = objectMapper.writeValueAsString(new ConcurrentValueRequest(name, amount));

		mvc.perform(post(SOURCE_PATH).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());

		validateVerifies(0);
	}

	@Test
	void shouldReturnConcurrentValueWhenNameParamIsValid() throws Exception {
		when(service.getConcurrentValue(eq(TEST_NAME)))
				.thenReturn(new ConcurrentValue(1L, TEST_NAME, BigDecimal.ONE, LocalDateTime.of(2026, 3, 3, 2, 56)));

		mvc.perform(get(SOURCE_PATH + "/{name}", TEST_NAME).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.name").value(TEST_NAME))
				.andExpect(jsonPath("$.amount").value(1))
				.andExpect(jsonPath("$.lastUpdateDate").value("2026-03-03T02:56:00"));

		assertAll(() -> verify(service).getConcurrentValue(eq(TEST_NAME)),
				() -> verify(mapper).toResponse(any(ConcurrentValue.class)));
	}

	@Test
	void shouldReturnBadRequestWhenNameIsAnInvalidParam() throws Exception {
		when(service.getConcurrentValue(eq(TEST_NAME)))
				.thenThrow(new ResourceNotFoundException("test", "test", TEST_NAME));

		mvc.perform(get(SOURCE_PATH + "/{name}", TEST_NAME).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		assertAll(() -> verify(service).getConcurrentValue(eq(TEST_NAME)),
				() -> verify(mapper, times(0)).toResponse(any(ConcurrentValue.class)));
	}

	private void validateVerifies(int times) {
		assertAll(() -> verify(service, times(times)).saveConcurrentValue(any(ConcurrentValue.class)),
				() -> verify(mapper, times(times)).toModel(any(ConcurrentValueRequest.class)),
				() -> verify(mapper, times(times)).toResponse(any(ConcurrentValue.class)));
	}

}
