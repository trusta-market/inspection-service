package com.trustamarket.inspectionservice.inspection.domain.vo;

import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionException;
import com.trustamarket.inspectionservice.inspection.domain.enums.CurrencyCode;

import java.math.BigDecimal;
import java.util.Objects;

public record Money(BigDecimal amount, CurrencyCode currency) {

    public Money {
        Objects.requireNonNull(amount, "금액(amount)은 필수입니다");
        Objects.requireNonNull(currency, "통화(currency)는 필수입니다");
        if (amount.signum() < 0) {
            throw new InspectionException("금액(amount)은 음수일 수 없습니다");
        }
    }

    public static Money krw(long amount) {
        return new Money(BigDecimal.valueOf(amount), CurrencyCode.KRW);
    }
}
