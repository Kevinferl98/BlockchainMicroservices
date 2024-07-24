package com.project.consumer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;


@Service
public class EthereumService {

    private final Web3j web3j;

    @Value("${ethereum.privatekey.value}")
    private String privateKey;

    private static final Logger log = LoggerFactory.getLogger(EthereumService.class);

    public EthereumService(Web3j web3j) {
        this.web3j = web3j;
    }

    public void sendTransaction(String hash) {
        try {
            Credentials credentials = Credentials.create(privateKey);

            BigInteger balance = getBalance(credentials.getAddress());
            BigInteger gasPrice = getGasPrice();
            BigInteger gasLimit = BigInteger.valueOf(5_000_000);
            BigInteger transactionCost = gasPrice.multiply(gasLimit);

            log.info("Gas price: {}", gasPrice);
            log.info("Balance: {}", balance);

            if (isBalanceSufficient(balance, transactionCost)) {
                BigInteger nonce = getNonce(credentials.getAddress());
                RawTransaction rawTransaction = createRawTransaction(nonce, gasPrice, gasLimit, hash);
                String signedTransaction = signTransaction(rawTransaction, credentials);
                sendSignedTransaction(signedTransaction);
            }
        } catch (Exception e) {
            log.error("Error while sending transaction: {}", e.getMessage());
        }
    }

    public BigInteger getBalance(String address) throws IOException {
        EthGetBalance ethGetBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
        return ethGetBalance.getBalance();
    }

    public BigInteger getGasPrice() throws IOException {
        EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
        return ethGasPrice.getGasPrice();
    }

    private boolean isBalanceSufficient(BigInteger balance, BigInteger transactionCost) {
        if (balance.compareTo(transactionCost) < 0) {
            log.error("Insufficient balance to send transaction");
            return false;
        }
        return true;
    }

    private BigInteger getNonce(String address) throws ExecutionException, InterruptedException {
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                address, DefaultBlockParameterName.LATEST).sendAsync().get();
        return ethGetTransactionCount.getTransactionCount();
    }

    private RawTransaction createRawTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String hash) {
        return RawTransaction.createTransaction(
                nonce,
                gasPrice,
                gasLimit,
                "0x0",
                "0x" + hash
        );
    }

    private String signTransaction(RawTransaction rawTransaction, Credentials credentials) {
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        return Numeric.toHexString(signedMessage);
    }

    private void sendSignedTransaction(String hexValue) throws IOException, InterruptedException {
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();

        if (ethSendTransaction.hasError()) {
            log.error("Error while sending: {}", ethSendTransaction.getError().getMessage());
        } else {
            String transactionHash = ethSendTransaction.getTransactionHash();
            log.info("Transaction sent successfully, transaction hash: {}", transactionHash);
            waitForReceipt(transactionHash);
        }
    }

    private void waitForReceipt(String transactionHash) throws IOException, InterruptedException {
        long startTime = System.currentTimeMillis();
        TransactionReceipt receipt = getTransactionReceipt(transactionHash, 10000);

        if (receipt == null) {
            log.error("Unable to receive receipt");
        } else {
            long endTime = System.currentTimeMillis();
            log.info("Block written, wait time: {} ms", endTime - startTime);
        }
    }

    private TransactionReceipt getTransactionReceipt(String transactionHash, long timeoutMills) throws IOException, InterruptedException {
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeoutMills) {
            TransactionReceipt receipt = web3j.ethGetTransactionReceipt(transactionHash).send().getResult();
            if (receipt != null) {
                return receipt;
            }
            Thread.sleep(1000);
        }
        return null;
    }
}
