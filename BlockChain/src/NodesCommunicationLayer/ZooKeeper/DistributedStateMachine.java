package NodesCommunicationLayer.ZooKeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;


public class DistributedStateMachine implements Watcher{

    String root = "/ReplicatedSM_test";
    ZooKeeper _zooKeeper;
    String _myName;
    Object _lock = new Object();
    Object _setDataLock = new Object();

    State _state;
    CountDownLatch _sendFinishedEvent;

    public DistributedStateMachine(ZooKeeper zooKeeper, String name) throws KeeperException, InterruptedException {
        _zooKeeper = zooKeeper;
        _myName = name;
        _state = new State();

        RegisterToZnode();
    }

    public boolean ChangeState(StateMachineState newState, State state) {
        if (newState == state.getState()) {
            return false;
        }
        return SetData(ConvertEnumToByte(newState), state.getVersion());
    }

    public boolean ChangeState(StateMachineState newState) {

        State currentState = GetVersionAndState();
        if (currentState.getState() != newState) {
            return SetData(ConvertEnumToByte(newState), currentState.getVersion());
        }
        return false;
    }

    private void RegisterToZnode() throws KeeperException, InterruptedException {
        if (_zooKeeper.exists(root, false) == null) {
            _zooKeeper.create(root, ConvertEnumToByte(StateMachineState.Idle), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

        Stat stat = new Stat();
        byte[] data = _zooKeeper.getData(root, this, stat);
        UpdateVersionAndState(stat.getVersion(), ConvertByteToEnum(data));
    }

    private void UpdateVersionAndState(int version, StateMachineState state) {
        synchronized (_lock) {
            if (version > _state.getVersion()) {
                _state.setVersion(version);
                _state.setState(state);
            }
        }
    }

    public State GetVersionAndState() {
        synchronized (_lock) {
            State returnValue = new State();
            returnValue.setState(_state.getState());
            returnValue.setVersion(_state.getVersion());
            return returnValue;
        }
    }

    private boolean SetData(byte[] data, int version) {
        synchronized (_setDataLock) {
            try {
                _sendFinishedEvent = new CountDownLatch(1);
                _zooKeeper.setData(root, data, version);
                _sendFinishedEvent.await();
                return true;
            } catch (KeeperException e) {
                return false;
            } catch (InterruptedException e) {
                return false;
            }
        }
    }

    byte[] ConvertEnumToByte(StateMachineState state) {
        byte returnValue = 0;
        switch (state) {
            case Idle:
                returnValue = 0;
                break;
            case Transmitting:
                returnValue = 1;
                break;
            case Commit:
                returnValue = 2;
                break;
            case Abort:
                returnValue = 3;
                break;
            case Unknown:
                returnValue = 4;
                break;
            default:
                System.out.println("Not implemented!");
        }
        return new byte[]{returnValue};
    }

    StateMachineState ConvertByteToEnum(byte[] data) {
        if (data == null || data.length != 1) {
            return StateMachineState.Unknown;
        }
        byte en = data[0];
        if (en == 0)
            return StateMachineState.Idle;
        if (en == 1)
            return StateMachineState.Transmitting;
        if (en == 2)
            return StateMachineState.Commit;
        if (en == 3)
            return StateMachineState.Abort;
        return StateMachineState.Unknown;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        synchronized (_lock) {
            try {
                _zooKeeper.getData(root, this, null);
                Stat stat = new Stat();
                byte[] data = new byte[0];
                data = _zooKeeper.getData(root, false, stat);
                System.out.println("Change SM state! " + ConvertByteToEnum(data).toString());
                UpdateVersionAndState(stat.getVersion(), ConvertByteToEnum(data));
                _sendFinishedEvent.countDown();
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
