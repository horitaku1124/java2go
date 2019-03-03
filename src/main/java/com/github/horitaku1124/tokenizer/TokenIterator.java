package com.github.horitaku1124.tokenizer;

import java.util.List;
import java.util.Optional;

public class TokenIterator {
  private List<String> tokens;
  private int index;
  public TokenIterator(List<String> tokens) {
    this.tokens = tokens;
    index = 0;
  }

  public boolean noNext() {
    int index2 = index;
    while(index2 < tokens.size()) {
      String next = tokens.get(index2);
      index2++;
      if (next.isBlank()) {
        continue;
      }
      return true;
    }
    return false;
  }
  public Optional<String> nextNonSpace() {
    while(index < tokens.size()) {
      String next = tokens.get(index);
      index++;
      if (next.isBlank()) {
        continue;
      }
      return Optional.of(next);
    }
    return Optional.empty();
  }
}
