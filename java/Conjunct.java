import java.util.ArrayList;

public class Conjunct extends AST
{
  private AST left;
  private AST right;

  public Conjunct(AST l, AST r)
  {
    left = l;
    right = r;
  }

  public AST getLeft(){ return left; }
  public AST getRight(){ return right; }
  public void setLeft(AST l){ left = l; }
  public void setRight(AST r){ right = r; }

  public ArrayList<Binding> unifies(AST ast, ArrayList<Binding> bindings)
  {
    //System.out.println("CONJUNCT");
    //unify lhs
    //use bindings as constraints for the right hand side
    ArrayList<Binding> full_bindings = new ArrayList<Binding>();
    ArrayList<Binding> left_bindings = null;
    if(left instanceof Conjunct || left instanceof Disjunct){
      left_bindings = left.unifies(ast, bindings);
    }else{
      left_bindings = ast.unifies(left, bindings);
    }
    //System.out.println(left_bindings.size() + " " + left.formatString() + " " + ast.formatString());
    
    for(Binding l : left_bindings)
    {
      AST newQuery = right.apply(l);
      //System.out.println(newQuery.formatString());
      ArrayList<Binding> right_results = null;
      if(newQuery instanceof Conjunct || newQuery instanceof Disjunct){
        right_results = Database.getInstance().query(newQuery, true);
      } else {
        right_results = Database.getInstance().query(newQuery);
      }

      for(Binding r : right_results)
      {
        Binding combo = l.combine(r);
        //System.out.println(combo.toString());
        if(combo != null)
          full_bindings.add(combo); 
      }
    }
    return full_bindings;
  }
  
  public AST copy()
  {
    return new Conjunct(left, right);
  }
  
  public AST apply(Binding b)
  {
    return new Conjunct(left.apply(b), right.apply(b));
  }

  public String toString(){
    String l = left.toString();
    String r = right.toString();
    return "[Conjunct " + l + " " + r + "]";
  }

  public String formatString(){
    String l = left.formatString();
    String r = right.formatString();
    return l + ", " + r;
  }
}
