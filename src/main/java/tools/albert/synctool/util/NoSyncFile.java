package tools.albert.synctool.util;

import java.io.File;
import java.net.URI;

public class NoSyncFile extends File {
    private boolean noSync = false;

    public NoSyncFile(String pathname) {
        super(pathname);
    }

    public NoSyncFile(String parent, String child) {
        super(parent, child);
    }

    public NoSyncFile(File parent, String child) {
        super(parent, child);
    }

    public NoSyncFile(URI uri) {
        super(uri);
    }

    public boolean isNoSync() {
        return noSync;
    }

    public void setNoSync(boolean noSync) {
        this.noSync = noSync;
    }
}
