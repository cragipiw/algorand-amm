package ru.test.demo.controller;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.test.demo.service.AccountStorage;
import ru.test.demo.service.TransactionSender;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class AlgoController {

  AccountStorage accountStorage;
  TransactionSender transactionSender;

  @GetMapping("/accounts/{accountAddress}")
  @SneakyThrows
  public String getBalanceByAddress(@PathVariable String accountAddress) {
    return accountStorage.getBalanceByAddress(new Address(accountAddress)).toString();
  }

  @GetMapping("/first-account-balance")
  public String getFirstAccountBalance() {
    Account account = accountStorage.getFistAccount();
    return accountStorage.getBalanceByAccount(account).toString();
  }

  @PostMapping("/send-money-from-multisin-account")
  public void sendMoneyFromMultiSigAccount() {
    String receiverId = "BMJBQCZHVWPG7RX43HLAY2V6M4O7RXH3PM32LG2GGJPT2ALJ5444LD5BYA";

    var multisigAccount = accountStorage.getMultisigAccount();
    log.info("First account balance before transaction: {}", accountStorage.getBalanceByAccount(accountStorage.getFistAccount()));
    transactionSender.sendMultiSigTransaction(multisigAccount, receiverId);
    log.info("First account balance after transaction: {}", accountStorage.getBalanceByAccount(accountStorage.getFistAccount()));
  }

}


