# blockchain-demo2

## Usage

**Step1 Run 2 Nodes**

- Run `com.dvbug.demo2.RunApp` class with `-Dserver.port=8090`
  > Node1 listened on `/ip4/<ip>/tcp/<p2pport>/ipfs/QmasYpE1CiMS5onR9r7WDejmYu34GippLBqmScZ9QhVQUF`
- Run `com.dvbug.demo2.RunApp` class with `-Dserver.port=8091`
  > Node2 listened on `/ip4/<ip>/tcp/<p2pport>/ipfs/QmaareuRHP73aohRR5bej8tStzGLpAMssRG5acJGKL6AEc`

**Step2 Test Node1 with RestApi**

- `http://localhost:8090/api/conn?addr=<node2 listened addr>`
  > New peer /ip4/<ip>/tcp/<p2pport>/ipfs/QmaareuRHP73aohRR5bej8tStzGLpAMssRG5acJGKL6AEc connected
  > Then in node2 also connected to node1
- `http://localhost:8090/api/ping?peer=QmaareuRHP73aohRR5bej8tStzGLpAMssRG5acJGKL6AEc`
  > Will response `PONG` message from remote peer
- `http://localhost:8090/api/test`
  > Will create `Genesis` block and then mine second block
  > broadcast new block into p2p net after mining
  > In node2, will receive `RESPONSE_LATEST_BLOCK` p2p message
- `http://localhost:8090/api/mine`
  > Mine a new block, and broadcast into p2p net after mining
- `http://localhost:8090/api/scan`
  > Show blockchain on this node