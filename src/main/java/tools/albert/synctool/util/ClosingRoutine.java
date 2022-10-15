package tools.albert.synctool.util;

import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

import java.io.FileWriter;
import java.util.Map;

import static tools.albert.synctool.controller.RestController.*;

@Component
public class ClosingRoutine {

    @PreDestroy
    public void onDestroy() throws Exception {

        FileWriter writer = new FileWriter("quellenListe.txt");
        for(String path : quellService.getArrayListQuellString()){
            writer.write(path + System.lineSeparator());
        }
        writer.close();
        logger.info("Quellenliste wurde gespeichert");
        writer = new FileWriter("zielListe.txt");
        for(String path : zielService.getArrayListZielString()){
            writer.write(path + System.lineSeparator());
        }
        writer.close();
        logger.info("Zielliste wurde gespeichert");
        writer = new FileWriter("erledigteDateien.txt");
        for(String path : syncService.getLastFile()){
            writer.write(path + System.lineSeparator());
        }
        writer.close();
        logger.info("Kopierte Dateien wurde gespeichert");
        writer = new FileWriter("bereitsKopierteDateien.txt");

        for(Map.Entry<String, NoSyncFile> entry : syncService.getMapAlreadySync().entrySet()){
            writer.write(entry.getKey() + ":"
                    + entry.getValue().getAbsolutePath() + System.lineSeparator());
        }
        writer.close();
        logger.info("Dateienliste wurde gespeichert");
    }
}
