package com.github.horitaku1124.nodes;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ClassInsideNode extends NodeBase {
  private List<MethodNode> methods;
  private List<PropertyNode> members;
  public ClassInsideNode() {
    methods = new ArrayList<>();
    members = new ArrayList<>();
  }

  public void addMethod(MethodNode method) {
    methods.add(method);
  }
  public void addMember(PropertyNode member) {
    members.add(member);
  }
}
