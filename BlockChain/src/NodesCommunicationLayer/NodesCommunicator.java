package NodesCommunicationLayer;

import NodesCommunicationLayer.ZooKeeper.ZkConnector;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;

public class NodesCommunicator {

    ZkConnector _zooKeeperService;

    public NodesCommunicator(String zkHost, String myAddr) throws InterruptedException, IOException, KeeperException {
        _zooKeeperService = new ZkConnector();
        _zooKeeperService.connect(zkHost, 1, myAddr);
    }
}
