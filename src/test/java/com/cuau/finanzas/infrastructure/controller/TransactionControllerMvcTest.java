package com.cuau.finanzas.infrastructure.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.cuau.finanzas.domain.enums.CreditType;
import com.cuau.finanzas.domain.enums.CronologyType;
import com.cuau.finanzas.domain.enums.DebitType;
import com.cuau.finanzas.domain.exception.ResourceNotFoundException;
import com.cuau.finanzas.domain.model.CreditTransaction;
import com.cuau.finanzas.domain.model.DebitTransaction;
import com.cuau.finanzas.domain.model.Transaction;
import com.cuau.finanzas.domain.service.TransactionService;
import com.cuau.finanzas.infrastructure.dto.CreditTransactionDto;
import com.cuau.finanzas.infrastructure.dto.DebitTransactionDto;
import com.cuau.finanzas.infrastructure.dto.TransactionDto;
import com.cuau.finanzas.infrastructure.mapper.TransactionMapper;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(value = TransactionController.class)
public class TransactionControllerMvcTest {

	private static final String SOURCE_PATH = "/transactions";

	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private TransactionService service;
	@MockitoBean
	private TransactionMapper mapper;

	@Test
	void shouldCreateCreditTransactionWhenDtoIsValid() throws Exception {
		when(mapper.modelFrom(any(TransactionDto.class))).thenReturn(new CreditTransaction());
		when(service.saveTransaction(any(Transaction.class))).then(returnsFirstArg());
		when(mapper.dtoFrom(any(Transaction.class))).thenReturn(buildCreditDto(1L, CronologyType.ACTUAL, 1, 1));

		String json = objectMapper.writeValueAsString(buildCreditDto(null, CronologyType.ACTUAL, 1, 1));

		mvc.perform(post(SOURCE_PATH).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Credit test")).andExpect(jsonPath("$.amount").value(1))
				.andExpect(jsonPath("$.cronologyType").value("ACTUAL"))
				.andExpect(jsonPath("$.date").value("2026-03-02")).andExpect(jsonPath("$.creditType").value("EXPENSE"))
				.andExpect(jsonPath("$.numberActualPayment").value(1)).andExpect(jsonPath("$.totalPayments").value(1))
				.andExpect(jsonPath("$.type").value("CREDIT"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

		validateVerifiesCreateTransactionProcess(1);
	}

	@Test
	void shouldCreateDebitTransactionWhenDtoIsValid() throws Exception {
		when(mapper.modelFrom(any(TransactionDto.class))).thenReturn(new DebitTransaction());
		when(service.saveTransaction(any(Transaction.class))).then(returnsFirstArg());
		when(mapper.dtoFrom(any(Transaction.class))).thenReturn(buildDebitDto(1L, CronologyType.ACTUAL));

		String json = objectMapper.writeValueAsString(buildDebitDto(null, CronologyType.ACTUAL));

		mvc.perform(post(SOURCE_PATH).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Debit Test")).andExpect(jsonPath("$.amount").value(1))
				.andExpect(jsonPath("$.cronologyType").value("ACTUAL"))
				.andExpect(jsonPath("$.date").value("2026-03-02")).andExpect(jsonPath("$.debitType").value("INCOME"))
				.andExpect(jsonPath("$.type").value("DEBIT"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

		validateVerifiesCreateTransactionProcess(1);
	}

	@Test
	void shouldCreateTransactionList() throws Exception {
		List<TransactionDto> dtos = createTransactionDtoList();
		when(mapper.modelFrom((anyList()))).thenReturn(new ArrayList<Transaction>());
		when(service.saveTransactions(anyList())).then(returnsFirstArg());
		when(mapper.dtoFrom(anyList())).thenReturn(dtos);

		String json = objectMapper.writerFor(new TypeReference<List<TransactionDto>>() {
		}).writeValueAsString(dtos);

		mvc.perform(post(SOURCE_PATH + "/batch").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(dtos.size()));

		assertAll(() -> verify(mapper).modelFrom(anyList()), () -> verify(service).saveTransactions(anyList()),
				() -> verify(mapper).dtoFrom(anyList()), () -> verifyNoMoreInteractions(mapper, service));
	}

	@ParameterizedTest
	@CsvSource(value = { "null, 1", "1, null", "2, 1", "0, 1", "1, 0", "1, -1", "-1, 1" }, nullValues = "null")
	void shouldReturnBadRequestWhenNumberPaymentRulesAreMissed(Integer numberActual, Integer total) throws Exception {
		CreditTransactionDto dto = buildCreditDto(1L, CronologyType.ACTUAL, numberActual, total);
		String json = objectMapper.writeValueAsString(dto);

		mvc.perform(post(SOURCE_PATH).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());

		validateVerifiesCreateTransactionProcess(0);
	}

	@ParameterizedTest
	@MethodSource("provideTransactions")
	void shouldReturnBadRequestWhenCronologyTypeIsNotActual(TransactionDto dto) throws Exception {
		String json = objectMapper.writeValueAsString(dto);

		mvc.perform(post(SOURCE_PATH).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());

		validateVerifiesCreateTransactionProcess(0);
	}

	@Test
	void shouldGetCreditTransactionById() throws Exception {
		when(service.getTransaction(anyLong())).thenReturn(new CreditTransaction());
		when(mapper.dtoFrom(any(Transaction.class))).thenReturn(buildCreditDto(1L, CronologyType.ACTUAL, 1, 1));

		mvc.perform(get(SOURCE_PATH + "/{id}", "1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.name").value("Credit test"))
				.andExpect(jsonPath("$.amount").value(1)).andExpect(jsonPath("$.cronologyType").value("ACTUAL"))
				.andExpect(jsonPath("$.date").value("2026-03-02")).andExpect(jsonPath("$.creditType").value("EXPENSE"))
				.andExpect(jsonPath("$.numberActualPayment").value(1)).andExpect(jsonPath("$.totalPayments").value(1))
				.andExpect(jsonPath("$.type").value("CREDIT"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	void shouldGetDebitTransactionById() throws Exception {
		when(service.getTransaction(anyLong())).thenReturn(new DebitTransaction());
		when(mapper.dtoFrom(any(Transaction.class))).thenReturn(buildDebitDto(1L, CronologyType.ACTUAL));

		mvc.perform(get(SOURCE_PATH + "/{id}", "1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.name").value("Debit Test"))
				.andExpect(jsonPath("$.amount").value(1)).andExpect(jsonPath("$.cronologyType").value("ACTUAL"))
				.andExpect(jsonPath("$.date").value("2026-03-02")).andExpect(jsonPath("$.debitType").value("INCOME"))
				.andExpect(jsonPath("$.type").value("DEBIT"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	void shouldReturnNotFoundWhenIdIsNotValid() throws Exception {
		when(service.getTransaction(anyLong())).thenThrow(new ResourceNotFoundException("transaction", "id", "1"));

		mvc.perform(get(SOURCE_PATH + "/{id}", "1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	void shouldReturnLatestTransactions() throws Exception {
		List<TransactionDto> dtos = createTransactionDtoList();
		when(service.getLatestTransactions()).thenReturn(new ArrayList<Transaction>());
		when(mapper.dtoFrom(anyList())).thenReturn(dtos);

		mvc.perform(get(SOURCE_PATH + "/latest").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(dtos.size()));
	}

	@Test
	void shouldReturnInternalServerErrorStatusWhenGetLatestTransactionsThrowsDataAccessException() throws Exception {
		when(service.getLatestTransactions()).thenThrow(new DataAccessResourceFailureException("DB unavailable"));

		mvc.perform(get(SOURCE_PATH + "/latest").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

		verify(mapper, times(0)).dtoFrom(anyList());
	}

	@Test
	void shouldReturnLatestCreditTransactions() throws Exception {
		List<TransactionDto> creditDtos = new ArrayList<>();
		creditDtos.add(buildCreditDto(1L, CronologyType.ACTUAL, null, null));
		when(service.getLatestCreditTransactions()).thenReturn(new ArrayList<CreditTransaction>());
		when(mapper.dtoFrom(anyList())).thenReturn(creditDtos);

		mvc.perform(get(SOURCE_PATH + "/credit/latest").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(creditDtos.size()));
	}

	@Test
	void shouldReturnInternalServerErrorStatusWhenGetLatestCreditTransactionsThrowsDataAccessException()
			throws Exception {
		when(service.getLatestCreditTransactions()).thenThrow(new DataAccessResourceFailureException("DB unavailable"));

		mvc.perform(get(SOURCE_PATH + "/credit/latest").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

		verify(mapper, times(0)).dtoFrom(anyList());
	}

	@Test
	void shouldReturnLatestDebitTransactions() throws Exception {
		List<TransactionDto> debitDtos = new ArrayList<>();
		debitDtos.add(buildDebitDto(null, CronologyType.CALCULATED));
		when(service.getLatestDebitTransactions()).thenReturn(new ArrayList<DebitTransaction>());
		when(mapper.dtoFrom(anyList())).thenReturn(debitDtos);

		mvc.perform(get(SOURCE_PATH + "/debit/latest").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(debitDtos.size()));
	}

	@Test
	void shouldReturnInternalServerErrorStatusWhenGetLatestDebitTransactionsThrowsDataAccessException()
			throws Exception {
		when(service.getLatestDebitTransactions()).thenThrow(new DataAccessResourceFailureException("DB unavailable"));

		mvc.perform(get(SOURCE_PATH + "/debit/latest").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

		verify(mapper, times(0)).dtoFrom(anyList());
	}

	private void validateVerifiesCreateTransactionProcess(int times) {
		assertAll(() -> verify(service, times(times)).saveTransaction(any(Transaction.class)),
				() -> verify(mapper, times(times)).modelFrom(any(TransactionDto.class)),
				() -> verify(mapper, times(times)).dtoFrom(any(Transaction.class)),
				() -> verifyNoMoreInteractions(mapper, service));
	}

	private static Stream<TransactionDto> provideTransactions() {
		return Stream.of(buildCreditDto(1L, CronologyType.CALCULATED, null, null),
				buildDebitDto(null, CronologyType.CALCULATED));
	}

	private List<TransactionDto> createTransactionDtoList() {
		List<TransactionDto> dtos = new ArrayList<>();
		dtos.add(buildCreditDto(1L, CronologyType.ACTUAL, null, null));
		dtos.add(buildDebitDto(1L, CronologyType.ACTUAL));
		return dtos;
	}

	private static CreditTransactionDto buildCreditDto(Long id, CronologyType type, Integer numberActualPayments,
			Integer totalPayments) {
		CreditTransactionDto dto = new CreditTransactionDto();
		dto.setId(id);
		dto.setName("Credit test");
		dto.setCronologyType(type);
		dto.setAmount(BigDecimal.ONE);
		dto.setDate(LocalDate.of(2026, 3, 2));
		dto.setCreditType(CreditType.EXPENSE);
		dto.setInterest(null);
		dto.setNumberActualPayment(numberActualPayments);
		dto.setTotalPayments(totalPayments);
		return dto;
	}

	private static DebitTransactionDto buildDebitDto(Long id, CronologyType type) {
		DebitTransactionDto dto = new DebitTransactionDto();
		dto.setId(id);
		dto.setAmount(BigDecimal.ONE);
		dto.setCronologyType(type);
		dto.setDate(LocalDate.of(2026, 3, 2));
		dto.setName("Debit Test");
		dto.setDebitType(DebitType.INCOME);
		return dto;
	}

}
