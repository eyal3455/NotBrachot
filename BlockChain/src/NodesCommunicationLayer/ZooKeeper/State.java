package NodesCommunicationLayer.ZooKeeper;

public class State {
    private StateMachineState _state;
    private int _version;

    public StateMachineState getState() {
        return _state;
    }

    public void setState(StateMachineState _state) {
        this._state = _state;
    }

    public int getVersion() {
        return _version;
    }

    public void setVersion(int _version) {
        this._version = _version;
    }
}
