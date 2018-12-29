package NodesCommunicationLayer.ZooKeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;


public class DistributedStateMachine {

    String root = "/ReplicatedSM";
    ZooKeeper _zooKeeper;
    String _myName;
    Object _lock = new Object();

    State _state;

    public DistributedStateMachine(ZooKeeper zooKeeper, String name) throws KeeperException, InterruptedException {
        _zooKeeper = zooKeeper;
        _myName = name;

        RegisterToZnode();
    }

    public boolean ChangeState(StateMachineState newState) {
        State currentState = GetVersionAndState();
        if (currentState.getState() == StateMachineState.Idle || newState == StateMachineState.Idle) {
            return SetData(newState.toString().getBytes(), currentState.getVersion());
        }
        return false;
    }

    private void RegisterToZnode() throws KeeperException, InterruptedException {
        if (_zooKeeper.exists(root, false) == null) {
            _zooKeeper.create(root, StateMachineState.Idle.toString().getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        _zooKeeper.getData(root, false, this::processResult, null);

        Stat stat = new Stat();
        byte[] data = _zooKeeper.getData(root, false, stat);
        UpdateVersionAndState(stat.getVersion(), StateMachineState.valueOf(new String(data)));
    }

    private void UpdateVersionAndState(int version, StateMachineState state) {
        synchronized (_lock) {
            if (version > _state.getVersion()) {
                _state.setVersion(version);
                _state.setState(state);
            }
        }
    }

    private State GetVersionAndState() {
        synchronized (_lock) {
            State returnValue = new State();
            returnValue.setState(_state.getState());
            returnValue.setVersion(_state.getVersion());
            return returnValue;
        }
    }

    private boolean SetData(byte[] data, int version) {
        try {
            _zooKeeper.setData(root, data, version);
            return true;
        } catch (KeeperException e) {
            return false;
        } catch (InterruptedException e) {
            return false;
        }
    }

    private void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {

        UpdateVersionAndState(stat.getVersion(), StateMachineState.valueOf(new String(data)));
        _zooKeeper.getData(root, false, this::processResult, null);
    }
}
