package NodesCommunicationLayer;

import Model.Block;
import NodesCommunicationLayer.ServersCommunication.ServerCommunicationService;
import NodesCommunicationLayer.ZooKeeper.State;
import NodesCommunicationLayer.ZooKeeper.StateMachineState;
import NodesCommunicationLayer.ZooKeeper.ZookeeperService;
import org.apache.zookeeper.KeeperException;

import java.io.Console;
import java.io.IOException;

public class NodesCommunicator {

    ZookeeperService _zooKeeperService;
    ServerCommunicationService _serverCommunicationService;
    State _stateWhenStartingToCalculate;

    public NodesCommunicator(String zkHost, String myAddr) throws InterruptedException, IOException, KeeperException {
        _zooKeeperService = new ZookeeperService();
        _serverCommunicationService = new ServerCommunicationService(myAddr);
        _zooKeeperService.connect(zkHost, 1, myAddr);
    }

    // This function gets the version of the current block, to prevent sending irrelevant block.
    public boolean StartCalculating() {
        _stateWhenStartingToCalculate = _zooKeeperService.GetState();
        return _stateWhenStartingToCalculate.getState() != StateMachineState.Transmitting;
    }

    public boolean CommitBlock(Block block) {
        if (_stateWhenStartingToCalculate == null) {
            System.out.println("No known state");
            return false;
        }
        boolean isAuthorizedToBCast = _zooKeeperService.StartTransmitting(_stateWhenStartingToCalculate);   // to ensure the chain hasn't changed since the block's calculation.
        if (!isAuthorizedToBCast) {
            System.out.println("The chain has changed since started calculating the block. Version: " + _stateWhenStartingToCalculate.getVersion());
            return false;
        }

        boolean success = _serverCommunicationService.BroadcastBlock(_zooKeeperService.GetMembership(), block);
        if (success) {
            _zooKeeperService.Commit();
        }
        else {
            _zooKeeperService.Abort();
        }
        return success;
    }
}
