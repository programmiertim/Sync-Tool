package tools.albert.synctool.services;

import lombok.Getter;
import org.springframework.stereotype.Service;
import tools.albert.synctool.util.NoSyncFile;

import java.io.File;
import java.util.ArrayList;

@Service
@Getter
public class ZielService {

    private ArrayList<NoSyncFile> arrayListZiel = new ArrayList<>();
    private ArrayList<String> arrayListZielString = new ArrayList<>();

    public void addZiel(String pfad){
        NoSyncFile dir = new NoSyncFile(pfad);
        arrayListZiel.add(dir);
        arrayListZielString.add(pfad);
    }
}
