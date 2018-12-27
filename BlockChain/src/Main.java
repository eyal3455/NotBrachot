import NodesCommunicationLayer.ZooKeeper.ZookeeperService;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;

import static java.lang.Thread.sleep;

public class Main {

    private static final int AMOUNT_OF_SERVERS = 1;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        System.out.println("Hello World!");
        //CreateAndConnect("EYAL_PC:8089");
    }

    private static Thread CreateThread(String addr) {
        return new Thread(() -> {
            try {
                CreateAndConnect(addr);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (KeeperException e) {
                e.printStackTrace();
            }
        });
    }

    private static void CreateAndConnect(String addr) throws IOException, InterruptedException, KeeperException {
        ZookeeperService zkc = new ZookeeperService();
        zkc.connect("localhost", AMOUNT_OF_SERVERS, addr);
        String leader = zkc.GetLeader();
        sleep(600000);
        System.out.println("Finished!");
    }
}
