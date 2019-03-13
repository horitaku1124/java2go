package com.github.horitaku1124.golang;

import com.github.horitaku1124.nodes.ClassInsideNode;
import com.github.horitaku1124.nodes.ClassNode;
import com.github.horitaku1124.nodes.NodeBase;
import static java.lang.System.out;

public class GoWriter {
  public GoWriter() {

  }
  public void generate(NodeBase root) {
    out.println("package main\n" +
        "\n" +
        "import \"fmt\"\n" +
        "\n");

    ClassNode classNode = (ClassNode) root;
    ClassInsideNode inside = classNode.getInside();
    for (var method: inside.getMethods()) {
//      if (method.isStatic() && method.getType().equals("void")) {
//
//      }

      out.format("func %s() {\n", method.getName());
      for(var expression: method.getExpression()) {
        out.print("  ");
        for (var node: expression.getNodes()) {
          if (node.equals("System.out.println")) {
            out.print("fmt.Println");
          } else {
            out.print(node);
          }
        }
        out.println("");
      }


      out.format("}\n");
    }
  }
}
