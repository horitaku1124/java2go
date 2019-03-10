package com.github.horitaku1124;

import com.github.horitaku1124.modifier.AccessModifier;
import com.github.horitaku1124.nodes.*;
import com.github.horitaku1124.tokenizer.TokenIterator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    System.out.println(root);
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
    INNER_CLASS,
    METHOD_ARGUMENTS,
    INNER_METHOD,
  }

  private NodeBase toAst(List<String> tokens) {
    ClassNode classNode = null;
    ClassInsideNode classInside = null;
    Type pos = Type.OUTER_CLASS;
    MethodNode methodNode = null;
    TokenIterator tokenIterator = new TokenIterator(tokens);

    while(true){
      if (tokenIterator.noNext()) {
        break;
      }
      if (pos == Type.OUTER_CLASS) {
        String token = tokenIterator.nextNonSpace().get();
        if ("class".equals(token)) {
          if (classNode == null) {
            classNode = new ClassNode();
            classNode.setName(tokenIterator.nextNonSpace().get());
          }

          for (int skip = 1;skip <= 2;skip++) {
            if (tokenIterator.nextNonSpace().get().equals("{")) {
              classInside = new ClassInsideNode();
              pos = Type.INNER_CLASS;
              break;
            }
          }
        }
        continue;
      }
      if (pos == Type.INNER_CLASS) {
        String token = tokenIterator.nextNonSpace().get();

        if (token.equals("}")) {
          pos = Type.OUTER_CLASS;
          classNode.setInside(classInside);
          classInside = null;
          continue;
        }
        var modifier = AccessModifier.PUBLIC;
        switch (token) {
          case "private":
          case "public":
          case "protected":
            modifier = AccessModifier.of(token);
            break;
        }
        String typeName;
        var isStatic = false;
        var nextToken = tokenIterator.nextNonSpace().get();
        if ("static".equals(nextToken)) {
          isStatic = true;
          typeName = tokenIterator.nextNonSpace().get();
        } else {
          typeName = nextToken;
        }

        var memberName = tokenIterator.nextNonSpace().get();
        nextToken = tokenIterator.nextNonSpace().get();
        if (";".equals(nextToken)) {
          var property = new PropertyNode();
          property.setName(memberName);
          property.setType(typeName);
          property.setAccess(modifier);
          property.setStatic(isStatic);
          classInside.addMember(property);
          continue;
        }

        if ("(".equals(nextToken)) {
          pos = Type.METHOD_ARGUMENTS;
          while (true) {
            token = tokenIterator.nextNonSpace().get();
            if (")".equals(token)) {
              break;
            }
          }
          token = tokenIterator.nextNonSpace().get();
          if (";".equals(token)) {
            // TODO
          } else if ("{".equals(token)) {
            pos = Type.INNER_METHOD;
            methodNode = new MethodNode();
            methodNode.setName(memberName);
            methodNode.setType(typeName);
            methodNode.setAccess(modifier);
            methodNode.setStatic(isStatic);
          }
          continue;
        }
      }
      if (pos == Type.INNER_METHOD) {
        while(true) {
          List<String> lineTokens = new ArrayList<>();
          var token = tokenIterator.nextNonSpace();
          if (token.isEmpty() || token.get().equals("}")) {
            pos = Type.INNER_CLASS;
            break;
          }
          while(true) {
            var str = token.get();
            if (";".equals(str)) {
              break;
            }
            lineTokens.add(str);
            token = tokenIterator.nextNonSpace();
          }
          LocalVarNode localVar = null;
          int next;
          if (lineTokens.get(1).equals("=")) {
            var localVarName = lineTokens.get(0);
            localVar = LocalVarNode.of(localVarName);
            next = 2;
          } else if (lineTokens.get(2).equals("=")) {
            var localVarType = lineTokens.get(0);
            var localVarName = lineTokens.get(1);
            localVar = new LocalVarNode(localVarType, localVarName);
            next = 3;
          } else {
            next = 0;
          }

          var expressionNode = new ExpressionNode();
          if (localVar != null) {
            expressionNode.setAssignTo(localVar);
          }
          for (int i = next;i < lineTokens.size();i++) {
            expressionNode.getNodes().add(lineTokens.get(i));
          }
          methodNode.getExpression().add(expressionNode);
        }
        classInside.addMethod(methodNode);
        methodNode = null;
      }
    }

    return classNode;
  }
}
