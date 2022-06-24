package org.fm.moneytransfer.controller;

import lombok.RequiredArgsConstructor;
import org.fm.moneytransfer.model.Account;
import org.fm.moneytransfer.service.AccountService;
import org.fm.moneytransfer.service.request.AccountCreateRequest;
import org.fm.moneytransfer.service.request.MoneyTransferRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> create(@RequestBody AccountCreateRequest request){
        return ResponseEntity.ok(accountService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAll(){
        return ResponseEntity.ok(accountService.getAll());
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(@RequestBody MoneyTransferRequest request){
        accountService.transferMoney(request);
        return ResponseEntity.ok("Money transferred");
    }
}
