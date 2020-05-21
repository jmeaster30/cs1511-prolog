import java.util.ArrayList;

public class Functor extends AST
{
  private String name;
  private ArrayList<AST> args;
  private int arity;

  public Functor(String f, AST a)
  {
    name = f;
    args = new ArrayList<AST>();
    if(a instanceof Args){
      //System.out.println((((Args) a).getLeft() == null) ? "flatten null" : ((Args) a).getLeft().formatString());
      args.add(((Args) a).getLeft());
      flatten(((Args) a).getRight());
    } else {
      args.add(a);
    }
    arity = args.size();
  }
  
  public void flatten(AST a)
  {
    if(a instanceof Args) {
      //System.out.println((((Args) a).getLeft() == null) ? "flatten null" : ((Args) a).getLeft().formatString());
      args.add(((Args) a).getLeft());
      flatten(((Args) a).getRight());
    } else {
      args.add(a);
    }
  }

  public String getName(){ return name; }
  public int getArity(){ return arity; }
  public ArrayList<AST> getArgs(){ return args; }

  public void setName(String s){ name = s; }
  public void setArgs(ArrayList<AST> a){ args = a; }
  public void setArity(int a){ arity = a; }
  
  public boolean equals(Object obj)
  {
    if(obj == null) return false;
    if(obj instanceof Functor)
    {
      if(!name.equals(((Functor) obj).getName()))
        return false;
      if(arity != ((Functor) obj).getArity())
        return false;
      return args.equals(((Functor) obj).getArgs());
    }
    return false;
  }
  
  public AST copy()
  {
    Functor result = new Functor(name, null);
    result.setArgs(new ArrayList(args));
    result.setArity(arity);
    return result;
  }
  
  public AST apply(Binding b)
  {
    Functor result = (Functor)copy();
    ArrayList<AST> aargs = new ArrayList<AST>();
    for(AST arg : result.getArgs()){
      aargs.add(arg.apply(b)); 
    }
    result.setArgs(aargs);
    return result;
  }

  public ArrayList<Binding> unifies(AST ast, ArrayList<Binding> bindings)
  {
    if(ast == null) return new ArrayList<Binding>();
    
    ArrayList<Binding> full_bindings = new ArrayList<Binding>(bindings);
    //if the other ast node is a Variable then we can unify
    if(ast instanceof Variable)
      if(full_bindings.get(full_bindings.size() - 1).bind(((Variable) ast), this))
        return full_bindings;
      else
        return new ArrayList<Binding>();
    
    //rule 3 : a functor unifies with a functor with the same name and the same arity
    if(ast instanceof Functor){
      if(!name.equals(((Functor) ast).getName())) return new ArrayList<Binding>();
      if(arity != ((Functor) ast).getArity()) return new ArrayList<Binding>();
    
      //functor is unified
    
      //unify the arguments
      for(int i = 0; i < arity; i++){
        AST arg_i = args.get(i);
        AST oarg_i = ((Functor) ast).getArgs().get(i);
      
        ArrayList<Binding> result = arg_i.unifies(oarg_i, full_bindings);
        if(result.size() == 0){
          full_bindings = new ArrayList<Binding>();
          break;
        }
        full_bindings = result;
      }
    } else {
      full_bindings = new ArrayList<Binding>();
    }
    return full_bindings;
  }

  public String toString(){
    String args_s = "";
    for(int i = 0; i < arity; i++){
      args_s += args.get(i).toString();
      if(i < arity - 1) args_s += " ";
    }
    return "[Functor " + name + "/" + arity + " : " + args_s + "]";
  }

  public String formatString(){
    StringBuilder sb = new StringBuilder();
    
    sb.append(name);
    sb.append("(");
    for(int i = 0; i < arity; i++){
      AST arg = args.get(i);
      //System.out.println((arg == null) ? "null" : "not null");
      sb.append((arg == null) ? "[null]" : arg.formatString());
      if(i < args.size() - 1)
        sb.append(", ");
    }
    sb.append(")");
    return sb.toString();
  }
}
