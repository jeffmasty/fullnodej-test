package org.fullnodej.test.misc;

import static org.fullnodej.Util.Constants.*;
import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.fullnodej.test.FullnodeTest;
import org.junit.Test;

public class EstimateFees extends FullnodeTest {

	@Test public void test() {

		BigDecimal estimate = rpc.estimatefee(DEFAULT_BLOCK_FEE_WINDOW);
		assertTrue(estimate.compareTo(LARGE_FEE) < 0);
		// assertTrue(estimate.compareTo(BigDecimal.ZERO) > 0); // -1 happens on testnet often

		if ((estimate.compareTo(BigDecimal.ZERO) > 0)) {
			// Normal Operations
			assertTrue(rpc.settxfee(estimate));

			// slower estimate does not have a higher fee than a faster estimate
			// assertTrue(rpc.estimatefee(10).compareTo(rpc.estimatefee(1)) <= 0);
		}

		try { // the library method
			BigDecimal libEstimate = node.getUtil().useEstimatedFees(DEFAULT_BLOCK_FEE_WINDOW, LARGE_FEE);
			// assertTrue(libEstimate.compareTo(BigDecimal.ZERO) > 0); // -1 happens when data not avail.
			assertTrue(libEstimate.compareTo(LARGE_FEE) <= 0);
		} catch (Exception e) {
			fail(e.getMessage());
		}


		passed("Tx Fee per Kb set to estimate: " + estimate);

	}

}
