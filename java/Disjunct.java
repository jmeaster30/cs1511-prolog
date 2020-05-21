import java.util.ArrayList;

public class Disjunct extends AST
{
  private AST left;
  private AST right;

  public Disjunct(AST l, AST r)
  {
    left = l;
    right = r;
  }

  public AST getLeft(){ return left; }
  public AST getRight(){ return right; }
  public void setLeft(AST l){ left = l; }
  public void setRight(AST r){right = r; }

  public ArrayList<Binding> unifies(AST ast, ArrayList<Binding> bindings)
  {
    ArrayList<Binding> full_bindings = new ArrayList<Binding>();
    ArrayList<Binding> left_bindings = null;
    if(left instanceof Conjunct || left instanceof Disjunct){
      left_bindings = left.unifies(ast, bindings);
    } else {
      left_bindings = ast.unifies(left, bindings);
    } 
    
    for(Binding l : left_bindings)
      full_bindings.add(l);
    
    //System.out.println(left_bindings.size());

    ArrayList<Binding> right_bindings = null;
    if(right instanceof Conjunct || right instanceof Disjunct){
      right_bindings = right.unifies(ast, bindings);
    } else {
      right_bindings = ast.unifies(right, bindings);
    }
    
    //System.out.println(right_bindings.size());

    for(Binding r : right_bindings)
    {
      boolean result = true;
      //System.out.println("> " + r.toString());
      for(Binding f : full_bindings){
        if(f.equals(r)){
          //System.out.println(f.toString());
          //System.out.println("==");
          //System.out.println(r.toString());
          result = false;
          break;
        } else {
          //System.out.println(f.toString());
          //System.out.println("!=");
          //System.out.println(r.toString());
        }
      }
      if(result){
        full_bindings.add(r);
        //System.out.println("Add.");
      }else{
        //System.out.println("Don't add.");
      }
    }

    return full_bindings;
  }
  
  public AST copy()
  {
    return new Disjunct(left, right);
  }
  
  public AST apply(Binding b)
  {
    return new Disjunct(left.apply(b), right.apply(b));
  }

  public String toString(){
    String l = left.toString();
    String r = right.toString();
    return "[Disjunct " + l + " " + r + "]";
  }

  public String formatString(){
    String l = left.formatString();
    String r = right.formatString();
    return l + "; " + r;
  }
}
