package com.github.ASnoname.TaskCFT.configure;

import com.github.ASnoname.TaskCFT.attributes.*;
import com.github.ASnoname.TaskCFT.attributes.Modes.EmptyLineMode;
import com.github.ASnoname.TaskCFT.attributes.Modes.SortMode;
import com.github.ASnoname.TaskCFT.attributes.Modes.SpaceMode;
import com.github.ASnoname.TaskCFT.attributes.Modes.TypeMode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Configure {

    public static final String INPUT_DIRECTORY = "input/";
    private static final String OUTPUT_DIRECTORY = "output/";
    private static final int COUNT_CODES = 4;

    private static Map<String, String> codeToName;

    static {
        codeToName = new HashMap<>(COUNT_CODES);
        codeToName.put( SortMode.ASCENDING.getCode(), SortMode.name);
        codeToName.put(SortMode.DECREASES.getCode(), SortMode.name);
        codeToName.put( TypeMode.STRING_TYPE.getCode(), TypeMode.name);
        codeToName.put(TypeMode.INTEGER_TYPE.getCode(), TypeMode.name);
    }

    private static Map<String, String> codeToValueEnum;

    static {
        codeToValueEnum = new HashMap<>(COUNT_CODES);
        codeToValueEnum.put(SortMode.ASCENDING.getCode(), SortMode.ASCENDING.name());
        codeToValueEnum.put(SortMode.DECREASES.getCode(), SortMode.DECREASES.name());
        codeToValueEnum.put(TypeMode.STRING_TYPE.getCode(), TypeMode.STRING_TYPE.name());
        codeToValueEnum.put(TypeMode.INTEGER_TYPE.getCode(), TypeMode.INTEGER_TYPE.name());
    }

    private Map<String, Attribute> attributes;
    private Path outFile;
    private List<Path> inFiles;

    public Path getOutFile() {
        return outFile;
    }

    public List<Path> getInFiles() {
        return inFiles;
    }

    {
        inFiles = new ArrayList<>();
        outFile = Paths.get("");
        attributes = new HashMap<>();
        defaultAttributes();
    }

    public Map<String, Attribute> getAttributes() {
        return attributes;
    }

    public Stream<String> filterLines(Stream<String> stream){

        return filterByEmptyLineMode(filterBySpaceMode(filterByTypeMode(stream)));
    }

    private Stream<String> filterByEmptyLineMode(Stream<String> stream) {

        if(attributes.get( EmptyLineMode.name).getValue().equals(EmptyLineMode.WITHOUT_EMPTY_LINE.name())){

            return EmptyLineMode.WITHOUT_EMPTY_LINE.doFilter(stream);
        }
        else {
            return EmptyLineMode.WITH_EMPTY_LINE.doFilter(stream);
        }
    }

    private Stream<String> filterBySpaceMode(Stream<String> stream) {

        if(attributes.get( SpaceMode.name).getValue().equals(SpaceMode.WITHOUT_SPACE.name())){

            return SpaceMode.WITHOUT_SPACE.doFilter(stream);
        }
        else {
            return SpaceMode.WITH_SPACE.doFilter(stream);
        }
    }

    private Stream<String> filterByTypeMode(Stream<String> stream) {

        if(attributes.get(TypeMode.name).getValue().equals(TypeMode.INTEGER_TYPE.name())){

            return TypeMode.INTEGER_TYPE.doFilter(stream);
        }
        else {
            return TypeMode.STRING_TYPE.doFilter(stream);
        }
    }

    private void defaultAttributes() {

        //Inner fields
        attributes.put(SpaceMode.name, new Attribute(SpaceMode.WITHOUT_SPACE.name()));
        attributes.put(EmptyLineMode.name, new Attribute(EmptyLineMode.WITHOUT_EMPTY_LINE.name()));

        //Optional fields
        attributes.put(SortMode.name, new Attribute(null));

        //Required fields
        attributes.put(TypeMode.name, new Attribute(null));
    }

    public Configure(String[] args) throws IOException {

        if (args == null || args.length < 3){
            throw new IllegalArgumentException("Not correct input data");
        }

        fillAttributes(args);

        List<String> files = Arrays
                .stream(args)
                .filter(arg -> !isAttribute(arg))
                .collect(Collectors.toList());

        outFile = Paths.get(OUTPUT_DIRECTORY + files.get(0));

        files.remove(0);

        inFiles = files
                .stream()
                .map(name -> Paths.get(INPUT_DIRECTORY + name))
                .collect(Collectors.toList());

        int before = inFiles.size();
        int after = ((int) inFiles
                                    .stream()
                                    .filter( p -> Files.exists( p ) )
                                    .count());

        if (before != after){
            throw new IllegalArgumentException("Not correct input data");
        }

        Files.deleteIfExists(outFile);
        Files.createFile(outFile);

        if (inFiles.size() < 1){
            throw new IllegalArgumentException("Not found input files");
        }
    }

    private void fillAttributes(String[] args){

        Arrays
                .stream(args)
                .filter(this::isAttribute)
                .forEach(arg -> {
                    String value = codeToName.get(arg);
                    if (value == null){
                        throw new IllegalArgumentException("Argument " + arg + " not found");
                    }

                    Attribute attribute = attributes.get(value);

                    if (attribute.getValue() == null){
                        attribute.setValue(codeToValueEnum.get(arg));
                    }
                    else {
                        throw new IllegalArgumentException("This argument has already been processed");
                    }
                });

        fillOptionalAttributes();
        checkRequiredAttributes();
    }

    private void checkRequiredAttributes() {

        //TypeMode
        if (attributes.get(TypeMode.name).getValue() == null){
            throw new IllegalArgumentException("Data type argument not found");
        }
    }

    private void fillOptionalAttributes() {

        //SortMode
        if (attributes.get(SortMode.name).getValue() == null){
            attributes.put(SortMode.name, new Attribute(SortMode.ASCENDING.name()));
        }
    }

    private boolean isAttribute(String arg) {

        return codeToName
                .keySet()
                .stream()
                .filter(code -> code.equals(arg))
                .count() == 1;
    }
}