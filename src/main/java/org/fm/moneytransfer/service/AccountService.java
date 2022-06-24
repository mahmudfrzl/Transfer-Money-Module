package org.fm.moneytransfer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fm.moneytransfer.model.Account;
import org.fm.moneytransfer.repository.AccountRepository;
import org.fm.moneytransfer.service.request.AccountCreateRequest;
import org.fm.moneytransfer.service.request.MoneyTransferRequest;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final AmqpTemplate rabbitMqTemplate;
    private final DirectExchange directExchange;
    @Value("${rabbitmq.queue}")
    private String queueName;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    public Account create(AccountCreateRequest request){
        return accountRepository.save(Account.builder()
                .accountName(request.accountName())
                .accountNumber(request.accountNumber())
                .balance(request.balance())
                .build());
    }

    public List<Account> getAll(){
        return accountRepository.findAll();
    }


    public void transferMoney(MoneyTransferRequest request){
        rabbitMqTemplate.convertAndSend(directExchange.getName(), routingKey, request);
    }

    @RabbitListener(queues = "${rabbitmq.queue}")
    public void sendAmount(MoneyTransferRequest request){
        final var account = accountRepository.findByAccountNumber(request.fromAccountNumber());
        account.ifPresentOrElse(
                (account1) ->{
                    if (account1.getBalance().compareTo(request.amount()) >= 0){
                        account1.setBalance(account1.getBalance().subtract(request.amount()));
                        accountRepository.save(account1);
                        rabbitMqTemplate.convertAndSend(directExchange.getName(), "second-route", request);
                    } else {
                        log.error("Not enough money in account");
                    }
                },
                () -> System.out.println("Account not found")
        );
    }

    @RabbitListener(queues ="transfer-step-2")
    public void receiveAmount(MoneyTransferRequest request){
        final var account = accountRepository.findByAccountNumber(request.toAccountNumber());
        account.ifPresentOrElse(
                (account1) ->{
                    account1.setBalance(account1.getBalance().add(request.amount()));
                    accountRepository.save(account1);
                    rabbitMqTemplate.convertAndSend(directExchange.getName(), "third-route", request);
                },
                () -> {
                    System.out.println("Account not found");
                    var fromAccount = accountRepository.findByAccountNumber(request.fromAccountNumber());
                    fromAccount.ifPresentOrElse(
                            (account1) -> {
                                account1.setBalance(account1.getBalance().add(request.amount()));
                                accountRepository.save(account1);
                            },
                            () -> System.out.println("Account not found")
                    );
                }

        );
    }

    @RabbitListener(queues ="transfer-step-3")
    public void finalizeAmount(MoneyTransferRequest request){
        String notification = "Transfer from " + request.fromAccountNumber() + " to " + request.toAccountNumber() + " amount " + request.amount();
        System.out.println(notification);
    }







}
