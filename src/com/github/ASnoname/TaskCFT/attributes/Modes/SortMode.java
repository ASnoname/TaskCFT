package com.github.ASnoname.TaskCFT.attributes.Modes;

import com.github.ASnoname.TaskCFT.attributes.interfaces.CmdAttribute;

public enum SortMode implements CmdAttribute {

    ASCENDING{
        @Override
        public String getCode(){
            return "-a";
        }
    },
    DECREASES{
        @Override
        public String getCode(){
            return "-d";
        }
    };

    public static final String name = "sortMode";
}