import java.util.ArrayList;

public class Atom extends AST
{
  private String name;

  public Atom(String n)
  {
    name = n;
  }
  
  public String getName(){ return name; }

  //returns true or false if this Atom has the same name as another atom
  @Override
  public boolean equals(Object obj)
  {
    if(obj == null) return false;
    if(obj instanceof Atom){
      return name.equals(((Atom) obj).getName());
    }
    return false;
  }
  
  public AST copy()
  {
    return new Atom(name);
  }

  public AST apply(Binding b)
  {
    return new Atom(name);
  }

  public ArrayList<Binding> unifies(AST ast, ArrayList<Binding> bindings)
  {
    //RULE 1 : an atom unifies with a term iff that term is an atom and it has the same name
    //or more simply put an atom unifies with a term iff that term is equal to the atom
    
    ArrayList<Binding> new_bindings = new ArrayList<Binding>();
    for(int i = 0; i < bindings.size(); i++)
      new_bindings.add(bindings.get(i));

    //unify if the other ast is a variable
    if(ast instanceof Variable){
      if(new_bindings.get(new_bindings.size() - 1).bind((Variable) ast, this)){
        return new_bindings;
      }else{
        return new ArrayList<Binding>();
      }
    }
    
    if(equals(ast)){
      return new_bindings;
    }else{
      return new ArrayList<Binding>();
    }
  }

  public String toString(){
    return "[Atom " + name + "]";
  }

  public String formatString(){
    return name;
  }
}
