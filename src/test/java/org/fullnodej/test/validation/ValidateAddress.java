package org.fullnodej.test.validation;

import static org.fullnodej.Util.Constants.DEFAULT_ACCOUNT;
import static org.junit.Assert.*;

import org.fullnodej.data.AddressInformation;
import org.fullnodej.test.FullnodeTest;
import org.junit.Test;

public class ValidateAddress extends FullnodeTest {

	/** A random, used testnet address that won't be in your wallet */
	public static final String TESTNET_ADDR = "mqdofsXHpePPGBFXuwwypAqCcXi48Xhb2f";
	public static final String TESTNET_COINBASE = "mskhfpSUU12rF91noF2oe7S8kn73nSZarS";
	/** A random, used mainnet address that won't be in your wallet */
	public static final String MAINNET_ADDR = "12higDjoCCNXSA95xZMWUdPvXNmkAduhWv";
	public static final String MAINNET_COINBASE = "1C1QnL6oZqiRmKgaTm8XZbHccwupvJsAwG";

	@Test public void test() {

		AddressInformation info;

		final String addr = rpc.getaccountaddress(DEFAULT_ACCOUNT);
		info = rpc.validateaddress(addr);
		assertTrue(info.isValid());
		assertTrue(info.isMine());
		assertEquals(DEFAULT_ACCOUNT, info.getAccount());

		// coinbase output address of block 666
		final String externalAddress = node.getUtil().isTestnet() ? TESTNET_ADDR : MAINNET_ADDR;
		info = rpc.validateaddress(externalAddress);
		assertTrue(info.isValid());
		assertFalse(info.isMine());
		assertNull(info.getAccount());

		passed();

	}

}
