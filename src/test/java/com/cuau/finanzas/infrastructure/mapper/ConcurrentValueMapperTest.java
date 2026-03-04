package com.cuau.finanzas.infrastructure.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.cuau.finanzas.domain.model.ConcurrentValue;
import com.cuau.finanzas.infrastructure.dto.ConcurrentValueRequest;
import com.cuau.finanzas.infrastructure.dto.ConcurrentValueResponse;

public class ConcurrentValueMapperTest {

	private static final BigDecimal AMOUNT = BigDecimal.valueOf(100.01);
	private static final String NAME = "SomeFunnyName";

	private ConcurrentValue model = new ConcurrentValue(1L, NAME, AMOUNT, LocalDateTime.of(2026, 2, 23, 10, 0));

	private ConcurrentValueRequest dto = new ConcurrentValueRequest(NAME, AMOUNT);

	private ConcurrentValueMapper mapper = new ConcurrentValueMapper();

	@Test
	void shouldMapFromModelToDto() {
		ConcurrentValueResponse dtoMapped = assertDoesNotThrow(() -> mapper.toResponse(model));

		assertAll(() -> assertNotNull(dtoMapped), () -> assertEquals(model.getAmount(), dtoMapped.amount()),
				() -> assertEquals(model.getName(), dtoMapped.name()),
				() -> assertEquals(model.getLastUpdateDate(), dtoMapped.lastUpdateDate()),
				() -> assertEquals(model.getId(), dtoMapped.id()));
	}

	@Test
	void shouldMapFromDtoToModel() {
		ConcurrentValue modelMapped = assertDoesNotThrow(() -> mapper.toModel(dto));

		// Dto not to set ID and lastUpdateDate from request
		assertAll(() -> assertNotNull(modelMapped), () -> assertNull(modelMapped.getId()),
				() -> assertNull(modelMapped.getLastUpdateDate()),
				() -> assertEquals(dto.name(), modelMapped.getName()),
				() -> assertEquals(dto.amount(), modelMapped.getAmount()));
	}

	@Test
	void shouldMapListFromModelToResponse() {
		List<ConcurrentValue> models = List.of(model);

		List<ConcurrentValueResponse> result = mapper.toResponse(models);

		assertAll(() -> assertEquals(1, result.size()), () -> assertEquals(model.getId(), result.get(0).id()));
	}

	@Test
	void shouldMapListFromRequestToModel() {
		List<ConcurrentValueRequest> requests = List.of(dto);

		List<ConcurrentValue> result = mapper.toModel(requests);

		assertAll(() -> assertEquals(1, result.size()), () -> assertEquals(dto.name(), result.get(0).getName()));
	}

}
