import java.util.ArrayList;

public class Args extends AST
{
  private AST left;
  private AST right;

  public Args(AST l, AST r)
  {
    left = l;
    right = r;
  }

  public AST getLeft(){ return left; }
  public AST getRight(){ return right; }
  public void setLeft(AST l){ left = l; }
  public void setRight(AST r){ right = r; }

  public ArrayList<Binding> unifies(AST ast, ArrayList<Binding> bindings){
    return new ArrayList<Binding>();
  }
  
  public AST copy(){
    return null;
  }

  public AST apply(Binding b){
    return null;
  }

  public String toString()
  {
    return "[Args]";
  }

  public String formatString(){
    return "[Args]";
  }
}
