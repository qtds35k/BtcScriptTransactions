package edu.nyu.crypto.csci3033.transactions;

import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import java.io.File;
import java.util.List;

import static org.bitcoinj.script.ScriptOpCodes.*;

/**
 * Created by bbuenz on 24.09.15.
 */
public class PayToPubKeyHash extends ScriptTransaction {
    // TODO: Problem 1
	private ECKey key;
//	private WalletAppKit kit;
//	private ECKey key;

    public PayToPubKeyHash(NetworkParameters parameters, File file, String password, ECKey privateKey) {
        super(parameters, file, password);
        this.key = privateKey;
    }
    
    // original constructor
    public PayToPubKeyHash(NetworkParameters parameters, File file, String password) {
            super(parameters, file, password);
    		String privateKey = "5KMmMQVheAiGhRU5yS6dvqSeBnP7LrFfzim6hFHKrUnyBXAsC3r";
//    		String privateKey = "cQAuHGfWEBZYC4QbgSQ1pSWunt6DdkzCF9V3r9E2vz1FniGeNC5u";
    		DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(parameters, privateKey);
    		key = dumpedPrivateKey.getKey();
//            key = getWallet().freshReceiveKey();
//            kit = new WalletAppKit(parameters, new File("test-wallet"), "password");
//            List<ECKey> list = kit.wallet().getImportedKeys();
//    		ECKey wuKey = list.get(0);
    		System.out.println("confirm wuKey Address: " + key.toAddress(parameters).toString());
        }

    @Override
    public Script createInputScript() {
        // TODO: Create a P2PKH script
        // TODO: be sure to test this script on the mainnet using a vanity address
        ScriptBuilder builder = new ScriptBuilder();
        builder.op(OP_DUP);
        builder.op(OP_HASH160);
        builder.data(key.getPubKeyHash());
        builder.op(OP_EQUALVERIFY);
        builder.op(OP_CHECKSIG);
        return builder.build();
    }

    @Override
    public Script createRedemptionScript(Transaction unsignedTransaction) {
        // TODO: Redeem the P2PKH transaction
        TransactionSignature txSig = sign(unsignedTransaction, key);

        ScriptBuilder builder = new ScriptBuilder();
        builder.data(txSig.encodeToBitcoin());
        builder.data(key.getPubKey());
        return builder.build();
    }
}
