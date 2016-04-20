package org.fullnodej.test;

import org.fullnodej.test.listeners.ConfirmationListener;
import org.fullnodej.test.listeners.ZeroConfListener;
import org.fullnodej.test.misc.*;
import org.fullnodej.test.pingpong.PingPong;
import org.fullnodej.test.validation.*;
import org.fullnodej.test.wallet.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/* TODO WatchOnly, MultiSig, SoftForks, ChainTips and so on */
@SuiteClasses({

	// validation
	Preconditions.class,
	GetInfo.class,
	Blockchain.class,
	ValidateAddress.class,
	ValidateSignature.class,

	// misc.
	TxIndex.class,
	WalletAccounts.class,
	WalletTransactions.class,
	EstimateFees.class,
	PingNodes.class,
	AddNode.class,
	Mempool.class,
	Mining.class,

	// Wallet Support - set walletPassphrase in TESTDATA
	WalletBackup.class,
	WalletSupport.class,
	SignMessage.class,
	ImportPrivateKey.class,
	DumpWallet.class,
	ImportWallet.class,
	KeyPoolRefill.class,
	EncryptWallet.class, /*SHUTS DOWN BITCOIND, run once per wallet (detected and skipped afterwards)*/
	WalletPassphraseChange.class,
	// Move.class,

	// Listeners: Long running, some require additional setup (such as having external transactions sent)
	// BlockListener.class,
	ZeroConfListener.class,
	ConfirmationListener.class,

	// WARNING: test affects the blockchain, you can lose funds
	SendTransaction.class,

	// le Finale requires a second node with funds connected with notifications running.
	PingPong.class

}) @RunWith(Suite.class)
public class AllTests {

}
