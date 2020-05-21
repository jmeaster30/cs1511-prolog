import java.util.HashMap;
import java.util.Map;

public class Binding
{
  private Map<String, AST> bindings;

  public Binding()
  {
    bindings = new HashMap<>();
  }

  public Binding(Binding b)
  {
    bindings = new HashMap<>();
    for(Map.Entry<String, AST> entry : b.bindings.entrySet())
    {
      bindings.put((String)entry.getKey(), entry.getValue().copy());
    }
  }
  
  public boolean isStatic(AST a)
  {
    if(a instanceof Variable) return false;
    if(a instanceof Atom) return true;
    if(a instanceof Functor){
      for(AST arg : ((Functor)a).getArgs())
        if(!isStatic(arg))
          return false;
      return true;
    }
    if(a instanceof Conjunct) return isStatic(((Conjunct)a).getLeft()) && isStatic(((Conjunct)a).getRight());
    if(a instanceof Disjunct) return isStatic(((Disjunct)a).getLeft()) && isStatic(((Disjunct)a).getRight());
    if(a instanceof Implicate) return isStatic(((Implicate)a).getLeft());
    //the right hand side of the implication doesn't affect if the implication is static since if the left hand side is static
    //we can resolve the query
    return false;
  }
  
  public AST get(String key)
  {
    if(!bindings.containsKey(key)){
      return null;
    }
    return bindings.get(key);
  }
  
  public AST get(Variable key)
  {
    if(!bindings.containsKey(key.formatString())){
      //System.out.println("Key no contained.");
      return null;
    }
    //System.out.println("Key found.");
    return bindings.get(key.formatString());
  }

  public boolean bind(Variable v, AST a)
  {
    if(bindings.containsKey(v.formatString())){
      if(!bindings.get(v.formatString()).equals(a)){
        return false;
      }
    }else{
      bindings.put(v.formatString(), a);
    }
    return true; // make this so if we get a contradictory binding then the binds fail and the variable can't be unified
  }
  
  public boolean bind(String v, AST a)
  {
    if(bindings.containsKey(v)){
      if(!bindings.get(v).equals(a)){
        return false;
      }
    }else{
      bindings.put(v, a);
    }
    return true; // make this so if we get a contradictory binding then the binds fail and the variable can't be unified
  }

  public Binding getStaticBindings()
  {
    Binding s = new Binding();
    for(Map.Entry<String, AST> entry : bindings.entrySet()){
      if(isStatic(entry.getValue())){
        s.bind(entry.getKey(), entry.getValue());
      }
    }
    return s;
  }
  
  public Binding merge(Binding b)
  {
    //System.out.println("Merging");
    Binding m = new Binding();
    for(Map.Entry<String, AST> entry : bindings.entrySet()){
      if(entry.getValue() instanceof Variable){
        AST result = b.get(entry.getKey());
        if(result != null){
          m.bind((Variable)entry.getValue(), result);
          continue;
        }
      }
    }
    return m;
  }

  public Binding combine(Binding b)
  {
    Binding c = new Binding();
    for(Map.Entry<String, AST> entry : bindings.entrySet()){
      if(!c.bind(entry.getKey(), entry.getValue()))
        return null;
    }
    for(Map.Entry<String, AST> bentry : b.bindings.entrySet()){
      if(!c.bind(bentry.getKey(), bentry.getValue()))
        return null;
    }
    return c;
  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    for(Map.Entry<String, AST> entry : bindings.entrySet()){
      sb.append(entry.getKey());
      sb.append(" = ");
      sb.append(entry.getValue().formatString());
      sb.append("\n");
    }
    if(sb.length() > 0) sb.deleteCharAt(sb.length() - 1);//delete last newline
    return sb.toString();
  }

  public boolean equals(Object obj)
  {
    if(obj == null) return false;
    if(!(obj instanceof Binding)) return false;

    Binding other = (Binding)obj;


    //System.out.println(other.bindings.size() + " " + bindings.size());
    if(other.bindings.size() != bindings.size()) return false;

    for(Map.Entry<String, AST> entry : bindings.entrySet()){
      if(other.bindings.containsKey(entry.getKey())){
        if(other.bindings.get(entry.getKey()).equals(entry.getValue())){
          //System.out.println("Good");
          continue;
        }
      }
      return false;
    }
    return true;
  }

  public int hashCode(){ return 0; }
}
