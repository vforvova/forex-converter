package com.forexconverter.conversion;

import java.math.BigDecimal;

public record SuccessResponseDTO(BigDecimal result) implements ResponseDTO {}
