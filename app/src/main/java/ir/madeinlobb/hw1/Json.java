package ir.madeinlobb.hw1;


import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Json<T> {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<String> getAllFilePathInDirectory(String directoryPath) {
        try {
            Stream<Path> walk = Files.walk(Paths.get(directoryPath));
            List<String> result = walk.map(x -> x.toString()).filter(f -> f.endsWith(".json")).collect(Collectors.toList());
            return result;
        } catch (IOException e) {

        }
        return null;
    }

    public T getDigitalCoinObject(String path) {
        Gson gson = new Gson();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(path));
        } catch (FileNotFoundException e) {

        }
        DigitalCoin digitalCoin = gson.fromJson(reader, DigitalCoin.class);
        T t = (T) digitalCoin;
        return t;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<T> getAllJson(String folderPath) {
        ArrayList<T> all = new ArrayList<>();
        for (String s : getAllFilePathInDirectory(folderPath)) {
            all.add(getDigitalCoinObject(s));
        }
        return all;
    }
}

