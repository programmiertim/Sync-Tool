package tools.albert.synctool.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import static tools.albert.synctool.controller.RestController.*;


@Service
public class WatchFolderService {

    int i = 0;
    String pfad = "";

    WatchService watchService = null;
    public WatchFolderService(){
        System.out.println("Watcher angelegt!");
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Async("threadPoolTaskExecutor")
    public void setPfad(String pfad) {
        register(pfad);
    }


    private void register(String pfad) {
        try {
            Path path = Paths.get(pfad);

            path.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            e.printStackTrace();
        }
        watch();
    }



    private void watch() {
        try {
            System.out.println("Watcher watched!");

            WatchKey key = null;
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
        } catch (InterruptedException e) {
            System.out.println(e.fillInStackTrace());
        }
    }
}
