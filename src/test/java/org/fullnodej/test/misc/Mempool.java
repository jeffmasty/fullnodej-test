package org.fullnodej.test.misc;

import static org.junit.Assert.assertNotNull;

import org.fullnodej.test.FullnodeTest;
import org.junit.Test;

/**This can be a time consuming test*/
public class Mempool extends FullnodeTest {

	@Test public void test() {

		log.info("test starting, can be a lengthy process...");
		// log.info("---------------Mempool----------------");
		// log.info(Arrays.toString(rpc.getrawmempool().toArray()));
		// log.info("----------Mempool Objects-----------");
		// log.info(Arrays.toString(rpc.getrawmempool(true).values().toArray()));

		assertNotNull(rpc.getrawmempool());
 		assertNotNull(rpc.getrawmempool(true));
		assertNotNull(rpc.gettxoutsetinfo());
		assertNotNull(rpc.getchaintips());
		passed();

	}

}
