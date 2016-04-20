package org.fullnodej.test.pingpong;

import org.fullnodej.Notify;
import org.fullnodej.Rpc;
import org.fullnodej.data.Block;
import org.fullnodej.data.WalletTx;
import org.fullnodej.test.TESTDATA;
import org.fullnodej.test.pingpong.PingPongBall.Type;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor @Slf4j
public class PingPongPlayer implements Notify {

	final Type type;
	final TESTDATA data;
	final Rpc node;

	@Override public void blockNotify(Block block) {
		log.warn("Block " + block.getHeight() + " received by " + type);
		PingPongBall current = PingPong.serve();
		if (current.txid == null) {
			log.warn("No txid, too soon?");  return;
		}
		try {
			WalletTx tx = node.gettransaction(current.txid);
			int confirmations = tx.getConfirmations();
			if (confirmations >= data.confirmations) {
				log.debug(type + " ACK " + current.type + "'s Confirmation.");
				if (current.type == Type.PING)
					PingPong.pingConfirmed.countDown();
				// else yawn, we're not doing pong confirmations

			}
		} catch (Throwable e) {
			PingPong.quit(e);
		}

	}

	@Override
	public void walletNotify(WalletTx tx) {
		PingPongBall current = PingPong.serve();
		if (current.txid == null) {
			log.warn("No txid, too soon?");  return;
		}
		String txid = tx.getTxid();
		if (txid.equals(current.txid)) {
			log.debug(type + " ACK " + current.type + "'s zero-conf.");
			if (current.type == Type.PING)
				PingPong.pingRecieved.countDown();
			else
				PingPong.pongReceived.countDown();
		}
	}

	@Override public void alertNotify(String alert) {
		PingPong.quit("Unexpected alert: " + alert);
	}

	@Override
	public void turnedOff() {
		PingPong.quit("Notifications closed.");
	}

	@Override
	public String toString() {
		return PingPongPlayer.class.getSimpleName() + " " + type;
	}

}
