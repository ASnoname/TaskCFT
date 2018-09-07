package com.github.ASnoname.TaskCFT.inputFile;

import java.util.List;

public class MergeLinesBuilder {

    private MergeLines mergeLines;

    public MergeLinesBuilder left(Integer left){
        mergeLines.setLeft(left);
        return this;
    }

    public MergeLinesBuilder leftLimit(int leftLimit){
        mergeLines.setLeftLimit(leftLimit);
        return this;
    }

    public MergeLinesBuilder right(Integer right){
        mergeLines.setRight(right);
        return this;
    }

    public MergeLinesBuilder rightLimit(int rightLimit){
        mergeLines.setRightLimit(rightLimit);
        return this;
    }

    public MergeLinesBuilder list(List<String> list){
        mergeLines.setList(list);
        return this;
    }

    public MergeLinesBuilder tempList(List<String> tempList){
        mergeLines.setTempList(tempList);
        return this;
    }

    public MergeLines getMergeLines(){
        return this.mergeLines;
    }

    MergeLinesBuilder(MergeLines mergeLines){
        this.mergeLines = mergeLines;
    }
}