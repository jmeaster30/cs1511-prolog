import java.util.ArrayList;

public class Database
{
  private ArrayList<AST> facts;

  private static Database db_instance;

  private Database()
  {
    facts = new ArrayList<AST>();
    
    //put reserved stuff into facts
    AST write = new Implicate(new Functor("write", new Variable("X")), new Functor("write", new Variable("X")));
    scopeFact(write, Main.load_offset);
    Main.load_offset += 1;
    facts.add(write);

    AST load = new Implicate(new Functor("load", new Variable("X")), new Functor("load", new Variable("X")));
    scopeFact(load, Main.load_offset);
    Main.load_offset += 1;
    facts.add(load);
  }

  public static Database getInstance()
  {
    if(db_instance == null)
      db_instance = new Database();
    return db_instance;
  }

  public ArrayList<AST> getFacts()
  {
    return facts;
  }

  private void scopeFact(AST ast, int scope)
  {
    if(ast instanceof Variable){
      ((Variable) ast).setScope(scope);
    } 
    else if(ast instanceof Functor){
      for(AST arg : ((Functor)ast).getArgs())
        scopeFact(arg, scope);
    } 
    else if(ast instanceof Conjunct){
      scopeFact(((Conjunct) ast).getLeft(), scope);
      scopeFact(((Conjunct) ast).getRight(), scope);
    }
    else if(ast instanceof Disjunct){
      scopeFact(((Disjunct)ast).getLeft(), scope);
      scopeFact(((Disjunct)ast).getRight(), scope);
    }
    else if(ast instanceof Implicate){
      scopeFact(((Implicate)ast).getLeft(), scope);
      scopeFact(((Implicate)ast).getRight(), scope);
    }
  }

  public void fact(AST ast, int scope)
  {
    if(ast == null) return;

    boolean do_query = true;
    
    //System.out.println(ast.formatString());

    if(ast instanceof Functor){
      // load/1
      if(((Functor)ast).getName().equals("load")){
        if(((Functor)ast).getArity() == 1){
          Main.loadfile(((Functor)ast).getArgs().get(0).formatString() + ".pl");
          do_query = false;
        } else {
          do_query = false;
        }
      }
    }

    if(do_query){
      scopeFact(ast, scope);
      //i think just always add it as a fact
      facts.add(ast);
    }
  }

  //should return bindings
  public ArrayList<Binding> query(AST ast)
  {
    if(ast == null) return null;
    
    ArrayList<Binding> all_bindings = new ArrayList<Binding>();

    ArrayList<Binding> true_nb = new ArrayList<Binding>();
    true_nb.add(new Binding());
  
    boolean do_query = true;

    if(ast instanceof Atom){
      // nl.
      if(((Atom)ast).getName().equals("nl")){
        System.out.print("\n");
        all_bindings = true_nb;
        do_query = false;
      }
    }

    if(ast instanceof Functor){
      // write/1
      if(((Functor)ast).getName().equals("write")){
        if(((Functor)ast).getArity() == 1){
          System.out.print(((Functor)ast).getArgs().get(0).formatString());
          all_bindings = true_nb;
          do_query = false;
        } else {
          System.out.println("Error : too many arguments passed to write.");
          do_query = false;
        }
      }
      // load/1
      if(((Functor)ast).getName().equals("load")){
        if(((Functor)ast).getArity() == 1){
          Main.loadfile(((Functor)ast).getArgs().get(0).formatString() + ".pl");
          all_bindings = true_nb;
          do_query = false;
        } else {
          System.out.println("Error : too many arguments passed to load.");
          do_query = false;
        }
      }
    }

    if(do_query)
    {
      for(AST fact : facts)
      {
        ArrayList<Binding> bindings = new ArrayList<Binding>();
        bindings.add(new Binding());
        ArrayList<Binding> results = fact.unifies(ast, bindings);
        all_bindings.addAll(results);
      }
    }

    return all_bindings;
  }

  public ArrayList<Binding> query(AST ast, boolean q_first)
  {
    if(q_first)
    {
      if(ast == null) return null; 

      ArrayList<Binding> all_bindings = new ArrayList<Binding>();
 
      boolean do_query = true;

      if(ast instanceof Functor){
        // write/1
        if(((Functor)ast).getName().equals("write")){
          if(((Functor)ast).getArity() == 1){
            System.out.print(((Functor)ast).getArgs().get(0).formatString());
            all_bindings.add(new Binding());
            do_query = false;
          } else {
            System.out.println("Error : too many arguments passed to write.");
            do_query = false;
          }
        }
        // load/1
        if(((Functor)ast).getName().equals("load")){
          if(((Functor)ast).getArity() == 1){
            Main.loadfile(((Functor)ast).getArgs().get(0).formatString() + ".pl");
            
            all_bindings.add(new Binding());
            do_query = false;
          } else {
            System.out.println("Error : too many arguments passed to load.");
            do_query = false;
          }
        }
      }

      if(do_query)
      {
        for(AST fact : facts)
        {
          ArrayList<Binding> bindings = new ArrayList<Binding>();
          bindings.add(new Binding());
          
          ArrayList<Binding> results = ast.unifies(fact, bindings);
          
          all_bindings.addAll(results);
        }
      }
      return all_bindings;
    }
    else
    {
      return query(ast);
    }
  }
}
