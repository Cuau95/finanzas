package com.cuau.finanzas.infrastructure.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.cuau.finanzas.domain.model.ConcurrentValue;
import com.cuau.finanzas.infrastructure.dto.ConcurrentValueRequest;
import com.cuau.finanzas.infrastructure.dto.ConcurrentValueResponse;

@Component
public class ConcurrentValueMapper {

	public ConcurrentValue toModel(ConcurrentValueRequest dto) {
		return new ConcurrentValue(null, dto.name(), dto.amount(), null);
	}

	public ConcurrentValueResponse toResponse(ConcurrentValue model) {
		return new ConcurrentValueResponse(model.getId(), model.getName(), model.getAmount(),
				model.getLastUpdateDate());
	}

	public List<ConcurrentValue> toModel(List<ConcurrentValueRequest> dtos) {
		return dtos.stream().map(this::toModel).toList();
	}

	public List<ConcurrentValueResponse> toResponse(List<ConcurrentValue> models) {
		return models.stream().map(this::toResponse).toList();
	}

}
