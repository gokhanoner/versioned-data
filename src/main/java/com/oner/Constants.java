package com.oner;


public enum Constants {

    MAX_ENTRY(10_000),
    VERSION_PER_ENTRY(3);

    private final int value;

    Constants(int v) { value = v; }

    public int value() { return value; }

}
