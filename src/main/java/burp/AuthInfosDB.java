package burp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

enum Modes {
    ALL, KVS, USERS
}

enum USER_OPT_STATUS {
    ADD_USER_SUCCESS, USER_EXISTED, DELETE_USER_SUCCESS, USER_NOT_EXISTED, UPDATE_USER_SUCCESS
}

enum USER_VIEW_DIALOG_MODELS {
    REPLACE, ADD, UPDATE, DELTE
}

public class AuthInfosDB {

    public static Modes storeMode = Modes.KVS;
    public static Map<String, String> keyValuesStore = new HashMap<>();
    public static List<AuthInfo> authInfoList = new ArrayList<>();
    public static int userIndex = 0;
    public static USER_VIEW_DIALOG_MODELS user_view_dialog_mode;

    public static Modes getStoreMode() {
        return storeMode;
    }

    public static void setStoreMode(Modes storeMode) {
        AuthInfosDB.storeMode = storeMode;
    }

    public static Map<String, String> getKeyValuesStore() {
        return keyValuesStore;
    }

    public static void setKeyValuesStore(Map<String, String> keyValuesStore) {
        AuthInfosDB.keyValuesStore = keyValuesStore;
    }

    public static List<AuthInfo> getAuthInfoList() {
        return authInfoList;
    }

//    public static void setAuthInfoList(List<AuthInfo> authInfoList) {
//        AuthInfosDB.authInfoList = authInfoList;
//    }

    public static void updateKeyValuesStore(List<String> headers) {
        for (String header : headers) {
            String[] keyValues = header.split(": ");
            if (keyValues.length <= 1) {
                continue;
            }

            if (keyValuesStore.containsKey(keyValues[0])) {
                keyValuesStore.replace(keyValues[0], keyValues[1]);
            }
        }

    }

//    public static void setKeyValues(List<String> keyValues){
//        for (int i = 0; i < keyValues.size(); i++) {
//            String[] keyvalue = keyValues.get(i).split(": ");
//            if (keyvalue.length <= 1){
//                continue;
//            }
//            keyValuesStore.put(keyvalue[0], keyvalue[1]);
//        }
//    }


    public static int getUserIndex() {
        return userIndex;
    }

    public static void setUserIndex(int userIndex) {
        AuthInfosDB.userIndex = userIndex;
    }

    public static USER_OPT_STATUS addAuthInfo(AuthInfo authInfo) {
        for (AuthInfo info : authInfoList) {
            if (info.equals(authInfo)) {
                return USER_OPT_STATUS.USER_EXISTED;
            }
        }
        authInfoList.add(authInfo);
        return USER_OPT_STATUS.ADD_USER_SUCCESS;
    }

    public static USER_OPT_STATUS deleteAuthInfo(String role, String name) {
        int index = -1;
        for (int i = 0; i < authInfoList.size(); i++) {
            AuthInfo authInfo = authInfoList.get(i);
            if (authInfo.getRoleName().equals(role) && authInfo.getUserName().equals(name)) {
                index = i;
                break;
            }
        }
        if (-1 != index) {
            authInfoList.remove(index);
            return USER_OPT_STATUS.DELETE_USER_SUCCESS;
        } else {
            return USER_OPT_STATUS.USER_NOT_EXISTED;
        }
    }

    public static USER_OPT_STATUS updateAuthInfo(String role, String name, Map<String, String> sessionInfos) {
        for (AuthInfo authInfo : authInfoList) {
            if (role.equals(authInfo.getRoleName()) && name.equals(authInfo.getUserName())) {
                authInfo.updateSessionInfo(sessionInfos);
                return USER_OPT_STATUS.UPDATE_USER_SUCCESS;
            }
        }

        return USER_OPT_STATUS.USER_NOT_EXISTED;
    }

    public static Map<String, String> getSessionInfoByRoleAndName(String role, String name) {
        for (AuthInfo authInfo : authInfoList) {
            if (role.equals(authInfo.getRoleName()) && name.equals(authInfo.getUserName())) {
                return authInfo.getSessionInfos();
            }
        }
        return null;
    }

//    public static Map<String, String> getSessionInfoByName(String name){
//        for (AuthInfo authInfo : authInfoList) {
//            if (name.equals(authInfo.getUserName())){
//                return authInfo.getSessionInfos();
//            }
//        }
//        return null;
//    }

    public static USER_VIEW_DIALOG_MODELS getUser_view_dialog_mode() {
        return user_view_dialog_mode;
    }

    public static void setUser_view_dialog_mode(USER_VIEW_DIALOG_MODELS user_view_dialog_mode) {
        AuthInfosDB.user_view_dialog_mode = user_view_dialog_mode;
    }

    public static AuthInfo findAuthInfoByHeaders(List<String> headers) {

        for (AuthInfo authInfo : authInfoList) {
            Map<String, String> tmpSessionInfo = authInfo.getSessionInfos();
            int keyCnt = tmpSessionInfo.size();
            int kEqueCnt = 0;
            for (String header : headers) {
                String[] kvs = header.split(": ");
                if (1 >= kvs.length) {
                    continue;
                }
                if (tmpSessionInfo.containsKey(kvs[0]) && tmpSessionInfo.get(kvs[0]).equals(kvs[1])) {
                    kEqueCnt++;
                }
            }
            if (keyCnt == kEqueCnt) {
                return authInfo;
            }
        }

        return null;
    }
}
