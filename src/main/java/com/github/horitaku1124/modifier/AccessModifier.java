package com.github.horitaku1124.modifier;

public enum AccessModifier {
  PRIVATE, PUBLIC, PROTECTED;

  public static AccessModifier of(String s) {
    if ("private".equals(s)) {
      return PRIVATE;
    }
    if ("public".equals(s)) {
      return PUBLIC;
    }
    if ("protected".equals(s)) {
      return PROTECTED;
    }
    throw new RuntimeException();
  }
}
