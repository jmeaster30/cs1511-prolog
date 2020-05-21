import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;

public class Main 
{
  public static int load_offset = 1;

  public static String readfact(FileInputStream fis) throws IOException
  {
    StringBuilder line = null;

    int c = fis.read();
    while(c != -1){
      if(line == null) line = new StringBuilder();

      if((char)c == '.')
      {
        line.append((char)c);
        break;
      }

      line.append((char)c);
      c = fis.read();
    }

    if(line == null) return null;
    return line.toString();
  }
  
  public static boolean errored(AST query)
  {
    if(query == null) return true;
    if(query instanceof Error) return true;
    if(query instanceof Atom || query instanceof Variable)
      return false;
    if(query instanceof Functor){
      for(int i = 0; i < ((Functor) query).getArity(); i++)
        if(errored(((Functor) query).getArgs().get(i)))
          return true;
      return false;
    }
    if(query instanceof Conjunct)
      return errored(((Conjunct) query).getLeft()) && errored(((Conjunct) query).getRight());
    if(query instanceof Disjunct)
      return errored(((Disjunct) query).getLeft()) && errored(((Disjunct) query).getRight());
    if(query instanceof Implicate)
      return errored(((Implicate) query).getLeft()) && errored(((Implicate) query).getRight());
    return true;
  }

  public static void loadfile(String file)
  {
    try
    {
      FileInputStream fis = null;

      try {
        fis = new FileInputStream(file);
        
        System.out.println("Loading file '" + file + "'...");
        String line = readfact(fis);
        while(line != null)
        {
          //System.out.println(line);
          AST fact = Parser.getInstance().parse(line);
          Database.getInstance().fact(fact, Main.load_offset);
          line = readfact(fis);
          Main.load_offset += 1;
        }
        //System.out.println("\rFinished " + filename + ".");
      } finally {
        if(fis != null) {
          fis.close();
        }
      }
    } catch(IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws IOException
  {
    //command line arguments
    //if file passed in then we gotta load the file into the database
    Database db = Database.getInstance();
    Parser parser = Parser.getInstance();
    Scanner in = new Scanner(System.in);
    StringBuilder sb = new StringBuilder();
    
    //load fact files into database
    for(int i = 0; i < args.length; i++)
    {
      String filename = args[i];
      loadfile(filename);
    }
    
    //for(AST f : db.getFacts())
    //{
    //  System.out.println(f.formatString());
    //}

    while(true)
    {
      System.out.print("?- ");
      sb.append(in.nextLine());
      //if end of input doesn't end in a dot keep getting input
      if(sb.charAt(sb.length() - 1) == '.')
      {
        AST query = parser.parse(sb.toString());
        
        //System.out.println(query.toString());

        //exit.
        if(query.equals(new Atom("exit"))) break; 

        if(errored(query)){
          System.out.println("Error in query : ");
          System.out.println(query.toString());
        
        } else {
          
          ArrayList<Binding> bindings = null;
          if(query instanceof Conjunct || query instanceof Disjunct){
            bindings = db.query(query, true);
          } else {
            bindings = db.query(query);
          }

          if(bindings == null){
            continue;
          }else if(bindings.size() == 0){
            System.out.println("false.");
          }else{
            for(int i = 0; i < bindings.size(); i++){
              System.out.println(bindings.get(i).toString());
              if(i != bindings.size() - 1)
                System.out.println("");
            }
            System.out.println("true.");          
          }
        }

        sb.setLength(0);
      }
    }
  }
}
