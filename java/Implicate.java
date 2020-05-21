import java.util.ArrayList;

public class Implicate extends AST
{
  private AST left;
  private AST right;

  public Implicate(AST l, AST r)
  {
    left = l;
    right = r;
  }

  public AST getLeft(){ return left; }
  public AST getRight(){ return right; }
  public void setLeft(AST l){ left = l; }
  public void setRight(AST r){ right = r; }

  public ArrayList<Binding> unifies(AST ast, ArrayList<Binding> bindings){
    //unify the lhs with ast
    ArrayList<Binding> full_bindings = new ArrayList<Binding>();
    ArrayList<Binding> left_bindings = left.unifies(ast, bindings);     
    //go through rhs and try to generate the new bindings
    for(Binding b : left_bindings){
      //pass in the binding
      //System.out.println(b.toString());
      //System.out.println(right.formatString());
      AST newQuery = right.apply(b);
      //System.out.println(newQuery.toString());

      ArrayList<Binding> resultBindings = null;
      if(newQuery instanceof Conjunct || newQuery instanceof Disjunct){
        resultBindings = Database.getInstance().query(newQuery, true);
      } else {
        resultBindings = Database.getInstance().query(newQuery);
      }
      
      for(Binding r : resultBindings)
      {
        //System.out.println("Mergings Bindings:");
        //System.out.println(b.toString());
        //System.out.println("-and-");
        //System.out.println(r.toString());
        //System.out.println(">>");
        //System.out.println(b.merge(r).toString() + "\n");
        full_bindings.add(b.merge(r));
      }
    }
    return full_bindings;
  }
  
  public AST copy()
  {
    //I think this doesn't copy left and right
    return new Implicate(left, right);
  }
  
  public AST apply(Binding b)
  {
    return new Implicate(left.apply(b), right.apply(b));
  }

  public String toString(){
    String l = left.toString();
    String r = right.toString();
    return "[Implicate " + l + " " + r + "]";
  }

  public String formatString(){
    String l = left.formatString();
    String r = right.formatString();
    return l + " :- " + r;
  }
}
