package NodesCommunicationLayer.ZooKeeper;

import java.io.Serializable;

public enum StateMachineState implements Serializable {
    Idle,
    Transmitting,
    Unknown
}
