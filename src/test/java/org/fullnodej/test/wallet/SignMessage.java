package org.fullnodej.test.wallet;

import static org.fullnodej.Util.Constants.DEFAULT_ACCOUNT;
import static org.junit.Assert.*;

import org.fullnodej.test.FullnodeTest;
import org.fullnodej.test.FullnodeTest.OpenWalletRequired;
import org.junit.Test;

import com.googlecode.jsonrpc4j.JsonRpcClientException;

public class SignMessage extends FullnodeTest implements OpenWalletRequired {

	@Test public void test() {
		final String addr = rpc.getaccountaddress(DEFAULT_ACCOUNT);
		final String message = "Yo soy un hombre sincero";
		try {
			final String signature = rpc.signmessage(addr, message);
			assertTrue(rpc.verifymessage(addr, signature, message));
			assertFalse(rpc.verifymessage(addr, "0" + signature, message));
			assertFalse(rpc.verifymessage(addr, signature, message + " de donde crece la palma."));
			passed("with the mighty pen: " + addr);
		} catch (JsonRpcClientException e) {
			fail(e.getMessage() + " for addr: " + addr);
		}
	}

}
