import java.util.ArrayList;

public abstract class AST
{
  public AST(){}

  public abstract ArrayList<Binding> unifies(AST ast, ArrayList<Binding> bindings);
  public abstract AST copy();
  public abstract AST apply(Binding b);
  public abstract String toString();
  public abstract String formatString();
}
