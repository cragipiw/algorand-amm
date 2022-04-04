package ru.test.demo.service;

import com.algorand.algosdk.v2.client.common.AlgodClient;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.test.demo.config.AlgorandCustomProperty;

@Service
@RequiredArgsConstructor
public class AlgoWebClientProvider {

  private final AlgorandCustomProperty algorandCustomProperty;

  private static AlgodClient client;

  public static AlgodClient getClient() {
    return client;
  }

  @PostConstruct
  private void init() {
    client = new AlgodClient(
        algorandCustomProperty.getHost(),
        algorandCustomProperty.getPort(),
        algorandCustomProperty.getToken()
    );
  }

}
