package com.github.horitaku1124.nodes;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ClassInsideNode extends NodeBase {
  private List<NodeBase> methods;
  private List<PropertyNode> members;

  public void addMethod(NodeBase method) {
    methods.add(method);
  }
  public void addMember(PropertyNode member) {
    members.add(member);
  }
}
