package com.github.ASnoname.TaskCFT.attributes.Modes;

import com.github.ASnoname.TaskCFT.attributes.interfaces.FilterAttribute;
import com.github.ASnoname.TaskCFT.attributes.interfaces.InnerAttribute;

import java.util.stream.Stream;

public enum SpaceMode implements FilterAttribute, InnerAttribute {

    WITH_SPACE{
        @Override
        public Stream<String> getStream(Stream<String> stream) {
            return stream;
        }
    },

    WITHOUT_SPACE{
        @Override
        public Stream<String> getStream(Stream<String> stream) {
            return stream.filter(this::isWithoutSpaceLine);
        }

        private boolean isWithoutSpaceLine(String line) {

            long before = line.chars().count();
            //' ' is 32
            long after = line.chars().filter(c -> c != 32 ).count();

            return before == after;
        }
    };

    public static final String name = "spaceMode";
}
