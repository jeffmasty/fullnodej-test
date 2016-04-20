package org.fullnodej.test.validation;


import static org.fullnodej.Util.Constants.DEFAULT_ACCOUNT;
import static org.junit.Assert.*;

import java.util.List;

import org.fullnodej.Rpc;
import org.fullnodej.data.AddressDetails;
import org.fullnodej.test.FullnodeTest;
import org.fullnodej.test.FullnodeTest.OpenWalletRequired;
import org.fullnodej.test.TESTDATA;
import org.junit.Test;




/**Tests various features of wallet accounts.<br/>
 * NOTE: After completion, 1 address will have been created and set in the last {@link TESTDATA#walletAccounts}*/
public class WalletAccounts extends FullnodeTest implements OpenWalletRequired {

	String testnetNotInWallet = "";
	String mainnetNotInWallet = "";

	@Test public void test() {

		// look up account for address we don't own
		String unowned = node.getUtil().isMainnet() ? ValidateAddress.MAINNET_ADDR : ValidateAddress.TESTNET_ADDR;
		String accountLabel = rpc.getaccount(unowned);
		if (accountLabel != null) { // wish it were null
			// even though we don't own this address, it looks like it is in the default account:
			assertSame(DEFAULT_ACCOUNT, accountLabel);
			// Let's show that it is not:
			assertFalse(sigcheck(unowned));
			assertFalse(rpc.getaddressesbyaccount(DEFAULT_ACCOUNT).contains(unowned));
		}

		if (data.walletAddresses.isEmpty() && data.walletAccounts.isEmpty()) {
			skipped("walletAccounts not found in " + TESTDATA.PROPERTIES_FILE);
			return;
		}

		for (String account : data.walletAccounts) {
			List<String> addresses = rpc.getaddressesbyaccount(account);
			if (addresses == null || addresses.isEmpty()) {
				String address = rpc.getaccountaddress(account);
				log.info("created account " + account + " with initial address " + address);
			}
			assertTrue(rpc.getaddressesbyaccount(account).size() > 0);
		}

		List<String> all = node.getUtil().allAddresses();
		for (String address : data.walletAddresses) {
			assertTrue(all.contains(address));
			assertNotNull(rpc.getaccount(address));
		}

		// log.info(addressGroupings(rpc));

		passed("Wallet contains " + all.size() + " addresses (" + rpc.listunspent().size() +
				" unspent Utxo) and " + rpc.listaccounts().size() + " accounts");
	}

	public static final String addressGroupings(Rpc rpc) {
		final String separator = System.getProperty("line.separator");
		String dat = "";
		List<List<AddressDetails[]>> groupings = rpc.listaddressgroupings();
		for (List<AddressDetails[]> grouping : groupings) {
			String line = separator;
			for (AddressDetails[] data : grouping) {
				line += "[";
				for (int i = 0; i < data.length; i++) {
					line += data[i].toString();
				}
				line += "]";
			}
			dat += line;
		}
		return dat;
	}

}
