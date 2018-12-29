package NodesCommunicationLayer;

import NodesCommunicationLayer.ZooKeeper.State;
import NodesCommunicationLayer.ZooKeeper.StateMachineState;
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

        State state = _zooKeeperService.GetState();
        if (state.getState() == StateMachineState.Transmitting) {
            return false;
        }

        //TODO: calculate block to add

        boolean isAuthorizedToBCast = _zooKeeperService.StartTransmitting(state);   // to ensure the block is valid.
        if (!isAuthorizedToBCast) {
            return false;
        }

        //TODO: Broadcast here.

        //TODO: _zooKeeperService.Commit()/Abort();
        _zooKeeperService.StopTransmitting();
        return true;
    }
}
