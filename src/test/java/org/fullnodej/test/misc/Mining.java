package org.fullnodej.test.misc;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.fullnodej.test.FullnodeTest;
import org.junit.Assert;
import org.junit.Test;

public class Mining extends FullnodeTest {

	@Test public void test() {

		if (!data.generate) { skipped(); return; } // not applicable

		log.info(rpc.getmininginfo().toString());

		try {

			rpc.setgenerate(true, 1);
			assertTrue(rpc.getgenerate());
			log.info("Mining now (check yer cpu monitor), sleep for 5 secs and see what happens...");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}


			BigDecimal priority = rpc.estimatepriority(3); // always -1?
			Assert.assertNotNull(priority);
			if (priority.equals(BigDecimal.ONE.negate())) {
				log.warn("No estimatepriority returned (-1)" );
			}
			log.info(rpc.getmininginfo().toString());
			// TODO hashespersec, prioritisetransaction
			passed("That was too fun, let's stop mining now.");
		} finally {
			rpc.setgenerate(false);
		}

	}

}
