package com.lftc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args){
        try {
            File f = new File(args[0]);
            Path path = f.toPath();
            byte[] bFile = Files.readAllBytes(path);
            LexicAnalyser al = new LexicAnalyser();
            al.analiseFile(bFile);

    }catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    }
}
