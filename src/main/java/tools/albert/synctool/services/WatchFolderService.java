package tools.albert.synctool.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;


//@Service
public class WatchFolderService {

    int i = 0;
    public WatchFolderService(){
        System.out.println("Watcher angelegt!");
    }



    //@Async("threadPoolTaskExecutor")
    public void watch(String pfad, SyncService syncService, QuellService quellService, ZielService zielService){
        try {
            System.out.println("Watcher watched!");
            WatchService watchService
                    = FileSystems.getDefault().newWatchService();

            Path path = Paths.get(pfad);

            path.register(
                        watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY);




            WatchKey key = null;
            key = watchService.take();
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    i++;
                    System.out.println(i);
                    try {
                        for(File destination : zielService.getArrayListZiel()){
                            for (File source : quellService.getArrayListQuell()){
                                syncService.sync(source, destination, true);
                            }
                        }
                    } catch (IOException e) {

                    }
                    syncService.getLastFile().add(String.format("Folgende Datei %s wurde hinzugefügt", String.valueOf(event.context())));
                    System.out.println(
                            "Event kind:" + event.kind()
                                    + ". File affected: " + event.context() + ".");
                }
                key.reset();
            }
        } catch (IOException e) {
            System.out.println(e.fillInStackTrace());
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            System.out.println(e.fillInStackTrace());
            throw new RuntimeException(e);
        }
    }
}
