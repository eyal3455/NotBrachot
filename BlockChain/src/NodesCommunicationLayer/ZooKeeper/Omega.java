package NodesCommunicationLayer.ZooKeeper;

import org.apache.zookeeper.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Omega  implements Watcher {
    static ZooKeeper zk = null;
    static String root = "/OMEGA";
    static String myName;
    static String elected;
    static ArrayList<String> membership;
    Object lock = new Object();

    public Omega(ZooKeeper zooKeeper, String name) throws KeeperException, InterruptedException {
        zk = zooKeeper;
        myName = name;
        elected = null;
        membership = new ArrayList<String>();

        propose();
        //electLeader();
        //updateMembership();
    }

    public void propose() throws KeeperException, InterruptedException {
        if (zk.exists(root, this) == null) {
            zk.create(root, new byte[] {}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        zk.getChildren(root, this);
        zk.create(root + "/", myName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL);
    }
    public void electLeader() throws KeeperException, InterruptedException {
        synchronized (lock) {
            List<String> children = zk.getChildren(root, false);
            Collections.sort(children);
            byte[] data = null;
            for (String leader : children) {
                data = zk.getData(root + "/" + leader, false , null);
                if (data != null) {
                    break;
                }
            }
            if (data != null) {
                elected = new String(data);
                System.out.println("New leader: " + elected);
            }
        }


    }
    public String getLeader() {
        synchronized (lock) {
            return elected;
        }
    }

    public ArrayList<String> getMembership() {
        synchronized (lock) {
            return membership;
        }
    }

    public void process(WatchedEvent watchedEvent) {
        try {
            zk.getChildren(root, this);
            electLeader();
            updateMembership();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateMembership() throws KeeperException, InterruptedException {
        synchronized (lock) {
            membership.clear();
            List<String> children = zk.getChildren(root, false);
            byte[] data = null;
            for (String leader : children) {
                data = zk.getData(root + "/" + leader, false, null);
                if (data != null) {
                    membership.add(new String(data));
                }
            }
        }
    }
}
