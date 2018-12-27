package NodesCommunicationLayer.ZooKeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class ZkConnector {
    ZooKeeper _zookeeper;
    CountDownLatch _connectedSignal;
    Omega _omega;

    public void connect(String host, int amountOfServers, String name) throws IOException, InterruptedException, KeeperException {
        _connectedSignal = new CountDownLatch(amountOfServers);
        _zookeeper = new ZooKeeper(host, 5000,
                new Watcher() {
                    public void process(WatchedEvent event) {
                        if (event.getState() == Event.KeeperState.SyncConnected) {
                            _connectedSignal.countDown();
                        }
                    }
                });
        _connectedSignal.await();
        _omega = new Omega(_zookeeper, name);
    }

    public String GetLeader() throws KeeperException, InterruptedException {
        return _omega.getLeader();
    }

    public ArrayList<String> GetMembership() {
        return _omega.getMembership();
    }

    public void close() throws InterruptedException {
        _zookeeper.close();
    }

    public ZooKeeper getZooKeeper() {
        if (_zookeeper == null || !_zookeeper.getState().equals(ZooKeeper.States.CONNECTED)) {
            throw new IllegalStateException("NodesCommunicationLayer.ZooKeeper is not connected.");
        }
        return _zookeeper;
    }
}
