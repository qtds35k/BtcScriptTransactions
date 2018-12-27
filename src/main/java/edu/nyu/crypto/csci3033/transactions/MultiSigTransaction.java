package edu.nyu.crypto.csci3033.transactions;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import java.io.File;
import java.math.BigInteger;

import static org.bitcoinj.script.ScriptOpCodes.*;

/**
 * Created by bbuenz on 24.09.15.
 */
public class MultiSigTransaction extends ScriptTransaction {
    // TODO: Problem 3
	private DeterministicKey keyBank;
    private DeterministicKey keyCustomer1;
    private DeterministicKey keyCustomer2;
    private DeterministicKey keyCustomer3;


    public MultiSigTransaction(NetworkParameters parameters, File file, String password) {
        super(parameters, file, password);
        keyBank = getWallet().freshReceiveKey();
        keyCustomer1 = getWallet().freshReceiveKey();
        keyCustomer2 = getWallet().freshReceiveKey();
        keyCustomer3 = getWallet().freshReceiveKey();
    }

    @Override
    public Script createInputScript() {
        // TODO: Create a script that can be spend using signatures from the bank and one of the customers
        ScriptBuilder builder = new ScriptBuilder();
        builder.data(keyBank.getPubKey());
        builder.op(OP_CHECKSIGVERIFY);
        builder.op(OP_1);
        builder.data(keyCustomer1.getPubKey());
        builder.data(keyCustomer2.getPubKey());
        builder.data(keyCustomer3.getPubKey());
        builder.op(OP_3);
        builder.op(OP_CHECKMULTISIG);

        return builder.build();
    }

    @Override
    public Script createRedemptionScript(Transaction unsignedTransaction) {
        // Please be aware of the CHECK_MULTISIG bug!
        // TODO: Create a spending script
        TransactionSignature txSigBank = sign(unsignedTransaction, keyBank);
        TransactionSignature txSigCustomer = sign(unsignedTransaction, keyCustomer1);

        ScriptBuilder builder = new ScriptBuilder();
        builder.smallNum(0);
        builder.data(txSigCustomer.encodeToBitcoin());
        builder.data(txSigBank.encodeToBitcoin());
        return builder.build();
    }
}
