package torrent.client;

import torrent.ArgsAndConsts;
import torrent.tracker.FilesInfo;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by the7winds on 09.04.16.
 */
public class Client {

    private static final Client INSTANCE = new Client();

    private static final String CMD_ERR_MESSAGE = "unknown command";
    private static final String LIST_CMD = "ls";
    private static final String UPLOAD_CMD = "add";
    private static final String DOWNLOAD_CMD = "ld";
    private static final String QUIT_CMD = "q";

    private ClientNetwork client;

    private Client() {
    }

    public static Client getInstance() {
        return INSTANCE;
    }

    public void main() throws IOException {
        client = new ClientImpl(ArgsAndConsts.port);
        client.connect(ArgsAndConsts.host);
        Notification.connected();

        while (true) {
            String line = System.console().readLine();
            String[] words = line.split("\\s");
            String cmd = words[0];
            String[] cmdArgs = Arrays.copyOfRange(words, 1, words.length);

            switch (cmd) {
                case LIST_CMD:
                    listHandle();
                    break;
                case UPLOAD_CMD:
                    uploadHandle(cmdArgs);
                    break;
                case DOWNLOAD_CMD:
                    downloadHandle(cmdArgs);
                    break;
                case QUIT_CMD:
                    System.exit(0);
                default:
                    System.out.println(CMD_ERR_MESSAGE);
            }
        }
    }

    void listHandle() {
        try {
            Collection<FilesInfo.FileInfo> list = client.list();
            System.out.printf("ID\tNAME\tSIZE\n");
            for (FilesInfo.FileInfo info : list) {
                System.out.printf("%d\t%s\t%d\n", info.id, info.name, info.size);
            }
        } catch (IOException e) {
            System.out.printf(e.getMessage());
        }
    }

    void uploadHandle(String[] cmdArgs) {
        try {
            int id = client.upload(new File(cmdArgs[0]));
            Notification.added(id);
        } catch (IOException e) {
            System.out.printf(e.getMessage());
        }
    }

    void downloadHandle(String[] cmdArgs) {
        int id = Integer.valueOf(cmdArgs[0]);
        String pathname = cmdArgs[1];
        client.download(id, pathname);
        Notification.downloaded();
    }
}
