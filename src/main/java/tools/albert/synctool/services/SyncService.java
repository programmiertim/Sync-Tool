package tools.albert.synctool.services;

import lombok.Getter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tools.albert.synctool.util.NoSyncFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.*;

@Service
@Getter
public class SyncService {

    private ArrayList<String> lastFile = new ArrayList<>();

    private HashMap<String, NoSyncFile> mapAlreadySync = new HashMap<>();

    private static final int FAT_PRECISION = 2000;
    public static final long DEFAULT_COPY_BUFFER_SIZE = 16 * 1024 * 1024; // 16 MB


    @Async("threadPoolTaskExecutor")
    public boolean sync(File source, NoSyncFile destination, boolean smart) throws IOException {
        return synchronize(source, destination, smart, DEFAULT_COPY_BUFFER_SIZE);
    }

    @Async("threadPoolTaskExecutor")
    public boolean synchronize(File source, NoSyncFile destination, boolean smart, long chunkSize) throws IOException {
        boolean newSync = false;
        if (chunkSize <= 0) {
            System.out.println("Chunk size must be positive: using default value.");
            chunkSize = DEFAULT_COPY_BUFFER_SIZE;
        }
        if (source.isDirectory()) {
            if (!destination.exists()) {
                if (!destination.mkdirs()) {
                    throw new IOException("Could not create path " + destination);
                }
            } else if (!destination.isDirectory()) {
                throw new IOException(
                        "Source and Destination not of the same type:"
                                + source.getCanonicalPath() + " , " + destination.getCanonicalPath()
                );
            }
            String[] sources = source.list();
            Set<String> srcNames = new HashSet<String>(Arrays.asList(sources));
            String[] dests = destination.list();

            //delete files not present in source
            for (String fileName : dests) {
                if (!srcNames.contains(fileName)) {
                    //delete(new File(destination, fileName));
                }
            }
            //copy each file from source
            for (String fileName : sources) {
                File srcFile = new File(source, fileName);
                NoSyncFile destFile = new NoSyncFile(destination, fileName);
                synchronize(srcFile, destFile, smart, chunkSize);
            }
        } else {
            if (destination.exists() && destination.isDirectory()) {
                //delete(destination);
            }

                if (!mapAlreadySync.containsKey(source.getName())) {
                    copyFile(source, destination, chunkSize);
                    mapAlreadySync.put(destination.getName(),destination);
                    newSync = true;
                }

        }
        return newSync;
    }




    private void copyFile(File srcFile, File destFile, long chunkSize) throws IOException {
        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            is = new FileInputStream(srcFile);
            FileChannel iChannel = is.getChannel();
            os = new FileOutputStream(destFile, false);
            FileChannel oChannel = os.getChannel();
            long doneBytes = 0L;
            long todoBytes = srcFile.length();
            while (todoBytes != 0L) {
                long iterationBytes = Math.min(todoBytes, chunkSize);
                long transferredLength = oChannel.transferFrom(iChannel, doneBytes, iterationBytes);
                if (iterationBytes != transferredLength) {
                    throw new IOException(
                            "Error during file transfer: expected "
                                    + iterationBytes + " bytes, only " + transferredLength + " bytes copied."
                    );
                }
                doneBytes += transferredLength;
                todoBytes -= transferredLength;
            }
            lastFile.add(String.format("File: %s nach %s", srcFile.getAbsolutePath(), destFile.getAbsolutePath()));
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }
}
