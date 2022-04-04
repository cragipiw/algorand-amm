package ru.test.demo.service;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Ed25519PublicKey;
import com.algorand.algosdk.crypto.MultisigAddress;
import com.algorand.algosdk.v2.client.common.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class AccountStorage {

  private static Account firstAccount;
  private static Account secondAccount;
  private static MultisigAddress multisigAccount;

  @PostConstruct
  @SneakyThrows
  private void initAccounts() {
    firstAccount = createSimpleAccount();
    secondAccount = createSimpleAccount();
    multisigAccount = createMultisigAccount(firstAccount, secondAccount);
  }

  public Account getFistAccount() {
    return firstAccount;
  }

  public Account getSecondAccount() {
    return secondAccount;
  }

  public MultisigAddress getMultisigAccount() {
    return multisigAccount;
  }

  @SneakyThrows
  public Account createSimpleAccount() {
    Account myAccount = new Account();
    log.info("Simple account has been created. Address {}, link {}, passphrase {}",
        myAccount.getAddress(),
        String.format("https://dispenser.testnet.aws.algodev.network?account=%s", myAccount.getAddress().toString()),
        myAccount.toMnemonic()
    );
    return myAccount;
  }

  @SneakyThrows
  public static MultisigAddress createMultisigAccount(Account... accounts) {
    List<Ed25519PublicKey> publicKeys = Arrays.stream(accounts)
        .map(Account::getEd25519PublicKey)
        .collect(Collectors.toList());
    MultisigAddress multisigAccount = new MultisigAddress(1, 2, publicKeys);
    log.info("Multisig account has been created with address {}", multisigAccount.toAddress());
    return multisigAccount;
  }

  @SneakyThrows
  public Response<com.algorand.algosdk.v2.client.model.Account> getBalanceByAddress(Address address) {
    var response = AlgoWebClientProvider.getClient().AccountInformation(address).execute();
    if (!response.isSuccessful()) {
      throw new IllegalArgumentException("Getting account's balance error: " + response.message());
    }
    return response;
  }

  @SneakyThrows
  public Response<com.algorand.algosdk.v2.client.model.Account> getBalanceByAccount(Account account) {
    var response = AlgoWebClientProvider.getClient().AccountInformation(account.getAddress()).execute();
    if (!response.isSuccessful()) {
      throw new IllegalArgumentException("Getting account's balance error: " + response.message());
    }
    return response;
  }

}
