package tools.albert.synctool.services;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;

@Service
@Getter
public class ZielService {

    private ArrayList<File> arrayListZiel = new ArrayList<>();
    private ArrayList<String> arrayListZielString = new ArrayList<>();

    public void addZiel(String pfad){
        File dir = new File(pfad);
        arrayListZiel.add(dir);
        arrayListZielString.add(pfad);
    }
}
