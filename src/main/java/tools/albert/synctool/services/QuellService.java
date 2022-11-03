package tools.albert.synctool.services;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;

@Service
@Getter
public class QuellService {

    private ArrayList<File> arrayListQuell = new ArrayList<>();
    private ArrayList<String> arrayListQuellString = new ArrayList<>();

    public void addQuelle(String pfad){
        File dir = new File(pfad);
        arrayListQuell.add(dir);
        arrayListQuellString.add(pfad);
    }

    public  void deleteQuelle(String pfad){
        File dir = new File(pfad);
        arrayListQuell.remove(dir);
        arrayListQuellString.remove(pfad);
    }
}
