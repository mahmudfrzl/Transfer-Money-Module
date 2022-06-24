package org.fm.moneytransfer.service.request;

import java.math.BigDecimal;

public record MoneyTransferRequest(
        String fromAccountNumber,
        String toAccountNumber,
        BigDecimal amount
) {
}
