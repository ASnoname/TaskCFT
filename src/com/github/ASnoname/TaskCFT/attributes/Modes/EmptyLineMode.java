package com.github.ASnoname.TaskCFT.attributes.Modes;

import com.github.ASnoname.TaskCFT.attributes.interfaces.FilterAttribute;
import com.github.ASnoname.TaskCFT.attributes.interfaces.InnerAttribute;

import java.util.stream.Stream;

public enum EmptyLineMode implements FilterAttribute, InnerAttribute {

    WITH_EMPTY_LINE{
        @Override
        public Stream<String> getStream(Stream<String> stream) {
            return stream;
        }
    },

    WITHOUT_EMPTY_LINE{
        @Override
        public Stream<String> getStream(Stream<String> stream) {
            return stream.filter(l -> l.length() > 0);
        }
    };

    public static final String name = "emptyLineMode";
}
