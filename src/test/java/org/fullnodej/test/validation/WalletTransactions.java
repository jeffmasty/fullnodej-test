package org.fullnodej.test.validation;

import static org.junit.Assert.assertFalse;

import java.util.List;

import org.fullnodej.data.WalletTx;
import org.fullnodej.test.FullnodeTest;
import org.junit.Assert;
import org.junit.Test;

public class WalletTransactions extends FullnodeTest {

	@SuppressWarnings("rawtypes")
	@Test public void test() {

		if ( (data.walletTxsRcvd == null || data.walletTxsRcvd.isEmpty()) &&
				(data.walletTxsSent == null || data.walletTxsSent.isEmpty())) {
			skipped("wallet tx data not found");
			return;
		}

		WalletTx tx;
		for (String txid : data.walletTxsRcvd) {
			tx = rpc.gettransaction(txid);
			Assert.assertNotNull(tx);
		}
		for (String txid : data.walletTxsSent) {
			tx = rpc.gettransaction(txid);
			Assert.assertNotNull(tx);
		}


		List all = rpc.listreceivedbyaccount(6, true, false);
		List received = rpc.listreceivedbyaccount(6, false, false);
		assertFalse(received.size() > all.size());

		all = rpc.listreceivedbyaddress(6, true, false);
		received = rpc.listreceivedbyaddress(6, false, false);
		assertFalse(received.size() > all.size());
		// for (Object obj : received) log.info(obj.toString());

		passed();

	}

}
