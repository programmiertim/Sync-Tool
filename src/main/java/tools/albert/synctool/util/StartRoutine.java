package tools.albert.synctool.util;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static tools.albert.synctool.controller.RestController.*;

@Component
public class StartRoutine {

    @EventListener(ApplicationReadyEvent.class)
    public void loadLists() {

        try {
            BufferedReader reader = new BufferedReader(new FileReader("bereitsKopierteDateien.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] lineToken = line.split(":");
                syncService.getMapAlreadySync().put(lineToken[0],new NoSyncFile(lineToken[1]));
            }
            reader.close();
            logger.info("Liste der bereits kopierter Dateien wurde geladen");
        } catch (IOException e) {

        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader("erledigteDateien.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                syncService.getLastFile().add(line);
            }
            reader.close();
            logger.info("Liste der erledigten Dateien wurde geladen");
        } catch (IOException e) {

        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader("quellenListe.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                quellService.getArrayListQuell().add(new File(line));
                quellService.getArrayListQuellString().add(line);
            }
            reader.close();
            logger.info("Quellliste wurde geladen");
        } catch (IOException e) {

        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader("zielListe.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                zielService.getArrayListZiel().add(new NoSyncFile(line));
                zielService.getArrayListZielString().add(line);
            }
            reader.close();
            logger.info("Zielliste wurde geladen");
        } catch (IOException e) {

        }
    }
}
