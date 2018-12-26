import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZkConnector {
    ZooKeeper zookeeper;
    CountDownLatch connectedSignal = new CountDownLatch(1);

    public void connect(String host) throws IOException, InterruptedException {
        zookeeper = new ZooKeeper(host, 5000,
                new Watcher() {
                    public void process(WatchedEvent event) {
                        if (event.getState() == Event.KeeperState.SyncConnected) {
                            connectedSignal.countDown();
                        }
                    }
                });
        connectedSignal.await();
    }

    public void close() throws InterruptedException {
        zookeeper.close();
    }

    public ZooKeeper getZooKeeper() {
        if (zookeeper == null || !zookeeper.getState().equals(ZooKeeper.States.CONNECTED)) {
            throw new IllegalStateException("ZooKeeper is not connected.");
        }
        return zookeeper;
    }
}