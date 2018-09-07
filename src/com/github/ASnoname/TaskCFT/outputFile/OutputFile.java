package com.github.ASnoname.TaskCFT.outputFile;

import com.github.ASnoname.TaskCFT.configure.Configure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.pow;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;

public class OutputFile {

    private long SIZE_BUFF_FOR_WRITE = MergeFiles.MAX_SIZE_BUFF_FILE * 2;

    private Path outPath;
    private Path directory;
    private List<Path> listPaths;

    public OutputFile(Path path, List<Path> listPaths) {
        this.directory = getDirectory(listPaths.get(0));
        this.listPaths = listPaths;
        this.outPath = path;
    }

    private Path getDirectory(Path path) {
        return Paths.get(path.toString().substring(0, path.toString().lastIndexOf('/')));
    }

    public void mergeFiles(Configure configure) throws IOException {

        for (List<Integer> pointer : getPointers(listPaths.size())) {
            sort(new MergeFiles(listPaths.get(pointer.get(0)), listPaths.get(pointer.get(1)), createOutFile(), configure));
        }

        Files
                .walk(directory, 1)
                .filter(p -> !Files.isDirectory(p))
                .findFirst()
                .ifPresent(p -> {
                    try {
                        Files.move(p, outPath, ATOMIC_MOVE);
                    } catch (IOException e) {
                        System.out.println("I/O error");
                    }
                } );
    }

    private void sort(MergeFiles mergeFiles) throws IOException {

        if (mergeFiles.getFirstInFile().equals(mergeFiles.getSecondInFile())){
            Files.deleteIfExists(mergeFiles.getOutFile());
            return;
        }

        merge(mergeFiles);
        Files.deleteIfExists(mergeFiles.getFirstInFile());
        Files.deleteIfExists(mergeFiles.getSecondInFile());
        Files.move(mergeFiles.getOutFile(), mergeFiles.getFirstInFile());
    }

    private Path createOutFile() throws IOException {
        return Files.createFile( Paths.get(directory.toString() + "/" + "temp.txt"));
    }

    private void merge(MergeFiles mergeFiles) throws IOException {

//        long sumSizes = Files.size(mergeFiles.getFirstInFile()) + Files.size(mergeFiles.getSecondInFile());
//        long countIteration = (sumSizes / (MergeFiles.MAX_SIZE_BUFF_FILE * 2)) + 1;
//
//        for (long i = 0; i < countIteration; i++){
//            Files.write(mergeFiles.getOutFile(), fillOutList(mergeFiles));
//        }
    }

//    private List<String> fillOutList(MergeFiles mergeFiles) throws IOException {
//
//        List<String> outList = new ArrayList<>((int)(MergeFiles.MAX_SIZE_BUFF_FILE * 2));
//
//        long leftLimit = mergeFiles.getBuffSize().get(0);
//        long rightLimit = mergeFiles.getBuffSize().get(0);
//        long lastIndexLeft = Files.lines(mergeFiles.getFirstInFile()).count() - 1;
//        long lastIndexRight = Files.lines(mergeFiles.getSecondInFile()).count() - 1;
//
//        while (outList.size() < SIZE_BUFF_FOR_WRITE && (leftLimit < lastIndexLeft || rightLimit < lastIndexRight)){
//
//
//        }
//
//        List<String> leftList = getLines(mergeFiles.getFirstInFile(), i, mergeFiles);
//        List<String> rightList = getLines(mergeFiles.getSecondInFile(), i, mergeFiles);
//
//        int left = 0;
//        int right = 0;
//
//        long limit = mergeFiles.getBuffSize().get(i);
//
//        for (int j = 0; j < 2 * limit; j++) {
//
//            if (right < limit) {
//
//                if (left < limit) {
//                    if ((mergeFiles.getComparator().compare(leftList.get(left), rightList.get(right)) >= 0) ^ mergeFiles.isUp()) {
//                        outList.add(j, leftList.get(left));
//                        left++;
//                    } else {
//                        outList.add(j, rightList.get(right));
//                        right++;
//                    }
//                } else {
//                    outList.add(j, rightList.get(right));
//                    right++;
//                }
//            } else {
//                outList.add(j, leftList.get(left));
//                left++;
//            }
//        }
//        return outList;
//    }

    private List<String> getLines(Path path, int number, MergeFiles mergeFiles) throws IOException {

        if (number >= mergeFiles.getBuffSize().size()){
            throw new IllegalAccessError("Inner error");
        }

        long lines = 0;
        for (int i = 0; i < number; i++){
            lines += mergeFiles.getBuffSize().get(i);
        }
        return Files
                .lines(path)
                .skip(lines)
                .limit(mergeFiles.getBuffSize().get(number))
                .collect(Collectors.toList());
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
}