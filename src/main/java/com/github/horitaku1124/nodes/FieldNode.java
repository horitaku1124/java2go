package com.github.horitaku1124.nodes;

import com.github.horitaku1124.modifier.AccessModifier;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FieldNode extends NodeBase {
  private AccessModifier access;
  private String name;
  private String type;
  private boolean isStatic = false;
}
