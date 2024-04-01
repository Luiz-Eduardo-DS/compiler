import java.util.Stack;

public class Semantic {
  static Stack<String[]> symbolStack = new Stack<>();
  static Stack<String> identifierStack = new Stack<>();
  protected static Stack<String> typeControlStack = new Stack<>();

  // Função para verificar se uma variável está declarada
  public static String findInStack(String var) {
    // Se for nome do programa... ERRO
    if (var.equals(symbolStack.peek()[0])) {
      System.out.println("ERROR: TRYING TO USE PROGRAM NAME " + var);
      System.exit(1);
    }
    for (String[] values : symbolStack) {
      if (values[0].equals(var)) {
        System.out.println(var + " found in stack, can be used");
        return values[1];
      }
    }
    System.out.println("ERROR: TRYING TO USE UNDECLARED VAR NAMED " + var);
    System.exit(1);
    return null; // Nunca alcançado, apenas para satisfazer a exigência de retorno
  }

  // Limpa a pilha até encontrar Mark, removendo todas as variáveis do escopo local
  public static void cleanStack() {
    System.out.println("=====Before Clean Stack Scope=========");
    System.out.println(symbolStack);
    while (!symbolStack.empty() && !symbolStack.peek()[0].equals("mark")) {
      symbolStack.pop();
    }
    symbolStack.pop(); // Remove o marcador "mark"
    System.out.println("=====After Clean Stack Scope=========");
    System.out.println(symbolStack);
  }

  // Verifica se não está declarado no escopo
  public static void isInStack(String var) {
    // Tentando declarar uma variável com o nome do programa
    if (var.equals(symbolStack.peek()[0])) {
      System.out.println("ERROR: TRYING TO DECLARE " + var + " BUT IT'S THE PROGRAM NAME");
      System.exit(1);
    }
    for (String[] strings : symbolStack) {
      if (strings.equals("mark")) {
        System.out.println(var + " NOT FOUND IN SCOPE, CAN BE DECLARED");
        break;
      }
      if (strings.equals(var)) {
        System.out.println("ERROR: TRYING TO DECLARE " + var + " BUT IS ALREADY DECLARED IN SCOPE");
        System.exit(1);
      }
    }
  }

  // Adiciona a variável no escopo, se ainda não estiver declarada no escopo
  public static void stackAppend(String typeStack) {
    for (String vars : identifierStack) {
      isInStack(vars);
      symbolStack.push(new String[] {vars, typeStack});
    }
    identifierStack.clear(); // Limpa a pilha de identificadores
  }

  // Remove os valores do topo e subtopo e adiciona o resultado na pilha de controle de tipo
  public static void updateTypeStack(String resultType) {
    typeControlStack.pop();
    typeControlStack.pop();
    typeControlStack.push(resultType);
  }

  // Verificação de tipo para operações
  public static void typeStackChecker(boolean isRel, boolean isBoolOp) {
    // Se for uma operação booleana...
    if (isBoolOp) {
      // Se for uma operação booleana entre valores booleanos...
      if (typeControlStack.peek().equals("boolean")
          && typeControlStack.get(typeControlStack.size() - 2).equals("boolean")) {
        System.out.println("Boolean Operation between two booleans - OK");
        updateTypeStack("boolean");
        System.out.println(typeControlStack);
      } else {
        System.out.println(
            "ERROR: TRYING TO MAKE A BOOLEAN OPERATION BETWEEN "
                + typeControlStack.peek()
                + " AND "
                + typeControlStack.get(typeControlStack.size() - 2));
        System.exit(1);
      }
    } else {
      // Se isRel for true, é uma operação relacional, então o resultado é um booleano
      if (typeControlStack.peek().equals("integer")
          && typeControlStack.get(typeControlStack.size() - 2).equals("integer")) {
        System.out.println("Operation between integer and integer - OK");
        if (isRel) {
          updateTypeStack("boolean");
          System.out.println("Relational Operation - boolean output");
        } else {
          updateTypeStack("integer");
        }
      } else if ((typeControlStack.peek().equals("integer")
              && typeControlStack.get(typeControlStack.size() - 2).equals("real"))
          || (typeControlStack.peek().equals("real")
              && typeControlStack.get(typeControlStack.size() - 2).equals("integer"))) {
        System.out.println("Operation between integer and real - OK");
        if (isRel) {
          updateTypeStack("boolean");
          System.out.println("Relational Operation - boolean output");
        } else {
          updateTypeStack("real");
        }
      } else if (typeControlStack.peek().equals("real")
          && typeControlStack.get(typeControlStack.size() - 2).equals("real")) {
        System.out.println("Operation between real and real - OK");
        if (isRel) {
          updateTypeStack("boolean");
          System.out.println("Relational Operation - boolean output");
        } else {
          updateTypeStack("real");
        }
      } else {
        System.out.println(
            "ERROR: TRYING TO MAKE A OPERATION BETWEEN "
                + typeControlStack.peek()
                + " AND "
                + typeControlStack.get(typeControlStack.size() - 2));
        System.exit(1);
      }
    }
  }

  // Verificação de tipo de atribuição
  public static void assignStackChecker() {
    if (typeControlStack.peek().equals("integer")
        && typeControlStack.get(typeControlStack.size() - 2).equals("integer")) {
      System.out.println("Assignment between integer and integer - OK");
      typeControlStack.clear();
      System.out.println(typeControlStack);
    } else if (typeControlStack.peek().equals("integer")
        && typeControlStack.get(typeControlStack.size() - 2).equals("real")) {
      System.out.println("Assignment between integer and real - OK");
      typeControlStack.clear();
      System.out.println(typeControlStack);
    } else if (typeControlStack.peek().equals("real")
        && typeControlStack.get(typeControlStack.size() - 2).equals("real")) {
      System.out.println("Assignment between real and real - OK");
      typeControlStack.clear();
      System.out.println(typeControlStack);
    } else if (typeControlStack.peek().equals("boolean")
        && typeControlStack.get(typeControlStack.size() - 2).equals("boolean")) {
      System.out.println("Assignment between boolean and boolean - OK");
      typeControlStack.clear();
      System.out.println(typeControlStack);
    } else {
      System.out.println(
          "ERROR: TRYING TO ASSIGN A "
              + typeControlStack.peek()
              + " IN A "
              + typeControlStack.get(typeControlStack.size() - 2)
              + " VAR");
      System.exit(1);
    }
  }

  // Verifica se a expressão na declaração IF / WHILE é um booleano
  public static void verifyBooleanResult() {
    System.out.println(
        "Verifying if a Boolean value is resulted after IF / WHILE Expression Analysis...");
    if (typeControlStack.peek().equals("boolean")) {
      System.out.println("Boolean Value at the top of typeControlStack - OK");
      System.out.println(typeControlStack);
      typeControlStack.pop();
      System.out.println("Stack Top Cleaned...");
      System.out.println(typeControlStack);
    } else {
      System.out.println(typeControlStack);
      System.out.println("ERROR: AFTER IF / WHILE STATEMENT, TOP MUST BE AN BOOLEAN");
      System.exit(1);
    }
  }

  // Limpa a pilha até encontrar procedimento (usado para limpar a pilha após o comando de chamada
  // de procedimento)
  public static void clearTopTypeStack() {
    while (!typeControlStack.empty()) {
      if (typeControlStack.peek().equals("procedure")) {
        break;
      }
      typeControlStack.pop();
    }
    typeControlStack.pop();
    System.out.println("Cleaning top of TypeControlStack...");
    System.out.println(typeControlStack);
  }
}
