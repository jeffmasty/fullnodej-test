# Starts the ping Node of the PingPong test. 
# This node initiates the test transfer because, hopefully, it holds reserve funds
# compared to the primary wallet under test  
#
bitcoin-qt -testnet -server -port=18666 -rpcport=18665 \
	-datadir=/home/judah/.bitcoin/pingpong \
	-blocknotify="echo '%s' | nc 127.0.0.1 7932" \
	-walletnotify="echo '%s' | nc 127.0.0.1 7932" \
	-alertnotify="echo '%s' | nc 127.0.0.1 7932"

