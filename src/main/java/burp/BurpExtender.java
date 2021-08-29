package burp;

import java.io.PrintWriter;

public class BurpExtender implements IBurpExtender {
    public static IExtensionHelpers helpers;
    public static IBurpExtenderCallbacks callbacks;
    public static PrintWriter stdout;
    public static PrintWriter stderr;
    private static final String extenderName = "Auth info Manager";

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        this.helpers = callbacks.getHelpers();
        this.callbacks = callbacks;
        this.stdout = new PrintWriter(callbacks.getStdout(), true);
        this.stderr = new PrintWriter(callbacks.getStderr(), true);

        callbacks.setExtensionName(extenderName);

        callbacks.registerContextMenuFactory(new Menu());

        stdout.println(extenderName);
    }
}
