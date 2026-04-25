package com.trustamarket.inspectionservice.inspection.domain.model.vo;

import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionException;

import java.math.BigDecimal;
import java.util.Objects;

public record Money(BigDecimal amount, String currency) {

    public Money {
        Objects.requireNonNull(amount, "금액(amount)은 필수입니다");
        Objects.requireNonNull(currency, "통화(currency)는 필수입니다");
        if (amount.signum() < 0) {
            throw new InspectionException("금액(amount)은 음수일 수 없습니다");
        }
    }

    public static Money krw(long amount) {
        return new Money(BigDecimal.valueOf(amount), "KRW");
    }
}
