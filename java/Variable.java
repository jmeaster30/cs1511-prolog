import java.util.ArrayList;

public class Variable extends AST
{
  private String name; 
  private int scope_id;

  public Variable(String n)
  {
    name = n;
    scope_id = -1;
  }

  public String getName(){ return name; }

  public int getScope(){ return scope_id; }
  public void setScope(int s){ scope_id = s; }

  public boolean equals(Object obj)
  {
    if(obj == null) return false;
    if(obj instanceof Variable){
      boolean n = name.equals(((Variable) obj).getName());
      boolean s = scope_id == ((Variable) obj).getScope();
      //System.out.println("Name: " + n);
      //System.out.println("Scope: " + s);
      return n && s;
    }
    return false;
  }
  
  public AST copy()
  {
    AST result = new Variable(name);
    ((Variable)result).setScope(scope_id);
    return result;
  }
  
  public AST apply(Binding b)
  {
    //System.out.println(formatString());
    //System.out.println(b.toString());
    AST result = b.get(this);
    if(result == null) result = this;
    if(result instanceof Variable) result = this;
    return result;
  }

  public ArrayList<Binding> unifies(AST ast, ArrayList<Binding> bindings)
  {
    //RULE 2 : a variable and a term unifies by the variable being bound to that term 
    //add binding
    ArrayList<Binding> full_bindings = new ArrayList<Binding>();
    for(int i = 0; i < bindings.size(); i++)
      full_bindings.add(bindings.get(i));

    if(!full_bindings.get(full_bindings.size() - 1).bind(this, ast))
      return new ArrayList<Binding>();
    return full_bindings;
  }

  public String toString(){
    return "[Variable " + name + "]";
  }

  public String formatString(){
    return (scope_id == -1) ? name : name + "(" + scope_id + ")";
  }
}
