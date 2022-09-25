package org.learning.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;

@Slf4j
public class Web3jService {

//    private static Logger log = LoggerFactory.getLogger(Web3jService.class.getName());
    public Web3jService(String urlHttpService) {
        this.web3j = Web3j.build(new HttpService(urlHttpService));
    }

    private Web3j web3j;

    public void getClientVersion() throws IOException {
        log.error(web3j.web3ClientVersion().send().getWeb3ClientVersion());
    }

    public EthFilter getEthFilterRequest(BigInteger startBlock, BigInteger endBlock, String contractAddress) {

        return new EthFilter(new DefaultBlockParameterNumber(startBlock),
                new DefaultBlockParameterNumber(endBlock),
                contractAddress);
    }
}