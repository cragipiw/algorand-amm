package ru.test.demo.service;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.MultisigAddress;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import com.algorand.algosdk.v2.client.model.PostTransactionsResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class TransactionSender {

  AccountStorage accountManager;

  @SneakyThrows
  public void sendMultiSigTransaction(MultisigAddress multisigAccount, String receiverId) {
    var transaction = createTransaction(5, multisigAccount.toAddress(), receiverId);
    var signedTransaction = accountManager.getFistAccount().signMultisigTransaction(multisigAccount, transaction);
    var appendedTransaction = accountManager.getSecondAccount().appendMultisigTransaction(multisigAccount, signedTransaction);
    submitTransaction(appendedTransaction);
  }

  @SneakyThrows
  private Transaction createTransaction(int amount, Address senderAddress, String receiverId) {
    var response = AlgoWebClientProvider.getClient().TransactionParams().execute().body();
    return Transaction.PaymentTransactionBuilder()
        .receiver(receiverId)
        .sender(senderAddress)
        .amount(amount)
        .note("Note".getBytes())
        .suggestedParams(response)
        .build();
  }

  @SneakyThrows
  private void submitTransaction(SignedTransaction signedTransaction) {
    byte[] encodedTxBytes = Encoder.encodeToMsgPack(signedTransaction);
    Response<PostTransactionsResponse> result = AlgoWebClientProvider.getClient().RawTransaction().rawtxn(encodedTxBytes).execute();
    if (!result.isSuccessful()) {
      throw new IllegalArgumentException("Transaction error: " + result.message());
    }
    String id = result.body().txId;
    waitForConfirmation(id);
    PendingTransactionResponse pendingTransactionResponse = AlgoWebClientProvider.getClient().PendingTransactionInformation(id).execute().body();
    log.info("Transaction information (with notes): " + pendingTransactionResponse.toString());
  }

  @SneakyThrows
  public void waitForConfirmation(String txID) {
    Long lastRound = AlgoWebClientProvider.getClient().GetStatus().execute().body().lastRound;
    while (true) {
      try {
        // Check the pending tranactions
        Response<PendingTransactionResponse> pendingInfo = AlgoWebClientProvider.getClient().PendingTransactionInformation(txID).execute();
        if (pendingInfo.body().confirmedRound != null && pendingInfo.body().confirmedRound > 0) {
          // Got the completed Transaction
          log.info("Transaction " + txID + " confirmed in round " + pendingInfo.body().confirmedRound);
          break;
        }
        lastRound++;
        AlgoWebClientProvider.getClient().WaitForBlock(lastRound).execute();
      } catch (Exception e) {
        throw e;
      }
    }

  }

}
