package org.fullnodej.test;

import java.math.BigDecimal;

import org.fullnodej.Fullnode;
import org.fullnodej.Notify;
import org.fullnodej.Rpc;
import org.fullnodej.data.WalletTx;
import org.fullnodej.test.misc.TxIndex;
import org.fullnodej.test.validation.Preconditions;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.jsonrpc4j.JsonRpcClientException;

/**base class for tests in this library:
 * <ul><li>statically initializes the rpc connection</li>
 *     <li>statically initializes the TESTDATA</li></ul>**/
public abstract class FullnodeTest {

	/**flag to indicate unencrypted or unlocked wallet is needed
	 * @see {@link FullnodeTest#setup()}*/
	public static interface OpenWalletRequired { }
	/**flag to indicate competitive fees need to be used*/
	public static interface FeeSupportRequired { }
	/**flag to indicate rpc needs to be running -txindex*/
	public static interface TxIndexRequired { }

	/**NOTICE: contains the data tested against the rpc, initialized from TestData.properties */
	protected static TESTDATA data = initTestData();

	private static TESTDATA initTestData() {
		TESTDATA data = new TESTDATA();
		try {
			// load from TESTDATA.properties
			// or comment out and put your own data in here...
			data.load();
		} catch (Exception e) {
			LoggerFactory.getLogger(FullnodeTest.class).error(e.getMessage(), e);
		}
		return data;
	}

	/** RPC source of blockchain and wallet */
	protected static Rpc rpc;
	protected static Fullnode node;

	/** For tests requesting Fee support, this is the fee/kb set */
	protected static BigDecimal feeEstimate;

	protected boolean shutdown;

	@BeforeClass
	public static void init() throws Exception {
		if (node == null) {
			node = new Fullnode(data.getRpcParams(), data.listenerPort);
			rpc = node.rpc;
		}
	}

	/**check for interfaces attached to this Test (OpenWalletRequired, FeeSupportRequired, TxIndexRequired)
	 * and plug in necessary support accordingly*/
	@Before
	public final void setup() throws RuntimeException {

		if (this instanceof FeeSupportRequired) {
			try {
				feeEstimate = node.getUtil().useEstimatedFees();
				log.info("Fee support required, using: " + feeEstimate);
			} catch (Exception e) {
				log.error("Fee estimation failed. " + e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}
		if (this instanceof OpenWalletRequired) {
			node.getWallet().unlock(data.walletPassphrase, 15);
		}
		if (this instanceof TxIndexRequired) {
			String requires = this.getClass().getSimpleName() + " requires -txindex... ";
			if (data.txindex) throw new IllegalStateException(requires + "Set TX_INDEXED to true in " + TESTDATA.PROPERTIES_FILE);
			if (TxIndex.isPassed()) {
				log.info(requires + "OK (previously tested)");
				return;
			}
			try {
				new TxIndex().test();
				log.info(requires + "OK -txindex found");
			} catch (Throwable t) {
				log.error(requires + "FAIL: " + t.getClass().getSimpleName() + ": " + t.getMessage());
				throw t;
			}
		}
	}

	@After
	public final void teardown() throws RuntimeException {
		shutdown = true;
		if (this instanceof OpenWalletRequired) {
			node.getWallet().lock();
		}
		if (this instanceof Notify && node.getNotifications().isListening()) {
			node.getNotifications().detach((Notify) this);
			log.debug("Notify detached.");
		}
	}

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	public static final void preconditions() { new Preconditions().test(); }
	public static final boolean isTxIndexed() { try { new TxIndex().test(); return true; } catch (Throwable t) { return false; } }
	public static final boolean isTrue(String s) { return "true".equalsIgnoreCase(s); }
	public static final boolean isFalse(String s) { return "false".equalsIgnoreCase(s); }
	public static final boolean notSet(String s) { return s == null || s.isEmpty(); }
	public static final boolean isSet(String s) { return s != null && !s.isEmpty(); }
	public static final boolean isConfirmed(WalletTx tx) { return tx.getConfirmations() >= data.confirmations; }
	public static final String vs(Object a, Object b) { return a.toString() + " vs. " + b.toString(); }

	public static final String SIGCHECK = "sigcheck";
	public static final boolean sigcheck() { return sigcheck(rpc.getaccountaddress(SIGCHECK)); }
	/**@return true if able to sign for the address (requires unlocked wallet)*/
	public static final boolean sigcheck(String addr) {
		try {
			String sig = rpc.signmessage(addr, SIGCHECK);
			if (!rpc.verifymessage(addr, sig, SIGCHECK))
				throw new IllegalArgumentException("FAIL: sig of \"" + SIGCHECK + "\" for " + addr + " = " + sig);
			return true;
		} catch (JsonRpcClientException e) {
			return false;
		}
	}

	protected void passed() { log.info("PASSED"); }
	protected void passed(String msg) { log.info("PASSED " + msg); }
	protected void skipped() { log.info("SKIPPED"); }
	protected void skipped(String msg) { log.info("SKIPPED " + msg); }

}
