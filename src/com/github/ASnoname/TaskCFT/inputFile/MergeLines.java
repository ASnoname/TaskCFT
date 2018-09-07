package com.github.ASnoname.TaskCFT.inputFile;

import com.github.ASnoname.TaskCFT.attributes.Modes.SortMode;
import com.github.ASnoname.TaskCFT.attributes.Modes.TypeMode;
import com.github.ASnoname.TaskCFT.configure.Configure;
import com.github.ASnoname.TaskCFT.configure.comparators.IntegerComparator;
import com.github.ASnoname.TaskCFT.configure.comparators.StringComparator;

import java.util.Comparator;
import java.util.List;

public class MergeLines {

    private int leftLimit;
    private Integer left;
    private int rightLimit;
    private Integer right;
    private List<String> list;
    private List<String> tempList;
    private boolean isUp;
    private Comparator<String> comparator;

    public MergeLines(Configure configure) {
        this.comparator = initComparator(configure);
        this.isUp = initIsUp(configure);
    }

    private boolean initIsUp(Configure configure) {

        return configure
                        .getAttributes()
                        .get( SortMode.name)
                        .getValue()
                        .equals(SortMode.ASCENDING.name());
    }

    private Comparator<String> initComparator(Configure configure) {

        if (configure.getAttributes().get( TypeMode.name).getValue().equals( TypeMode.INTEGER_TYPE.name())){
            return new IntegerComparator();
        }
        else {
            return new StringComparator();
        }
    }

    public MergeLinesBuilder builder(){
        return new MergeLinesBuilder(this);
    }

    public boolean isUp() {
        return isUp;
    }

    public Comparator<String> getComparator() {
        return comparator;
    }

    public int getLeftLimit() {
        return leftLimit;
    }

    public void setLeftLimit(int leftLimit) {
        this.leftLimit = leftLimit;
    }

    public Integer getLeft() {
        return left;
    }

    public void setLeft(Integer left) {
        this.left = left;
    }

    public int getRightLimit() {
        return rightLimit;
    }

    public void setRightLimit(int rightLimit) {
        this.rightLimit = rightLimit;
    }

    public Integer getRight() {
        return right;
    }

    public void setRight(Integer right) {
        this.right = right;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public List<String> getTempList() {
        return tempList;
    }

    public void setTempList(List<String> tempList) {
        this.tempList = tempList;
    }
}