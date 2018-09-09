package com.github.ASnoname.TaskCFT.outputFile;

import com.github.ASnoname.TaskCFT.configure.Configure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.pow;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardOpenOption.APPEND;

public class OutputFile {

    private int SIZE_BUFF_FOR_WRITE = (int) (MergeFiles.MAX_SIZE_BUFF_FILE * 2);

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

        //привести в порядок эти два метода и еще раз проверить все!

        int sizeOutList = getMaxBuffSize(mergeFiles) * 2;
        List<String> outList = new ArrayList<>(sizeOutList);

        int leftIterByBuffs = 0;
        int rightIterByBuffs = 0;

        List<String> leftList = getLines(mergeFiles.getSmallFile(), leftIterByBuffs, mergeFiles);
        List<String> rightList = getLines(mergeFiles.getBigFile(), rightIterByBuffs, mergeFiles);

        int leftLimit = leftList.size();
        int rightLimit = rightList.size();

        int left = 0;
        int right = 0;

        while (true){

            for (int j = 0; j < sizeOutList; j++) {

                if (right < rightLimit) {

                    if (left < leftLimit) {
                        if ((mergeFiles.getComparator().compare(leftList.get(left), rightList.get(right)) >= 0) ^ mergeFiles.isUp()) {
                            outList.add(j, leftList.get(left));
                            left++;
                        } else {
                            outList.add(j, rightList.get(right));
                            right++;
                        }
                    } else {
                        j--;
                        if (leftIterByBuffs + 1 < mergeFiles.getMinCountBuff()){
                            leftIterByBuffs++;
                            left = 0;
                            leftList = getLines(mergeFiles.getSmallFile(), leftIterByBuffs, mergeFiles);
                            leftLimit = leftList.size();
                        }
                        else {
                            Files.write(mergeFiles.getOutFile(), outList, APPEND);
                            finishMerge(mergeFiles.getBigFile(), mergeFiles.getOutFile(), right, rightIterByBuffs, mergeFiles);
                            return;
                        }
                    }
                } else {
                    j--;
                    if (rightIterByBuffs + 1 < mergeFiles.getBuffSize().size()){
                        rightIterByBuffs++;
                        right = 0;
                        rightList = getLines(mergeFiles.getBigFile(), rightIterByBuffs, mergeFiles);
                        rightLimit = rightList.size();
                    }
                    else {
                        Files.write(mergeFiles.getOutFile(), outList, APPEND);
                        finishMerge(mergeFiles.getSmallFile(), mergeFiles.getOutFile(), left, leftIterByBuffs, mergeFiles);
                        return;
                    }
                }
            }

            Files.write(mergeFiles.getOutFile(), outList, APPEND);
            outList.clear();
        }
    }

    private void finishMerge(Path inFile, Path outFile, int line, int numberBuff, MergeFiles mergeFiles) throws IOException {

        long currentLine = 0;
        for (int i = 0; i < numberBuff; i++){
            currentLine += mergeFiles.getBuffSize().get(i);
        }
        currentLine += line;

        long maxLine = mergeFiles.getBuffSize().get(numberBuff);

        Files.write(outFile, Files
                                    .lines(inFile)
                                    .skip(currentLine)
                                    .limit(maxLine - line)
                                    .collect(Collectors.toList()), APPEND);

        int max = mergeFiles.getMinCountBuff();

        if (inFile.equals(mergeFiles.getBigFile())){
            max = mergeFiles.getBuffSize().size();
        }

        numberBuff++;
        for (int i = numberBuff; i < max; i++){

            Files.write(outFile, getLines(inFile, i, mergeFiles), APPEND);
        }
    }

    private int getMaxBuffSize(MergeFiles mergeFiles) {

        long max = 0;
        for (int i = 0; i < mergeFiles.getBuffSize().size(); i++){
            if (mergeFiles.getBuffSize().get(i) > max){
                max = mergeFiles.getBuffSize().get(i);
            }
        }

        return (int) max;
    }

    private List<String> getLines(Path path, int numberBuff, MergeFiles mergeFiles) throws IOException {

        if (numberBuff >= mergeFiles.getBuffSize().size()){
            throw new IllegalAccessError("Inner error");
        }

        long lines = 0;
        for (int i = 0; i < numberBuff; i++){
            lines += mergeFiles.getBuffSize().get(i);
        }
        return Files
                .lines(path)
                .skip(lines)
                .limit(mergeFiles.getBuffSize().get(numberBuff))
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