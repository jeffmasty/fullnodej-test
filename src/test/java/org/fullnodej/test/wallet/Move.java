package org.fullnodej.test.wallet;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;

import org.fullnodej.test.FullnodeTest;
import org.fullnodej.test.FullnodeTest.OpenWalletRequired;
import org.junit.Test;

/** moves all funds from 1 random account to another*/
public class Move extends FullnodeTest implements OpenWalletRequired {

	@Test public void test() {

		Map<String, BigDecimal> accounts = rpc.listaccounts();
		Entry<String, BigDecimal> toAccount = null;
		Entry<String, BigDecimal> fromAccount = null;
		for (Entry<String, BigDecimal> account : accounts.entrySet())
			if (toAccount == null) {
				toAccount = account;
				continue;
			}
			else if(account.getValue().compareTo(BigDecimal.ZERO) > 0) {
					fromAccount = account;
					break;
				}
		if (fromAccount == null || toAccount == null) {
			skipped("2 accounts with funds don't exist");
		}
		BigDecimal combined = toAccount.getValue().add(fromAccount.getValue());

		log.info("moving " + fromAccount.getValue() + " from " + fromAccount.getKey() +
				" to " + toAccount.getKey());
		boolean result = rpc.move(fromAccount.getKey(), toAccount.getKey(), fromAccount.getValue());
		assertTrue(result);
		assertTrue(rpc.getbalance(toAccount.getKey()).equals(combined));
		passed();
	}

}
