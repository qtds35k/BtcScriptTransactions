package edu.nyu.crypto.csci3033.transactions;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import java.io.File;
import java.math.BigInteger;
import java.net.UnknownHostException;

import static org.bitcoinj.script.ScriptOpCodes.*;

/**
 * Created by bbuenz on 24.09.15.
 */
public class LinearEquationTransaction extends ScriptTransaction {
    private DeterministicKey key;
    // TODO: Problem 2
    public LinearEquationTransaction(NetworkParameters parameters, File file, String password) {
        super(parameters, file, password);
        key = getWallet().freshReceiveKey();
    }

    @Override
    public Script createInputScript() {
        // TODO: Create a script that can be spend by two numbers x and y such that x+y=first 4 digits of your suid and |x-y|=last 4 digits of your suid (perhaps +1)
        ScriptBuilder builder = new ScriptBuilder();
        builder.op(OP_2DUP);
        builder.op(OP_ADD);
        builder.data(encode(new BigInteger("1068")));
        builder.op(OP_EQUALVERIFY);
        builder.op(OP_SUB);
        builder.data(encode(new BigInteger("4258")));
        builder.op(OP_EQUAL);
        return builder.build();
    }

    @Override
    public Script createRedemptionScript(Transaction unsignedScript) {
        // TODO: Create a spending script
        ScriptBuilder builder = new ScriptBuilder();
        builder.data(encode(new BigInteger("2663")));
        builder.data(encode(new BigInteger("-1595")));
        return builder.build();
    }

    private byte[] encode(BigInteger bigInteger) {
        return Utils.reverseBytes(Utils.encodeMPI(bigInteger, false));
    }
}
