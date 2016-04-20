package org.fullnodej.test.wallet;

import static org.junit.Assert.*;

import java.security.SecureRandom;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Utils;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.fullnodej.test.FullnodeTest;
import org.fullnodej.test.FullnodeTest.OpenWalletRequired;
import org.junit.Test;

import com.googlecode.jsonrpc4j.JsonRpcClientException;

/** <ol><li>Create private key</li>
 * 		<li>import it to new account in wallet</li>
 * 		<li>verify account exists and the address</li>
 * 		<li>sign with it</li>
 * 		<li>verify signature</li></ol>*/
public class ImportPrivateKey extends FullnodeTest implements OpenWalletRequired {

	@Test public void test() {
		final NetworkParameters network = node.getUtil().isMainnet() ? MainNetParams.get() : TestNet3Params.get();
		final SecureRandom rnd = new SecureRandom("fullnodej".getBytes());
		final ECKey key = new ECKey(rnd);
		final String wif = key.getPrivateKeyAsWiF(network);
		final String address = new Address(network, Utils.sha256hash160(key.getPubKey())).toString();
 		final String account = "fullnodej-" + System.currentTimeMillis();

		assertTrue(address.length() < wif.length());
 		assertFalse(node.getUtil().isWalletAddress(address));
 		assertTrue(node.rpc.getaddressesbyaccount(account).isEmpty());

		try {
			rpc.dumpprivkey(address);
			fail("haven't imported key yet, are you a wizard?");
		} catch (JsonRpcClientException e) { /* expected */ }

		// Import
		try {
			rpc.importprivkey(wif, account, false);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			fail(t.getMessage());
		}

		// Sign with, etc
		String newAccount = rpc.getaccount(address);
		log.warn("new: " + address + " の account は "  + newAccount + " です!");
		assertEquals(account, newAccount);
		try {
			final String sig = node.rpc.signmessage(address, account);
			assertTrue(node.rpc.verifymessage(address, sig, account));
		} catch (Throwable t) {
			log.error(t.getClass().getSimpleName() + ": couldn't sign for " + address + " -- " + t.getMessage(), t);
			fail(t.getMessage());
		}

		passed();
	}

}
