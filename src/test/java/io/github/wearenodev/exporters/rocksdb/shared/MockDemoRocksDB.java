package io.github.wearenodev.exporters.rocksdb.shared;

import io.github.wearenodev.exporters.rocksdb.models.JRocksDB;
import io.github.wearenodev.exporters.rocksdb.shared.controllers.ActionInfoCtrl;
import io.github.wearenodev.exporters.rocksdb.shared.controllers.UserActionCtrl;
import io.github.wearenodev.exporters.rocksdb.shared.controllers.UserInfoCtrl;
import io.github.wearenodev.exporters.rocksdb.shared.entity.ActionInfo;
import io.github.wearenodev.exporters.rocksdb.shared.entity.UserInfo;
import org.rocksdb.ColumnFamilyHandle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MockDemoRocksDB {

    static List<UserInfo> users = new ArrayList<>();
    static Map<Integer, List<ActionInfo>> mapUserActions = new HashMap<>();

    static {
        for (int i = 1; i <= 50; i++) {
            users.add(genUser(i));
            List<ActionInfo> actions = new ArrayList<>();
            for (int j = 1; j <= 20; j++) {
                actions.add(genAction(i, j));
            }
            mapUserActions.put(i, actions);
        }
    }

    static UserInfo genUser(int i) {
        return new UserInfo(i, "User " + i);
    }

    static ActionInfo genAction(int uid, int order) {
        long actionId = uid * 1000L + order;
        return new ActionInfo(actionId, uid, "User " + uid + " do action " + actionId);
    }

    static void testUserInfo() {
        for (UserInfo u : users) {
            UserInfoCtrl.put(u);
            System.out.println(UserInfoCtrl.get(u.getId()));
        }
    }

    static void testActionInfo() {
        for (UserInfo u : users) {
            List<ActionInfo> actions = mapUserActions.get(u.getId());
            for (ActionInfo act : actions) {
                ActionInfoCtrl.put(act);
                System.out.println(ActionInfoCtrl.get(act.getId()));
            }
        }
    }

    static void testUserAction() {
        for (UserInfo u : users) {
            List<ActionInfo> actions = mapUserActions.get(u.getId());
            for (ActionInfo act : actions) {
                UserActionCtrl.put(act);
            }
        }

        for (UserInfo u : users) {
            List<ActionInfo> actions = UserActionCtrl.scan(u.getId());
            System.out.println("Scan actions of user " + u);
            for (ActionInfo act : actions) {
                System.out.println(">>> " + act);
            }
        }
    }

    static void getDBCurrUser() {
        UserInfo user = users.get(users.size() - 1);
        System.out.println("Get user " + UserInfoCtrl.get(user.getId()));
        List<ActionInfo> actions = UserActionCtrl.scan(user.getId());
        System.out.println("Scan actions of user " + user + "; actions.size=" + actions.size());
    }

    static void putDBNextUser() {
        int nextUID = users.size() + 1;
        UserInfo user = genUser(nextUID);
        List<ActionInfo> actions = new ArrayList<>();
        for (int j = 1; j <= 20; j++) {
            actions.add(genAction(nextUID, j));
        }

        System.out.println("Put db new user " + user);

        UserInfoCtrl.put(user);
        for (ActionInfo act : actions) {
            ActionInfoCtrl.put(act);
        }
        for (ActionInfo act : actions) {
            UserActionCtrl.put(act);
        }

        // update list users after put db
        users.add(user);
    }

    public static void doBackgroundJob() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

        scheduler.scheduleAtFixedRate(() -> {
            putDBNextUser();
        }, 1, 1, TimeUnit.SECONDS);

        scheduler.scheduleAtFixedRate(() -> {
            getDBCurrUser();
        }, 1, 1, TimeUnit.SECONDS);
    }

    public static void scheduleChangeCF(JRocksDB jRocksDB) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

        String cfName = "TestCF";

        scheduler.schedule(() -> {
            System.out.println("\n\nTest addNewCFHandles\n\n");
            ColumnFamilyHandle cfHandle = DemoRocksDB.addNewCF(cfName);
            jRocksDB.addNewCFHandles(cfHandle);
        }, 10, TimeUnit.SECONDS);

        scheduler.schedule(() -> {
            System.out.println("\n\nTest removeCFHandles\n\n");
            ColumnFamilyHandle cfHandle = DemoRocksDB.removeCF(cfName);
            jRocksDB.removeCFHandles(cfHandle);
        }, 20, TimeUnit.SECONDS);

        scheduler.schedule(() -> {
            System.out.println("\n\nTest setListCFHandles [add]\n\n");
            DemoRocksDB.addNewCF(cfName);
            jRocksDB.setListCFHandles(DemoRocksDB.getCfHandles());
        }, 30, TimeUnit.SECONDS);

        scheduler.schedule(() -> {
            System.out.println("\n\nTest setListCFHandles [remove]\n\n");
            DemoRocksDB.removeCF(cfName);
            jRocksDB.setListCFHandles(DemoRocksDB.getCfHandles());
        }, 40, TimeUnit.SECONDS);

    }

    public static void main(String[] args) {

        DemoRocksDB.openDB();

        testUserInfo();
        testActionInfo();
        testUserAction();

        doBackgroundJob();

    }

}
