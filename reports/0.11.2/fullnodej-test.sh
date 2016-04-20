# Starts a bitcoin node in UI and test configuration with notifications
 
bitcoin-qt -testnet -server -txindex -port=18333 -rpcport=18332 \
	-walletnotify="echo '%s' | nc 127.0.0.1 7931" \
	-blocknotify="echo '%s' | nc 127.0.0.1 7931" \
	-alertnotify="echo '%s' | nc 127.0.0.1 7931" 
