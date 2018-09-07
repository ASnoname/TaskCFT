package com.github.ASnoname.TaskCFT.attributes.Modes;

import com.github.ASnoname.TaskCFT.attributes.interfaces.CmdAttribute;
import com.github.ASnoname.TaskCFT.attributes.interfaces.FilterAttribute;

import java.util.stream.Stream;

public enum TypeMode implements CmdAttribute, FilterAttribute {

    STRING_TYPE{
        @Override
        public String getCode(){
            return "-s";
        }

        @Override
        public Stream<String> getStream(Stream<String> stream){
            return stream;
        }

    },
    INTEGER_TYPE{
        @Override
        public String getCode(){
            return "-i";
        }

        @Override
        public Stream<String> getStream(Stream<String> stream) {
            return stream.filter(this::isIntegerLine);
        }

        private boolean isIntegerLine(String line) {

            long before = line.chars().count();
            //48 is '0' ... 57 is '9'
            long after = line.chars().filter(c -> (c > 47 && c < 58)).count();

            return before == after;
        }
    };

    public static final String name = "typeMode";
}
