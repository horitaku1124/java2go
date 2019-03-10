package com.github.horitaku1124.nodes;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExpressionNode extends NodeBase {
  private LocalVarNode assignTo;
  private List<String> nodes = new ArrayList<>();
}
