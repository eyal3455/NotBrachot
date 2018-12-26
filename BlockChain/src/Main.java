import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class Main {

    private static final int AMOUNT_OF_SERVERS = 2;

    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("Hello World!");
        ZkConnector zkc = new ZkConnector();
        zkc.connect("localhost", AMOUNT_OF_SERVERS);
        ZooKeeper zk = zkc.getZooKeeper();
        System.out.println("Finished!");
    }
}
