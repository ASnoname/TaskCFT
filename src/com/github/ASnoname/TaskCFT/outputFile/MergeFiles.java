package com.github.ASnoname.TaskCFT.outputFile;

import com.github.ASnoname.TaskCFT.attributes.Modes.SortMode;
import com.github.ASnoname.TaskCFT.attributes.Modes.TypeMode;
import com.github.ASnoname.TaskCFT.configure.Configure;
import com.github.ASnoname.TaskCFT.configure.comparators.IntegerComparator;
import com.github.ASnoname.TaskCFT.configure.comparators.StringComparator;
import com.github.ASnoname.TaskCFT.inputFile.InputFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MergeFiles {

    public static final long MAX_SIZE_BUFF_FILE = InputFile.MAX_SIZE_FILE / 4;

    private Path firstInFile;
    private Path secondInFile;
    private Path outFile;
    private boolean firstMoreSecond;
    private List<Long> buffSize;
    private int minCountBuff;
    private boolean isUp;
    private Comparator<String> comparator;

    public MergeFiles(Path firstInFile, Path secondInFile, Path outFile, Configure configure) throws IOException {
        this.firstInFile = firstInFile;
        this.secondInFile = secondInFile;
        this.outFile = outFile;
        this.firstMoreSecond = initFirstMoreSecond(firstInFile, secondInFile);
        this.isUp = initIsUp(configure);
        this.comparator = initComparator(configure);
        this.buffSize = new ArrayList<>(0);
        this.minCountBuff = initMinCountBuff(firstInFile, secondInFile);
        initBuffSize(firstInFile, secondInFile);
    }

    private Comparator<String> initComparator(Configure configure) {
        if (configure.getAttributes().get( TypeMode.name).getValue().equals( TypeMode.INTEGER_TYPE.name())){
            return new IntegerComparator();
        }
        else {
            return new StringComparator();
        }
    }

    private boolean initIsUp(Configure configure) {
        return configure
                .getAttributes()
                .get( SortMode.name)
                .getValue()
                .equals(SortMode.ASCENDING.name());
    }

    private int initMinCountBuff(Path firstInFile, Path secondInFile) throws IOException{

        Path smallFile = getSmallFile(firstInFile, secondInFile);

        if (Files.size(smallFile) < MAX_SIZE_BUFF_FILE){
            this.buffSize.add(Files.lines(smallFile).count());
            return 1;
        }
        else {
            Long countBuffs = (Files.size(smallFile) / MAX_SIZE_BUFF_FILE) + 1;
            long lines = Files.lines(smallFile).count() / countBuffs;

            for (long i = 0; i < countBuffs-1; i++){
                this.buffSize.add(lines);
            }
            this.buffSize.add(Files.lines(smallFile).count() - (countBuffs-1)*lines);

            return countBuffs.intValue();
        }
    }

    private void initBuffSize(Path firstInFile, Path secondInFile) throws IOException {

        Path bigFile = getBigFile(firstInFile, secondInFile);
        long lines = 0;

        for (Long buff : this.buffSize) {
            lines += buff;
        }

        long countBuffs = (Files.size(bigFile) / MAX_SIZE_BUFF_FILE) + 1;

        long buffLines = (Files.lines(bigFile).count() - lines) / countBuffs;

        for (long i = 0; i < countBuffs-1; i++){
            this.buffSize.add(buffLines);
        }
        this.buffSize.add(Files.lines(bigFile).count() - lines - (countBuffs-1)*buffLines);
    }

    public Path getBigFile(){
        return getBigFile(firstInFile, secondInFile);
    }

    private Path getBigFile(Path firstInFile, Path secondInFile) {
        if (firstMoreSecond){
            return firstInFile;
        }
        else {
            return secondInFile;
        }
    }

    private Path getSmallFile(Path firstInFile, Path secondInFile) {

        if (firstMoreSecond){
            return secondInFile;
        }
        else {
            return firstInFile;
        }
    }

    private boolean initFirstMoreSecond(Path firstInFile, Path secondInFile) throws IOException {
        return Files.lines(firstInFile).count() > Files.lines(secondInFile).count();
    }

    public Path getFirstInFile() {
        return firstInFile;
    }

    public Path getSecondInFile() {
        return secondInFile;
    }

    public Path getOutFile() {
        return outFile;
    }

    public List<Long> getBuffSize() {
        return buffSize;
    }

    public int getMinCountBuff() {
        return minCountBuff;
    }

    public boolean isUp() {
        return isUp;
    }

    public Comparator<String> getComparator() {
        return comparator;
    }
}