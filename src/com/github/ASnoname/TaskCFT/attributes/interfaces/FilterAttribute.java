package com.github.ASnoname.TaskCFT.attributes.interfaces;

import java.util.stream.Stream;

public interface FilterAttribute {

    Stream<String> getStream(Stream<String> stream);
}
