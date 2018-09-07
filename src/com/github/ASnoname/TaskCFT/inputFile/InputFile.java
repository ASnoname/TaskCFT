package com.github.ASnoname.TaskCFT.inputFile;

import com.github.ASnoname.TaskCFT.outputFile.OutputFile;
import com.github.ASnoname.TaskCFT.configure.Configure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.pow;

public class InputFile {

    public static final long MAX_SIZE_FILE = 8_388_608L * 100;
    public static final String TEMP_DIRECTORY = Configure.INPUT_DIRECTORY + "temp/";

    static {
        try {
            Files.createDirectory(Paths.get(TEMP_DIRECTORY));
        } catch (IOException e) {
            System.out.println("I/O error");
        }
    }

    private Path inputPath;
    private Path directory;
    private long averageCountLines;

    public InputFile(Path path, Configure configure) throws IOException {
        this.inputPath = path;
        this.directory = createDirectory(path);
        this.averageCountLines = calculateAverageCountLines(path, configure);
    }

    private Stream<String> getLines(Configure configure) throws IOException {
        return configure
                .filterLines(Files.lines(inputPath));
    }

    private long calculateAverageCountLines(Path path, Configure configure) throws IOException{

        return getLines(configure).count() / ((Files.size(path) / MAX_SIZE_FILE) + 1);
    }

    private Path createDirectory(Path path) throws IOException {
        return Files
                .createDirectory(Paths.get(TEMP_DIRECTORY + "folder" + path.getFileName() + "/"));
    }

    public void sortFile(Configure configure) throws IOException {

        long countLines = getLines(configure).count();
        int i;

        for (i = 0; averageCountLines < countLines; i++, countLines -= averageCountLines){

            Files.write(getNewFile(i), mergeSort(getNextLines(i,averageCountLines, configure), configure));
        }
        Files.write(getNewFile(i), mergeSort(getNextLines(i, countLines, configure), configure));

        new OutputFile(inputPath, Files.list(directory).collect(Collectors.toList()))
                .mergeFiles(configure);
    }

    private List<String> mergeSort(List<String> listLines, Configure configure) {

        if (listLines.isEmpty()){
            return listLines;
        }

        Queue<List<Integer>> pointers = getPointers(listLines.size());
        List<String> tempListLines = new ArrayList<>(listLines);

        pointers.forEach( p -> {

            MergeLines mergeLines = new MergeLines(configure)
                    .builder()
                    .left(p.get(0))
                    .leftLimit(p.get(1))
                    .right(p.get(1))
                    .rightLimit(getRightLimit(listLines, p))
                    .list(listLines)
                    .tempList(tempListLines)
                    .getMergeLines();

            sort(mergeLines);
        });

        return listLines;
    }

    private int getRightLimit(List<String> listLines, List<Integer> p) {

        if (p.get(1) - p.get(0) > listLines.size() - p.get(1)){
            return listLines.size();
        }
        else {
            return 2 * p.get(1) - p.get(0);
        }
    }

    private void sort(MergeLines mergeLines) {

        int left = mergeLines.getLeft();
        int right = mergeLines.getRight();

        List<String> list = mergeLines.getList();
        List<String> tempList = mergeLines.getTempList();

        for (int i = mergeLines.getLeft(); i < mergeLines.getRightLimit(); i++) {

            if (right < mergeLines.getRightLimit()) {

                if (left < mergeLines.getLeftLimit()) {
                    if ((mergeLines.getComparator().compare(list.get(left), list.get(right)) >= 0) ^ mergeLines.isUp()) {
                        tempList.set(i, list.get(left));
                        left++;
                    } else {
                        tempList.set(i, list.get(right));
                        right++;
                    }
                } else {
                    tempList.set(i, list.get(right));
                    right++;
                }
            } else {
                tempList.set(i, list.get(left));
                left++;
            }
        }

        for (int i = mergeLines.getLeft(); i < mergeLines.getRightLimit(); i++){
            list.set(i,tempList.get(i));
        }
    }

    private Queue<List<Integer>> getPointers(int listSize) {

        Queue<List<Integer>> pointers = new ArrayDeque<>();

        int left;
        int right;
        int diff;
        int preDiff;
        int i;

        if (listSize == 1){
            pointers.add(getUnmodifiableList(0,0));
            return pointers;
        }

        for (i = 0, diff = 1, preDiff = 0; diff <= listSize; diff *= 2, preDiff = getPreDiff(i,preDiff), i++){

            for (left = 1, right = 2 + preDiff; right <= listSize && left <= listSize; left += 2 * diff, right = getRight(right, diff, listSize, left)){

                pointers.add(getUnmodifiableList(left-1,right-1));
            }
        }
        return pointers;
    }

    private List<Integer> getUnmodifiableList(int left, int right) {

        List<Integer> list = new ArrayList<>(2);
        list.add(left);
        list.add(right);

        return Collections.unmodifiableList(list);
    }

    private int getPreDiff(int i, int preDiff) {
        if (preDiff == 0){
            return 1;
        }
        else {
            return (int) (preDiff + pow(2,i));
        }
    }

    private int getRight(int right, int diff, int listSize, int left) {

        if (right + 2 * diff > listSize){
            return left;
        }
        else {
            return right + 2 * diff;
        }
    }

    private List<String> getNextLines(int i, long size, Configure configure) throws IOException {
        return getLines(configure)
                .skip(i*averageCountLines)
                .limit(size)
                .collect(Collectors.toList());
    }

    private Path getNewFile(int i) throws IOException {
        return Files
                .createFile(Paths.get(directory + "/" + Long.toString(i) + ".txt"));
    }
}