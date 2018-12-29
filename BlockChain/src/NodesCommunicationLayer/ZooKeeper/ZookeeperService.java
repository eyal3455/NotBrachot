package NodesCommunicationLayer.ZooKeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class ZookeeperService {
    ZooKeeper _zookeeper;
    CountDownLatch _connectedSignal;
    Omega _omega;
    DistributedStateMachine _stateMachine;

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
        _stateMachine = new DistributedStateMachine(_zookeeper, name);
    }

    public State GetState() {
        return _stateMachine.GetVersionAndState();
    }

    public boolean StartTransmitting(State state) {
        return _stateMachine.ChangeState(StateMachineState.Transmitting, state);
    }

    public boolean StopTransmitting() {
        return _stateMachine.ChangeState(StateMachineState.Idle);
    }

    public boolean Commit() {
        return _stateMachine.ChangeState(StateMachineState.Commit);
    }

    public boolean Abort() {
        return _stateMachine.ChangeState(StateMachineState.Abort);
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
