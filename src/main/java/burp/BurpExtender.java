package burp;

import java.io.PrintWriter;

public class BurpExtender implements IBurpExtender {
    private static final String extenderName = "Auth info Manager";
    public static IExtensionHelpers helpers;
    public static IBurpExtenderCallbacks callbacks;
    public static PrintWriter stdout;
    public static PrintWriter stderr;
    private static final boolean DEBUG = false;

    public static void StdoutPrintln(String msg) {
        if (DEBUG) {
            stdout.println(msg);
        }
    }

    public static void StderrPrintln(String msg) {
        if (DEBUG) {
            stderr.println(msg);
        }
    }

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        helpers = callbacks.getHelpers();
        BurpExtender.callbacks = callbacks;
        stdout = new PrintWriter(callbacks.getStdout(), true);
        stderr = new PrintWriter(callbacks.getStderr(), true);

        callbacks.setExtensionName(extenderName);

        callbacks.registerContextMenuFactory(new Menu());

        stdout.println(extenderName);
    }
}
