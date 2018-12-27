package NodesCommunicationLayer;

import NodesCommunicationLayer.ZooKeeper.ZookeeperService;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;

public class NodesCommunicator {

    ZookeeperService _zooKeeperService;

    public NodesCommunicator(String zkHost, String myAddr) throws InterruptedException, IOException, KeeperException {
        _zooKeeperService = new ZookeeperService();
        _zooKeeperService.connect(zkHost, 1, myAddr);
    }

    public boolean CommitBlock() {
        return false;
    }
}
