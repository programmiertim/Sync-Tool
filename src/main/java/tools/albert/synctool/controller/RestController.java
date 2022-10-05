package tools.albert.synctool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import tools.albert.synctool.services.QuellService;
import tools.albert.synctool.services.SyncService;
import tools.albert.synctool.services.WatchFolderService;
import tools.albert.synctool.services.ZielService;

import java.beans.Transient;
import java.io.File;
import java.io.IOException;

@Controller
public class RestController {

    public static QuellService quellService = new QuellService();

    public static ZielService zielService = new ZielService();

    public static SyncService syncService = new SyncService();

    @Autowired
    WatchFolderService watchFolderService;




    @GetMapping(value = {"/", "index.html"})
    public ModelAndView index(){
        return new ModelAndView("index");
    }

    @PostMapping(value = {"/indexQuellpfad", "index.html"})
    public ModelAndView postQuellpfad(@RequestParam(name = "Quellpfad")String pfad){
        ModelAndView modelAndView = new ModelAndView("index");

        quellService.addQuelle(pfad);
        watchFolderService.setPfad(pfad);
        try {
            for(File destination : zielService.getArrayListZiel()){
                for (File source : quellService.getArrayListQuell()){
                    syncService.sync(source, destination, true);
                }
            }
        } catch (IOException e) {
            modelAndView.addObject("fehler", "Es lief etwas schief!");
        }

        modelAndView.addObject("quellen", quellService.getArrayListQuellString());
        modelAndView.addObject("ziele", zielService.getArrayListZielString());
        modelAndView.addObject("lastFile", syncService.getLastFile());


        return modelAndView;
    }

    @PostMapping(value = {"/indexZielpfad", "index.html"})
    public ModelAndView postZielpfad(@RequestParam(name = "Zielpfad")String pfad){
        ModelAndView modelAndView = new ModelAndView("index");

        zielService.addZiel(pfad);
        try {
            for(File destination : zielService.getArrayListZiel()){
                for (File source : quellService.getArrayListQuell()){
                    syncService.sync(source, destination, true);
                }
            }
        } catch (IOException e) {
            modelAndView.addObject("fehler", "Es lief etwas schief!");
        }

        modelAndView.addObject("quellen", quellService.getArrayListQuellString());
        modelAndView.addObject("ziele", zielService.getArrayListZielString());
        modelAndView.addObject("lastFile", syncService.getLastFile());

        return modelAndView;
    }
}
