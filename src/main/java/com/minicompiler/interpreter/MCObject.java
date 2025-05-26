package com.minicompiler.interpreter;

public interface MCObject {
    ObjectType type();
    String inspect();
}
