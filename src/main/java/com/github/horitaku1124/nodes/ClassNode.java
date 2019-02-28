package com.github.horitaku1124.nodes;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClassNode extends NodeBase {
  private String packageName;
  private String name;

  private ClassInsideNode inside;
}
