package com.github.horitaku1124;

import com.github.horitaku1124.modifier.AccessModifier;
import com.github.horitaku1124.nodes.ClassInsideNode;
import com.github.horitaku1124.nodes.ClassNode;
import com.github.horitaku1124.nodes.NodeBase;
import com.github.horitaku1124.nodes.PropertyNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Compile {
  private final String spaces = " \t";
  private final String cr_lf = "\r\n";
  private final String singleOperator = "()+-.*/=;,><?&!|[]@{}";
  private final String allClears = singleOperator + spaces + cr_lf;


  public static void main(String[] args) throws IOException {
    List<String> lines = Files.readAllLines(Path.of(args[0]));
    Compile jc = new Compile();
    List<String> tokens = jc.lexicalAnalyzer(lines);
    for (String s: tokens) {
      System.out.println(s);
    }
    NodeBase root = jc.toAst(tokens);
  }

  private List<String> lexicalAnalyzer(List<String> lines) {
    Character quote = null;
    String verse = "";
    List<String> tokens = new ArrayList<>();
    boolean inLineComment = false;
    boolean inMultiLineComment = false;
    for (String line: lines) {
      for (int i = 0;i < line.length();i++) {
        char c = line.charAt(i);

        if(inLineComment) {
          if(c == '\n') {
            inLineComment = false;
            tokens.add(verse);
            tokens.add("\n");
            verse = "";
          } else {
            verse += c;
          }
          continue;
        } else if(inMultiLineComment) {
          verse += c;
          if(c == '*' && line.charAt(i + 1) == '/') {
            verse += "/";
            inMultiLineComment = false;
            if(verse.length() > 0) {
              tokens.add(verse);
            }
            verse = "";
            i++;
          }
          continue;
        } else if(quote != null) {
          if(c == quote) {
            verse += quote;
            if(verse.length() > 0) {
              tokens.add(verse);
            }
            quote = null;
            verse = "";
          } else if(c == '\\') {
            char escapeTarget = line.charAt(i + 1);
            verse += c;
            if(escapeTarget == quote) {
              verse += quote;
              i++;
            } else if("nt\\".indexOf(escapeTarget) >= 0) {
              verse += escapeTarget;
              i++;
            }
          } else {
            verse += c;
          }
          continue;
        }
        if(c == '\'' || c == '"' || c == '`') {
          if(verse.length() > 0) {
            tokens.add(verse);
          }
          verse = String.valueOf(c);
          quote = c;
        } else if(c == '/') {
          char nextChar = line.charAt(++i);
          if(nextChar == '/'){
            inLineComment = true;
          } else if(nextChar == '*'){
            inMultiLineComment = true;
          } else {
            // Divide operator
            if(verse.length() > 0) {
              tokens.add(verse);
            }
            tokens.add(String.valueOf(c));
            verse = "";
            i--;
          }

          if(verse.length() > 0) {
            tokens.add(verse);
          }
          if(inLineComment || inMultiLineComment) {
            verse = String.valueOf(c) + nextChar;
          }
        } else if(allClears.contains(String.valueOf(c))) {
          if(verse.length() > 0) {
            tokens.add(verse);
          }
          if(cr_lf.indexOf(c) >= 0 || singleOperator.indexOf(c) >= 0) {
            // tokens.push(new CodeVal(char));
          }
          tokens.add(String.valueOf(c));
          verse = "";
        } else {
          verse += c;
        }
      }
    }

    if(verse.length() > 0) {
      tokens.add(verse);
    }
    return tokens;

  }

  enum Type {
    OUTER_CLASS,
    INNER_CLASS
  }

  private NodeBase toAst(List<String> tokens) {
    ClassNode classNode = null;
    ClassInsideNode classInside = null;

    Type pos = Type.OUTER_CLASS;

    for (int i = 0;i < tokens.size();i++) {
      String token = tokens.get(i);
      if (pos == Type.OUTER_CLASS) {
        if ("class".equals(token)) {
          if (classNode == null) {
            classNode = new ClassNode();
            classNode.setName(tokens.get(i + 2));
          }
          i += 2;

          for (int skip = 1;skip <= 2;skip++) {
            if (tokens.get(i + skip).equals("{")) {
              classInside = new ClassInsideNode();
              pos = Type.INNER_CLASS;
              i += skip;
              break;
            }
          }
        }
        continue;
      }
      if (pos == Type.INNER_CLASS) {
        int skip = 1;
        AccessModifier modifier = AccessModifier.PUBLIC;
        switch (token) {
          case "private":
          case "public":
          case "protected":
            modifier = AccessModifier.of(token);
            skip += 2;
            break;
        }
        String typeName = tokens.get(i + skip);
        skip += 2;
        String memberName = tokens.get(i + skip);
        skip += 2;
        String next = tokens.get(i + skip);
        if (";".equals(next)) {
          PropertyNode property = new PropertyNode();
          property.setName(memberName);
          property.setType(typeName);
          property.setAccess(modifier);
          classInside.addMember(property);
          i += skip + 1;
          continue;
        }
      }

    }

    return classNode;
  }
}
