package org.fm.moneytransfer.service.request;

import java.math.BigDecimal;

public record AccountCreateRequest(String accountNumber,
                                   String accountName,
                                   BigDecimal balance) {
}

