package NodesCommunicationLayer.ZooKeeper;

import org.apache.zookeeper.*;

public class DistributedStateMachine implements Watcher {

    String root = "/ReplicatedSM";
    ZooKeeper _zooKeeper;
    String _myName;
    int _myCommitId;
    Object _lock = new Object();

    public DistributedStateMachine(ZooKeeper zooKeeper, String name) throws KeeperException, InterruptedException {
        _zooKeeper = zooKeeper;
        _myName = name;
        _myCommitId = 0;

        RegisterToZnode();
    }

    private void RegisterToZnode() throws KeeperException, InterruptedException {
        if (_zooKeeper.exists(root, this) == null) {
            _zooKeeper.create(root, new byte[] {}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {

    }
}
