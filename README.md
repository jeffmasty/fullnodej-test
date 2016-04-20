#Fullnodej-test

**Junit Tests for the Fullnodej project**  
  
https://github.com/jeffmasty/fullnodej

###Usage

Individual tests extend FullnodeTest which does the work of connecting 
to the Rpc and gathering test configuration data.
  
Test configuration is stored TESTDATA.java and can be loaded with TESTDATA.properties,
other TESTDATA.java constructors or manipulating TESTDATA fields directly.    
see: FullnodeTest.initTestData();
  
If a test requires an unlocked wallet, have that test implement 
FullnodeTest.OpenWalletRequired and support will be wired in at initialization.

Similarly, if a test requires Notifications, have that test implement the 
Notify interface and it will get wired in.   

###Test Reports

For reference, a test log, bitcoind startup and config scripts, TESTDATA.propertes and the actual wallet.dat used:

	/reports/0.11.2 

##License 

MIT License