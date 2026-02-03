package com.neurodiversidad.neurodiversidad_backend.finance;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FinSummaryDto {

    private BigDecimal totalIncome;
    private BigDecimal totalOutcome;
    private BigDecimal balance;
}
