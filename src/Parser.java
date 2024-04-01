import java.util.List;

public class Parser {
  private static int currentPosition;
  private static List<Tokens> tokenList;

  public Parser(List<Tokens> tokens) {
    tokenList = tokens;
  }

  public void parse() {
    synAnalysis();
  }

  private static void synAnalysis() {
    Semantic.symbolStack.push(new String[] {"mark", null});

    if (match("program")) {
      nextToken();
      if (isIdentifier()) {
        nextToken();
        if (match(";")) {
          nextToken();
          varDeclaration();
          subProgramDeclarations();
          compoundCommand();
          Semantic.cleanStack();
          if (!match(".")) {
            System.out.println(
                "ERROR: EXPECTED '.' BUT "
                    + getCurrentTokenValue()
                    + " FOUND IN LINE "
                    + getCurrentTokenLine());
          } else {
            System.out.println("PASCAL PROGRAM ACCEPTED");
          }
        } else {
          System.out.println(
              "ERROR: EXPECTED ';' BUT "
                  + getCurrentTokenValue()
                  + " FOUND IN LINE "
                  + getCurrentTokenLine());
        }
      } else {
        System.out.println(
            "ERROR: EXPECTED IDENTIFIER BUT "
                + getCurrentTokenValue()
                + " FOUND IN LINE "
                + getCurrentTokenLine());
      }
    } else {
      System.out.println(
          "ERROR: EXPECTED 'program' BUT "
              + getCurrentTokenValue()
              + " FOUND IN LINE "
              + getCurrentTokenLine());
    }
  }

  private static void varDeclaration() {
    if (match("var")) {
      nextToken();
      if (isIdentifier()) {
        while (!match("begin") && !match("procedure")) {
          listIdentifier();
        }
      } else {
        System.out.println(
            "ERROR: IDENTIFIER EXPECTED BUT '"
                + getCurrentTokenValue()
                + "' FOUND IN LINE "
                + getCurrentTokenLine());
      }
    } else {
      System.out.println("'var' not found, but " + getCurrentTokenValue());
    }
  }

  private static void subProgramDeclarations() {
    while (!match("begin")) {
      subProgramDeclaration();
    }
  }

  private static void subProgramDeclaration() {
    if (match("procedure")) {
      nextToken();
      Semantic.symbolStack.push(new String[] {getCurrentTokenValue(), "procedure"});
      Semantic.symbolStack.push(new String[] {"mark", null});
      if (isIdentifier()) {
        nextToken();
        if (match("(")) {
          nextToken();
          while (!match(")")) {
            listIdentifier(1);
          }
          if (match(")")) {
            nextToken();
            if (match(";")) {
              nextToken();
              varDeclaration();
              subProgramDeclarations();
              compoundCommand();
              Semantic.cleanStack();
            } else {
              if (match("end")) {
                System.out.println("Dont found ; but found end...");
              } else {
                System.out.println(
                    "ERROR: EXPECTED ';' BUT "
                        + getCurrentTokenValue()
                        + " FOUND IN LINE "
                        + getCurrentTokenLine());
              }
            }
          } else {
            System.out.println(
                "ERROR: EXPECTED ')' BUT "
                    + getCurrentTokenValue()
                    + " FOUND IN LINE "
                    + getCurrentTokenLine());
          }
        } else if (match(";")) {
          System.out.println("No parenthesis found, but ';' in line " + getCurrentTokenLine());
          nextToken();
          varDeclaration();
          subProgramDeclaration();
          compoundCommand();
          Semantic.cleanStack();
        } else {
          System.out.println(
              "ERROR: EXPECTED '(' OR ';' BUT "
                  + getCurrentTokenValue()
                  + " FOUND IN LINE "
                  + getCurrentTokenLine());
        }
      } else {
        System.out.println(
            "ERROR: EXPECTED IDENTIFIER BUT "
                + getCurrentTokenValue()
                + " IN LINE "
                + getCurrentTokenLine());
      }
    } else {
      System.out.println("procedure dont found but " + getCurrentTokenValue());
      nextToken();
    }
  }

  private static boolean isIdentifier() {
    return getCurrentTokenType().equals("IDENTIFIER");
  }

  private static void listIdentifier() {
    listIdentifier(0);
  }

  private static void listIdentifier(int isArgs) {
    if (isArgs == 1 && match(")")) {
      return;
    }
    if (isIdentifier()) {
      Semantic.identifierStack.push(getCurrentTokenValue());
      nextToken();
      if (match(",")) {
        nextToken();
        if (isArgs == 1) {
          listIdentifier(1);
        } else {
          listIdentifier();
        }
      } else if (match(":")) {
        nextToken();
        if (match("integer") || match("real") || match("boolean")) {
          String typeStack = getCurrentTokenValue();
          nextToken();
          if (match(";")) {
            nextToken();
            Semantic.stackAppend(typeStack);
          } else {
            if (isArgs == 1 && match(")")) {
              Semantic.stackAppend(typeStack);
              return;
            } else if (isArgs == 1) {
              System.out.println(
                  "ERROR: EXPECTED ')' OR ';' BUT "
                      + getCurrentTokenValue()
                      + " FOUND IN LINE "
                      + getCurrentTokenLine());
            } else {
              System.out.println(
                  "ERROR: EXPECTED ';' BUT FOUND "
                      + getCurrentTokenValue()
                      + " IN LINE "
                      + getCurrentTokenLine());
            }
          }
        } else {
          System.out.println(
              "ERROR: DATA TYPE EXPECTED BUT FOUND "
                  + getCurrentTokenValue()
                  + " IN LINE "
                  + getCurrentTokenLine());
        }
      } else {
        System.out.println(
            "ERROR: EXPECTED ':' BUT FOUND "
                + getCurrentTokenValue()
                + " IN LINE "
                + getCurrentTokenLine());
      }
    } else {
      System.out.println(
          "ERROR: IDENTIFIER EXPECTED BUT FOUND "
              + getCurrentTokenValue()
              + " IN LINE "
              + getCurrentTokenLine());
      nextToken();
    }
  }

  private static void compoundCommand() {
    if (match("begin")) {
      nextToken();
      while (!match("end")) {
        commandList();
      }
      if (match("end")) {
        nextToken();
        if (match(";")) {
          nextToken();
        } else {
          System.out.println(
              "ERROR: EXPECTED ';' BUT "
                  + getCurrentTokenValue()
                  + " FOUND IN LINE "
                  + getCurrentTokenLine());
        }
      } else {
        System.out.println(
            "ERROR: EXPECTED 'end' BUT "
                + getCurrentTokenValue()
                + " IN LINE "
                + getCurrentTokenLine());
      }
    } else {
      System.out.println(
          "ERROR: EXPECTED 'begin' BUT "
              + getCurrentTokenValue()
              + " IN LINE "
              + getCurrentTokenLine());
    }
  }

  private static void commandList() {
    commands();
    if (match(";")) {
      nextToken();
      commandList();
    }
  }

  private static void commands() {
    if (isIdentifier()) {
      Semantic.typeControlStack.push(Semantic.findInStack(getCurrentTokenValue()));
      nextToken();
      if (match(":=")) {
        nextToken();
        expressionAnalyzer();
        Semantic.assignStackChecker();
      } else if (match("(")) {
        nextToken();
        expressionList();
        if (match(")")) {
          nextToken();
          if (match(";")) {
            nextToken();
            Semantic.clearTopTypeStack();
          } else {
            System.out.println(
                "ERROR: EXPECTED ';' BUT "
                    + getCurrentTokenValue()
                    + " FOUND IN LINE "
                    + getCurrentTokenLine());
          }
        } else {
          System.out.println(
              "ERROR: EXPECTED ')' BUT "
                  + getCurrentTokenValue()
                  + " FOUND IN LINE "
                  + getCurrentTokenLine());
        }
      } else if (match(";")) {
        nextToken();
        Semantic.clearTopTypeStack();
      } else {
        System.out.println(
            "ERROR: EXPECTED ':=' OR ';' OR '();' BUT "
                + getCurrentTokenValue()
                + " FOUND IN LINE "
                + getCurrentTokenLine());
      }
    } else {
      if (getCurrentTokenType().equals("RESERVED_WORD")) {
        if (match("begin")) {
          nextToken();
          compoundCommand();
        } else if (match("if")) {
          nextToken();
          expressionAnalyzer();
          if (match("then")) {
            Semantic.verifyBooleanResult();
            nextToken();
            commands();
            if (match("else")) {
              nextToken();
              commandList();
            }
          } else {
            System.out.println(
                "ERROR: EXPECTED 'then' BUT "
                    + getCurrentTokenValue()
                    + " FOUND IN LINE "
                    + getCurrentTokenLine());
          }
        } else if (match("while")) {
          nextToken();
          expressionAnalyzer();
          if (match("do")) {
            Semantic.verifyBooleanResult();
            nextToken();
            commandList();
          } else {
            System.out.println(
                "ERROR: EXPECTED 'do' BUT "
                    + getCurrentTokenValue()
                    + " FOUND IN LINE "
                    + getCurrentTokenLine());
          }
        }
      } else {
        System.out.println(
            "ERROR: NO COMMAND FOUND: "
                + getCurrentTokenValue()
                + " IN LINE "
                + getCurrentTokenLine());
      }
    }
  }

  private static void expressionAnalyzer() {
    expressionSimple();
    if (match("RELATIONAL_OPERATOR")) {
      nextToken();
      expressionSimple();
      Semantic.typeStackChecker(true, true);
    }
  }

  private static void expressionList() {
    expressionAnalyzer();
    if (match(",")) {
      nextToken();
      expressionList();
    }
  }

  private static void expressionSimple() {
    if (match("+") || match("-")) {
      nextToken();
    }
    isTerm();
    if (match("ADDITIVE_OPERATOR")) {
      nextToken();
      expressionSimple();
      Semantic.typeStackChecker(true, true);
    }
  }

  private static boolean isTerm() {
    isFactor();
    if (match("MULTIPLICATION_OPERATOR")) {
      nextToken();
      isTerm();
      Semantic.typeStackChecker(true, true);
    }
    return true;
  }

  private static void isFactor() {
    if (isIdentifier()) {
      Semantic.typeControlStack.push(Semantic.findInStack(getCurrentTokenValue()));
      nextToken();
      if (match("(")) {
        nextToken();
        expressionList();
        if (match(")")) {
          nextToken();
          Semantic.typeControlStack.pop();
        } else {
          System.out.println(
              "ERROR: EXPECTED ')' BUT "
                  + getCurrentTokenValue()
                  + " FOUND IN LINE "
                  + getCurrentTokenLine());
        }
      }
    } else if (match("INTEGER") || match("REAL") || match("BOOLEAN")) {
      Semantic.typeControlStack.push(getCurrentTokenValue().toLowerCase());
      nextToken();
    } else if (match("not")) {
      nextToken();
      isFactor();
    } else if (match("(")) {
      nextToken();
      expressionAnalyzer();
      if (match(")")) {
        nextToken();
      } else {
        System.out.println(
            "ERROR: EXPECTED ')' BUT "
                + getCurrentTokenValue()
                + " FOUND IN LINE "
                + getCurrentTokenLine());
      }
    }
  }

  private static boolean match(String expected) {
    return getCurrentTokenValue().equals(expected);
  }

  private static void nextToken() {
    currentPosition++;
  }

  static String getCurrentTokenValue() {
    return tokenList.get(currentPosition).getToken();
  }

  static String getCurrentTokenType() {
    return tokenList.get(currentPosition).getClassification();
  }

  static Integer getCurrentTokenLine() {
    return tokenList.get(currentPosition).getLine();
  }
}
