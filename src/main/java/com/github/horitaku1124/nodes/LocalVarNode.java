package com.github.horitaku1124.nodes;

import lombok.Getter;

@Getter
public class LocalVarNode {
  private String type;
  private String name;
  public LocalVarNode() {}
  public LocalVarNode(String type, String name) {
    this.type = type;
    this.name = name;
  }

  public static LocalVarNode of(String localVarName) {
    var local = new LocalVarNode();
    local.name = localVarName;
    return local;
  }
}
