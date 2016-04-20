package org.fullnodej.test;

import java.math.BigDecimal;

import org.fullnodej.data.WalletTx;
import org.fullnodej.test.FullnodeTest.FeeSupportRequired;
import org.fullnodej.test.FullnodeTest.OpenWalletRequired;
import org.junit.Assert;
import org.junit.Test;

/**Sends a Tx if directed to by TestData.  Fails if funds unavailable.
 * On success, adds the txid to {@link TESTDATA#walletTxsSent} for later tests to pick up and use*/
public class SendTransaction extends FullnodeTest implements OpenWalletRequired, FeeSupportRequired {

	private boolean silent;
	public SendTransaction setSilent(boolean silent) {
		this.silent = silent;
		return this;
	}

	@Test public void test() {

		if (notSet(data.sendToAddress)) {
			skipped("No address defined to send to");
			return;
		}
		Assert.assertNotNull(data.sendToAmount);
		String comment = " ";
		if (isSet(data.sendComment)) {
			comment = " \"" + data.sendComment;
			if (isSet(data.sendToLabel)) comment += " / " + data.sendToLabel ;
			comment += "\"";
		}
		if (!silent)
			log.warn("sending " + data.sendToAmount + " to " + data.sendToAddress + comment);

		final String txid = rpc.sendtoaddress(
				data.sendToAddress, data.sendToAmount, data.sendComment, data.sendToLabel);
		Assert.assertNotNull(txid);

		WalletTx tx = null;
		try {
			tx = rpc.gettransaction(txid);
		} catch (Throwable e) {
			Assert.fail(e.getMessage() + " for just sent tx: " + txid);
		}

		Assert.assertEquals(data.sendToAmount, tx.getAmount().negate());
		if (feeEstimate.compareTo(BigDecimal.ZERO) >= 0)
			Assert.assertEquals(feeEstimate, tx.getFee().negate());
		Assert.assertEquals(0, tx.getConfirmations()); // won't have insta-mine block between txid and here?

		passed("sent tx: " + txid);

	}

}
