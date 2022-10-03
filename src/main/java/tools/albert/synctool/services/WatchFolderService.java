package tools.albert.synctool.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;


@Service
public class WatchFolderService {




    @Async("threadPoolTaskExecutor")
    public void watch(SyncService syncService, QuellService quellService, ZielService zielService){
        try {
            WatchService watchService
                    = FileSystems.getDefault().newWatchService();

            List<Path> pathList = new ArrayList<>();
            for(String pfad : quellService.getArrayListQuellString()){
                pathList.add(Paths.get(pfad));
            }

            for(Path path : pathList){
                path.register(
                        watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY);
            }



            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
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
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
