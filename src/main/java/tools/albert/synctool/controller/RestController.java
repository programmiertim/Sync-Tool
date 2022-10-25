package tools.albert.synctool.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import tools.albert.synctool.SyncToolApplication;
import tools.albert.synctool.config.SpringAsyncConfig;
import tools.albert.synctool.services.QuellService;
import tools.albert.synctool.services.SyncService;
import tools.albert.synctool.services.ZielService;
import tools.albert.synctool.util.NoSyncFile;

import java.io.File;
import java.io.IOException;

@Controller
public class RestController {

    public static Logger logger= LoggerFactory.getLogger(SyncToolApplication.class);

    public static QuellService quellService = new QuellService();


    public static ZielService zielService = new ZielService();


    public static SyncService syncService = new SyncService();

    private boolean syncActiv = false;


    @Autowired
    SpringAsyncConfig springAsyncConfig;




    @GetMapping(value = {"/", "index.html"})
    public ModelAndView index(){
        logger.info("index aufgerufen");
        ModelAndView modelAndView = new ModelAndView("index");

        if (!syncActiv) {
            try {
                for(NoSyncFile destination : zielService.getArrayListZiel()){
                    for (File source : quellService.getArrayListQuell()){
                        syncService.sync(source, destination, true);
                    }
                }
            } catch (IOException e) {
                modelAndView.addObject("fehler", "Es lief etwas schief!");
            }
        }
        modelAndView.addObject("quellen", quellService.getArrayListQuellString());
        logger.info("Quellen wurden in die View geladen");
        modelAndView.addObject("ziele", zielService.getArrayListZielString());
        logger.info("Ziele wurden in die View geladen");
        modelAndView.addObject("lastFile", syncService.getLastFile());
        logger.info("Liste der letzten Dateien wurde in die View geladen");

        return modelAndView;
    }

    @PostMapping(value = {"/indexQuellpfad", "index.html"})
    public ModelAndView postQuellpfad(@RequestParam(name = "Quellpfad")String pfad){
        if (pfad.compareTo("")!=0) {
            quellService.addQuelle(pfad);
            logger.info(pfad + " wurde als Quelle hinzugefügt");
        }
        return index();
    }

    @PostMapping(value = {"/", "index.html"})
    public ModelAndView postManuellesSync(){
        return index();
    }




    @PostMapping(value = {"/indexZielpfad", "index.html"})
    public ModelAndView postZielpfad(@RequestParam(name = "Zielpfad")String pfad){
        if (pfad.compareTo("")!=0) {
            zielService.addZiel(pfad);
            logger.info(pfad + " wurde als Ziel hinzugefügt");
            springAsyncConfig.autoSync();
            logger.info("Autosync wurde gestartet");
            syncActiv = true;
        }
        return index();
    }

    @PostMapping(value = {"/util/synctime", "index.html"})
    public ModelAndView postZielpfad(@RequestParam(name = "synctimer")Integer pfad){
        if (!pfad.equals(0)) {
            SpringAsyncConfig.setSyncTimer(String.valueOf(pfad));
        }
        return index();
    }

    @PostMapping(value = {"/actuator/shutdown", "index.html"})
    public void exit(){
        System.exit(0);
    }




}
