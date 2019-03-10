package com.github.horitaku1124.nodes;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MethodNode extends FieldNode {
  private List<ExpressionNode> expression = new ArrayList<>();
}
