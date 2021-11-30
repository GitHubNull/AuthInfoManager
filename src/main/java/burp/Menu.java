package burp;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class Menu implements IContextMenuFactory, ActionListener {
    private static IHttpRequestResponse[] messages;

    @Override
    public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {

        messages = invocation.getSelectedMessages();

        List<JMenuItem> menu = new ArrayList<>();


        JMenu mainMenu = new JMenu("AuthInfoManager");
        mainMenu.setActionCommand("AuthInfoManager");
        mainMenu.addActionListener(this);

        if (Modes.KVS == AuthInfosDB.getStoreMode()) {
            if (!AuthInfosDB.getKeyValuesStore().isEmpty()) {
                JMenuItem replaceAuthInfos = new JMenuItem("replaceKVS");
                replaceAuthInfos.setActionCommand("replaceKVS");
                replaceAuthInfos.addActionListener(this);
                mainMenu.add(replaceAuthInfos);

                JMenuItem updateKVs = new JMenuItem("updateKVs");
                updateKVs.setActionCommand("updateKVs");
                updateKVs.addActionListener(this);
                mainMenu.add(updateKVs);
            }

            JMenuItem addKVs = new JMenuItem("addKVs");
            addKVs.setActionCommand("addKVs");
            addKVs.addActionListener(this);
            mainMenu.add(addKVs);

            if (!AuthInfosDB.getKeyValuesStore().isEmpty()) {
                JMenuItem clearKVS = new JMenuItem("clearKVS");
                clearKVS.setActionCommand("clearKVS");
                clearKVS.addActionListener(this);
                mainMenu.add(clearKVS);
            }
        } else if (Modes.USERS == AuthInfosDB.getStoreMode()) {
            if (0 >= AuthInfosDB.getAuthInfoList().size()) {
                JMenuItem addUser = new JMenuItem("addUser");
                addUser.setActionCommand("addUser");
                addUser.addActionListener(this);
                mainMenu.add(addUser);
            } else {
                JMenuItem replaceUser = new JMenuItem("replaceUser");
                replaceUser.setActionCommand("replaceUser");
                replaceUser.addActionListener(this);
                mainMenu.add(replaceUser);

                JMenuItem updateUser = new JMenuItem("updateUser");
                updateUser.setActionCommand("updateUser");
                updateUser.addActionListener(this);
                mainMenu.add(updateUser);

                JMenuItem addUser = new JMenuItem("addUser");
                addUser.setActionCommand("addUser");
                addUser.addActionListener(this);
                mainMenu.add(addUser);

                JMenuItem deleteUser = new JMenuItem("deleteUser");
                deleteUser.setActionCommand("deleteUser");
                deleteUser.addActionListener(this);
                mainMenu.add(deleteUser);
            }
        } else {
            /////////////////////////////////////// kvs mode main menu
            JMenu kvModeMain = new JMenu("kvModeMain");

            if (!AuthInfosDB.getKeyValuesStore().isEmpty()) {
                JMenuItem replaceAuthInfos = new JMenuItem("replaceKVS");
                replaceAuthInfos.setActionCommand("replaceKVS");
                replaceAuthInfos.addActionListener(this);
                kvModeMain.add(replaceAuthInfos);

                JMenuItem updateKVs = new JMenuItem("updateKVs");
                updateKVs.setActionCommand("updateKVs");
                updateKVs.addActionListener(this);
                kvModeMain.add(updateKVs);
            }

            JMenuItem addKVs = new JMenuItem("addKVs");
            addKVs.setActionCommand("addKVs");
            addKVs.addActionListener(this);
            kvModeMain.add(addKVs);

            if (!AuthInfosDB.getKeyValuesStore().isEmpty()) {
                JMenuItem clearKVS = new JMenuItem("clearKVS");
                clearKVS.setActionCommand("clearKVS");
                clearKVS.addActionListener(this);
                kvModeMain.add(clearKVS);
            }
            mainMenu.add(kvModeMain);

            /////////////////////////////////// user mode main menu
            JMenu userModeMain = new JMenu("userModeMain");

            if (0 >= AuthInfosDB.getAuthInfoList().size()) {
                JMenuItem addUser = new JMenuItem("addUser");
                addUser.setActionCommand("addUser");
                addUser.addActionListener(this);
                userModeMain.add(addUser);
            } else {
                JMenuItem replaceUser = new JMenuItem("replaceUser");
                replaceUser.setActionCommand("replaceUser");
                replaceUser.addActionListener(this);
                userModeMain.add(replaceUser);

                JMenuItem updateUser = new JMenuItem("updateUser");
                updateUser.setActionCommand("updateUser");
                updateUser.addActionListener(this);
                userModeMain.add(updateUser);

                JMenuItem addUser = new JMenuItem("addUser");
                addUser.setActionCommand("addUser");
                addUser.addActionListener(this);
                userModeMain.add(addUser);

                JMenuItem deleteUser = new JMenuItem("deleteUser");
                deleteUser.setActionCommand("deleteUser");
                deleteUser.addActionListener(this);
                userModeMain.add(deleteUser);
            }

            mainMenu.add(userModeMain);

        }


        menu.add(mainMenu);

        JMenu modeChoose = new JMenu("modeChoose");
        modeChoose.addActionListener(this);


        JMenuItem kvMode = new JMenuItem("kvMode");
        kvMode.setActionCommand("kvMode");
        kvMode.addActionListener(this);

        JMenuItem usersMode = new JMenuItem("usersMode");
        usersMode.setActionCommand("usersMode");
        usersMode.addActionListener(this);

        JMenuItem allMode = new JMenuItem("allMode");
        allMode.setActionCommand("allMode");
        allMode.addActionListener(this);

        modeChoose.add(kvMode);
        modeChoose.add(usersMode);
        modeChoose.add(allMode);
        menu.add(modeChoose);

        return menu;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (messages.length <= 0) {
            return;
        }
        IHttpRequestResponse iHttpRequestResponse = messages[0];
        byte[] request = iHttpRequestResponse.getRequest();
        List<String> headers = BurpExtender.helpers.analyzeRequest(request).getHeaders();
        if (headers.size() <= 0) {
            return;
        }

        String actionCommand = e.getActionCommand();

        switch (actionCommand) {
            case "replaceKVS":
                Map<String, String> keyValuesStored = AuthInfosDB.getKeyValuesStore();


                Set<String> existKeys = new HashSet<>();
                for (int i = 0; i < headers.size(); i++) {
                    String[] kv = headers.get(i).split(": ");
                    if (kv.length <= 1) {
                        continue;
                    }
                    if (keyValuesStored.containsKey(kv[0])) {
                        existKeys.add(kv[0]);
                        headers.set(i, kv[0] + ": " + keyValuesStored.get(kv[0]));
                    }
                }


                Set<String> allKeys = keyValuesStored.keySet();
                Set<String> resultSet = new HashSet<>(allKeys);

                resultSet.removeAll(existKeys);

                for (String key : resultSet) {
                    System.out.println(key);
                    headers.add(key + ": " + keyValuesStored.get(key));
                }

                byte[] body = Utils.getBodyBytes(request, BurpExtender.helpers.analyzeRequest(request).getBodyOffset());


                BurpExtender.StdoutPrintln("=================================================");
                BurpExtender.StdoutPrintln(BurpExtender.helpers.bytesToString(body));
                BurpExtender.StdoutPrintln("=================================================");

                byte[] rawData = BurpExtender.helpers.buildHttpMessage(headers, body);

                BurpExtender.StdoutPrintln("-------------------------------------------------");
                BurpExtender.StdoutPrintln(BurpExtender.helpers.bytesToString(rawData));
                BurpExtender.StdoutPrintln("-------------------------------------------------");


                messages[0].setRequest(rawData);


                break;
            case "addKVs":

                KeyValuesDialog keyValuesDialog = new KeyValuesDialog();
                keyValuesDialog.addRows(headers);

                keyValuesDialog.pack();

                keyValuesDialog.setDefaultCloseOperation(KeyValuesDialog.DISPOSE_ON_CLOSE);
                keyValuesDialog.setVisible(true);

                AuthInfosDB.setKeyValuesStore(keyValuesDialog.getSelectKeyValues());

                break;
            case "updateKVs":
                AuthInfosDB.updateKeyValuesStore(headers);
                break;
            case "clearKVS":
                AuthInfosDB.getKeyValuesStore().clear();
                break;
            case "replaceUser":
//                UsersViewsDialog replace
                AuthInfosDB.setUser_view_dialog_mode(USER_VIEW_DIALOG_MODELS.REPLACE);
                UsersViewsDialog replaceUsersViewsDialog = new UsersViewsDialog();

                List<AuthInfo> authInfoList = AuthInfosDB.getAuthInfoList();
                int index = 0;
                for (AuthInfo authInfo : authInfoList) {
                    replaceUsersViewsDialog.addUserRow(new Object[]{authInfo.getRoleName(), authInfo.getUserName(), false});
                    if (0 == index) {
                        replaceUsersViewsDialog.setUsersTableSelectRow(0);
                        replaceUsersViewsDialog.addKVsByUserSessionInfos(authInfo.getSessionInfos());
                        index++;
                    }
                }

                replaceUsersViewsDialog.setUsersTableSelectRow(0);
                replaceUsersViewsDialog.pack();
                replaceUsersViewsDialog.setReplaceUserDialogConfig(headers);
                replaceUsersViewsDialog.setDefaultCloseOperation(UsersViewsDialog.DISPOSE_ON_CLOSE);
                replaceUsersViewsDialog.setVisible(true);

                Map<String, String> tmpKeyValues = replaceUsersViewsDialog.getSelectReplaceUserSessionInfo();
                if (tmpKeyValues.isEmpty()) {
                    return;
                }

                Set<String> replaceUserExistKeys = new HashSet<>();
                for (int i = 0; i < headers.size(); i++) {
                    String header = headers.get(i);
                    String[] kvs = header.split(": ");
                    if (kvs.length <= 1) {
                        continue;
                    }
                    if (tmpKeyValues.containsKey(kvs[0])) {
//                        header.repl
                        headers.set(i, kvs[0] + ": " + tmpKeyValues.get(kvs[0]));
                        replaceUserExistKeys.add(kvs[0]);
                    }
                }

                Set<String> replaceUserAllKeys = tmpKeyValues.keySet();
                Set<String> replaceUserResultSet = new HashSet<>(replaceUserAllKeys);

                replaceUserResultSet.removeAll(replaceUserExistKeys);

                for (String key : replaceUserResultSet) {
                    System.out.println(key);
                    headers.add(key + ": " + tmpKeyValues.get(key));
                }

                byte[] replaceUserBody = Utils.getBodyBytes(request, BurpExtender.helpers.analyzeRequest(request).getBodyOffset());


                BurpExtender.StdoutPrintln("=================================================");
                BurpExtender.StdoutPrintln(BurpExtender.helpers.bytesToString(replaceUserBody));
                BurpExtender.StdoutPrintln("=================================================");

                byte[] replaceUserRawData = BurpExtender.helpers.buildHttpMessage(headers, replaceUserBody);

                BurpExtender.StdoutPrintln("-------------------------------------------------");
                BurpExtender.StdoutPrintln(BurpExtender.helpers.bytesToString(replaceUserRawData));
                BurpExtender.StdoutPrintln("-------------------------------------------------");


                messages[0].setRequest(replaceUserRawData);

                break;
            case "addUser":
                AuthInfosDB.setUser_view_dialog_mode(USER_VIEW_DIALOG_MODELS.ADD);

                UsersViewsDialog addUsersViewsDialog = new UsersViewsDialog();

                List<AuthInfo> addUserAuthInfoList = AuthInfosDB.getAuthInfoList();
                int addUserIndex = 0;
                for (AuthInfo authInfo : addUserAuthInfoList) {
                    addUsersViewsDialog.addUserRow(new Object[]{authInfo.getRoleName(), authInfo.getUserName(), false});
                    if (0 == addUserIndex) {
                        addUsersViewsDialog.setUsersTableSelectRow(0);
                        addUsersViewsDialog.addKVsByUserSessionInfos(authInfo.getSessionInfos());
                        addUserIndex++;
                    }
                }
                addUsersViewsDialog.setUsersTableSelectRow(0);


                addUsersViewsDialog.initData(headers);
                addUsersViewsDialog.pack();
                addUsersViewsDialog.setAddUserDialogConfig();
                addUsersViewsDialog.setDefaultCloseOperation(UsersViewsDialog.DISPOSE_ON_CLOSE);
                addUsersViewsDialog.setVisible(true);
                break;
            case "updateUser":
                AuthInfosDB.setUser_view_dialog_mode(USER_VIEW_DIALOG_MODELS.UPDATE);
                UsersViewsDialog updateUsersViewsDialog = new UsersViewsDialog();

                List<AuthInfo> updateUserAuthInfoList = AuthInfosDB.getAuthInfoList();
                int updateUserIndex = 0;
                for (AuthInfo authInfo : updateUserAuthInfoList) {
                    updateUsersViewsDialog.addUserRow(new Object[]{authInfo.getRoleName(), authInfo.getUserName()});
                    if (0 == updateUserIndex) {
                        updateUsersViewsDialog.setUsersTableSelectRow(0);
                        updateUsersViewsDialog.addKVsByUserSessionInfos(authInfo.getSessionInfos());
                        updateUserIndex++;
                    }
                }
                updateUsersViewsDialog.setTmpkeyValuesForUpdate(Utils.listStringToMapString(headers));
                updateUsersViewsDialog.setUsersTableSelectRow(0);

                updateUsersViewsDialog.pack();
                updateUsersViewsDialog.setUpdateUserDialogConfig();
                updateUsersViewsDialog.setDefaultCloseOperation(UsersViewsDialog.DISPOSE_ON_CLOSE);
                updateUsersViewsDialog.setVisible(true);

                break;

            case "deleteUser":
                AuthInfosDB.setUser_view_dialog_mode(USER_VIEW_DIALOG_MODELS.DELTE);
                UsersViewsDialog deleteUsersViewsDialog = new UsersViewsDialog();

                List<AuthInfo> deleteUserAuthInfoList = AuthInfosDB.getAuthInfoList();
                int deleteUserIndex = 0;
                for (AuthInfo authInfo : deleteUserAuthInfoList) {
                    deleteUsersViewsDialog.addUserRow(new Object[]{authInfo.getRoleName(), authInfo.getUserName(), false});
                    if (0 == deleteUserIndex) {
                        deleteUsersViewsDialog.setUsersTableSelectRow(0);
                        deleteUsersViewsDialog.addKVsByUserSessionInfos(authInfo.getSessionInfos());
                        deleteUserIndex++;
                    }
                }
                deleteUsersViewsDialog.setUsersTableSelectRow(0);

                deleteUsersViewsDialog.setDeleteUserDialogConfig();
                deleteUsersViewsDialog.pack();
                deleteUsersViewsDialog.setDefaultCloseOperation(UsersViewsDialog.DISPOSE_ON_CLOSE);
                deleteUsersViewsDialog.setVisible(true);
                break;
            case "kvMode":
                AuthInfosDB.setStoreMode(Modes.KVS);
                break;
            case "usersMode":
                AuthInfosDB.setStoreMode(Modes.USERS);
                break;
            case "allMode":
                AuthInfosDB.setStoreMode(Modes.ALL);
                break;
            default:
                break;
        }
    }
}
