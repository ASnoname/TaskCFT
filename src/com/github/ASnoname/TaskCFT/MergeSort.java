package com.github.ASnoname.TaskCFT;

import com.github.ASnoname.TaskCFT.configure.Configure;
import com.github.ASnoname.TaskCFT.inputFile.InputFile;
import com.github.ASnoname.TaskCFT.outputFile.OutputFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.stream.Stream;

public final class MergeSort {

    public static void main(String[] args){

        try {
            runSort(new Configure(args));
        }
        catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
        catch (SecurityException | UnsupportedOperationException e){
            System.out.println("Not access file / directory");
        }
        catch (IOException e){
            System.out.println("I/O error");
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static void runSort(Configure configure) throws IOException{

        getStreamInputFiles(configure)
                .forEach(path -> {
                    try {
                        new InputFile(path, configure)
                                .sortFile(configure);
                    } catch (IOException e) {
                        System.out.println("I/O error");
                    }
                });

        new OutputFile(configure.getOutFile(), configure.getInFiles())
                .mergeFiles(configure);

        Files.walk(Paths.get(InputFile.TEMP_DIRECTORY))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    private static Stream<Path> getStreamInputFiles(Configure configure){

        return configure
                .getInFiles()
                .stream();
    }
}