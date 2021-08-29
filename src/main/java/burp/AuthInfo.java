package burp;

import java.util.Map;
import java.util.Objects;

public class AuthInfo {
    private String roleName;
    private final String UserName;
    private final Map<String, String> sessionInfos;

    public AuthInfo(String roleName, String userName, Map<String, String> sessionInfos) {
        this.roleName = roleName;
        UserName = userName;
        this.sessionInfos = sessionInfos;
    }

    public String getUserName() {
        return UserName;
    }

//    public void setUserName(String userName) {
//        UserName = userName;
//    }

    public Map<String, String> getSessionInfos() {
        return sessionInfos;
    }

//    public void setSessionInfos(Map<String, String> sessionInfos) {
//        this.sessionInfos = sessionInfos;
//    }

    public void addKey(String key) {
        sessionInfos.put(key, "");
    }

    public void delKey(String key) {
        sessionInfos.remove(key);
    }

    public void addValue(String key, String value) {
        sessionInfos.replace(key, value);
    }

    public void addKeyValue(String key, String value) {
        sessionInfos.put(key, value);
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void updateSessionInfo(Map<String, String> Infos) {
        for (Map.Entry<String, String> entry : Infos.entrySet()) {
            if (sessionInfos.containsKey(entry.getKey())) {
                sessionInfos.replace(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthInfo)) return false;
        AuthInfo authInfo = (AuthInfo) o;

        return (getRoleName().equals(authInfo.getRoleName()) && getUserName().equals(authInfo.getUserName())) || (getSessionInfos().equals(authInfo.getSessionInfos()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoleName(), getUserName(), getSessionInfos());
    }
}
