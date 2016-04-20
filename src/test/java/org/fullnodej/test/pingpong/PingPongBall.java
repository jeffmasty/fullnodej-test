package org.fullnodej.test.pingpong;

import java.math.BigDecimal;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PingPongBall {

	public static enum Type { PING, PONG }

	final Type type;
	final BigDecimal fee;
	final String toAddress;
	String txid;

}

