import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class Main {

    private static final int AMOUNT_OF_SERVERS = 1;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        System.out.println("Hello World!");
        CreateAndConnect();
    }

    private static void CreateAndConnect() throws IOException, InterruptedException, KeeperException {
        ZkConnector zkc = new ZkConnector();
        zkc.connect("localhost", AMOUNT_OF_SERVERS, 1);
        int leader = zkc.GetLeader();
        System.out.println("Our leader: " + leader);
        System.out.println("Finished!");
    }
}
