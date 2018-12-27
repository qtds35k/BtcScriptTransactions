package edu.nyu.crypto.csci3033;

import java.io.File;
import java.util.List;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.nyu.crypto.csci3033.transactions.*;
import junit.framework.Assert;

/**
 * Created by bbuenz on 23.09.15.
 */
public class ScriptTests {
	// TODO: Change this to true to use mainnet.
	private boolean useMainNet = true;
	// TODO: Change this to the address of the testnet faucet you use.
	private static final String faucetAddress = "mv4rnyY3Su5gjcDNzbMLKBQkBicCtHUtFB";

	private String wallet_name;
	private NetworkParameters networkParameters;
	private WalletAppKit kit;

	private static final Logger LOGGER = LoggerFactory.getLogger(ScriptTests.class);

	public ScriptTests() {
		if (useMainNet) {
			networkParameters = new MainNetParams();
			wallet_name = "main-wallet";
			LOGGER.info("Running on mainnet.");
		} else {
			networkParameters = new TestNet3Params();
			wallet_name = "test-wallet";
//            wallet_name = "test-wallet2";
			LOGGER.info("Running on testnet.");
		}
		kit = new WalletAppKit(networkParameters, new File(wallet_name), "password");
	}

	public void downloadBlockchain() {
		LOGGER.info("Starting to sync blockchain. This might take a few minutes");
		kit.setAutoSave(true);
		kit.startAsync();
		kit.awaitRunning();
		kit.wallet().allowSpendingUnconfirmedTransactions();
		LOGGER.info("Synced blockchain.");
		LOGGER.info("You've got " + kit.wallet().getBalance() + " in your pocket");
	}

	@Test
	public void printAddress() {
		downloadBlockchain();
		LOGGER.info("Your address is {}", kit.wallet().currentReceiveAddress());

//		ECKey wuKey;
//		String privateKey = "5KMmMQVheAiGhRU5yS6dvqSeBnP7LrFfzim6hFHKrUnyBXAsC3r";
//		DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(networkParameters, privateKey);
//		wuKey = dumpedPrivateKey.getKey();
//		kit.wallet().importKey(wuKey);
//		LOGGER.info("main Key imported");

//		ECKey testKey = new ECKey();
//		kit.wallet().importKey(testKey);
//		LOGGER.info("test Key imported");

		List<ECKey> list = kit.wallet().getImportedKeys();
		if (list.isEmpty())
			LOGGER.info("No imported keys");
		else {
			ECKey wuKey = list.get(0);
			LOGGER.info("PrivateKey: " + wuKey.getPrivateKeyAsWiF(networkParameters));
			LOGGER.info("PublicKey: " + wuKey.getPublicKeyAsHex());
			LOGGER.info("wuKey Address: " + wuKey.toAddress(networkParameters).toString());
		}
		kit.stopAsync();
		kit.awaitTerminated();
	}

	private void testTransaction(ScriptTransaction scriptTransaction) throws InsufficientMoneyException {
		final Script inputScript = scriptTransaction.createInputScript();
		Transaction transaction = scriptTransaction.createOutgoingTransaction(inputScript, Coin.valueOf(2700)); // 20000
		TransactionOutput relevantOutput = transaction.getOutputs().stream()
				.filter(to -> to.getScriptPubKey().equals(inputScript)).findAny().get();
		Transaction redemptionTransaction = scriptTransaction.createUnsignedRedemptionTransaction(relevantOutput,
				scriptTransaction.getReceiveAddress());
		Script redeemScript = scriptTransaction.createRedemptionScript(redemptionTransaction);
		scriptTransaction.testScript(inputScript, redeemScript, redemptionTransaction);
		redemptionTransaction.getInput(0).setScriptSig(redeemScript);
		scriptTransaction.sendTransaction(transaction);
		scriptTransaction.sendTransaction(redemptionTransaction);
	}

	// TODO: Uncomment this once you have coins on mainnet or testnet to check that
	// transactions are working as expected.
//    @Test
//    public void testPayToPubKey() throws InsufficientMoneyException {
//        try (ScriptTransaction payToPubKey = new PayToPubKey(networkParameters, new File(wallet_name), "password")) {
//            testTransaction(payToPubKey);
//
//       } catch (Exception e) {
//            e.printStackTrace();
//            Assert.fail(e.getMessage());
//        }
//    }

	// TODO: Uncomment this when you are ready to test PayToPubKeyHash.
	@Test
	public void testPayToPubKeyHash() throws InsufficientMoneyException {
//		downloadBlockchain();
//		LOGGER.info(kit.wallet().getImportedKeys().toString());
//		List<ECKey> list = kit.wallet().getImportedKeys();
//		
//		if (list.isEmpty())
//			LOGGER.info("No imported keys");
//		else
//			for (ECKey importedKey : list) {
//				LOGGER.info("PrivateKey: " + importedKey.getPrivateKeyAsWiF(networkParameters));
//				LOGGER.info("PublicKey: " + importedKey.getPublicKeyAsHex());
//				LOGGER.info("Address: " + importedKey.toAddress(networkParameters).toString());
//			}
//		ECKey wuKey = list.get(0);
		try (ScriptTransaction payToPubKeyHash = new PayToPubKeyHash(networkParameters, new File(wallet_name), "password")) {
			LOGGER.info("Confirm information");
//			LOGGER.info("PrivateKey: " + wuKey.getPrivateKeyAsWiF(networkParameters));
//			LOGGER.info("PublicKey: " + wuKey.getPublicKeyAsHex());
//			LOGGER.info("Address: " + wuKey.toAddress(networkParameters).toString());
			LOGGER.info("Ready to test payToPubKeyHash");
			testTransaction(payToPubKeyHash);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	// TODO: Uncomment this when you are ready to test LinearEquationTransaction.
//    @Test
//    public void testLinearEquation() throws InsufficientMoneyException {
//        try (LinearEquationTransaction linEq = new LinearEquationTransaction(networkParameters, new File(wallet_name), "password")) {
//            testTransaction(linEq);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Assert.fail(e.getMessage());
//        }
//    }

	// TODO: Uncomment this when you are ready to test MultiSigTransaction.
//    @Test
//    public void testMultiSig() throws InsufficientMoneyException {
//        try (ScriptTransaction multiSig = new MultiSigTransaction(networkParameters, new File(wallet_name), "password")) {
//            testTransaction(multiSig);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Assert.fail(e.getMessage());
//        }
//    }

	// TODO: Uncomment this when you are ready to send money back to Faucet on
	// testnet.
//    @Test
//    public void sendMoneyBackToFaucet() throws AddressFormatException, InsufficientMoneyException {
//        if (useMainNet) {
//            return;
//        }
//        downloadBlockchain();
//        Transaction transaction = kit.wallet().createSend(new Address(networkParameters, faucetAddress), kit.wallet().getBalance().subtract(Coin.MILLICOIN));
//        kit.wallet().commitTx(transaction);
//        kit.peerGroup().broadcastTransaction(transaction);
//        kit.stopAsync();
//        kit.awaitTerminated();
//    }
}
