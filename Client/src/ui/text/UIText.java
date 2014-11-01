package ui.text;

/**
 * Text Interface
 *
 * Created by ©OmniBox on 01-11-2014.
 */
public class UIText {

    /**
     * This program is intended to receive:
     *      - port
     *      - serverIpAddress [OPTIONAL]
     *      - directoryToSaveFilesTo [OPTIONAL]
     *
     * @param args
     */
    public static void main (String[] args) {
        if (args.length < 1) {
            System.out.println("Missing arguments! Should be something like this...\njava UIText port [ServerIP] [LocalDirectory]");
            System.exit(0);
        }
    }
}
