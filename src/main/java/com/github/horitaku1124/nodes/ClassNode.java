package com.github.horitaku1124.nodes;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ClassNode extends NodeBase {
  private String packageName;
  private String name;
  private List<NodeBase> methods;
}
