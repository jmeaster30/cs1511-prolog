import java.util.ArrayList;

public class Error extends AST
{
  private String message;
  public Error(String msg)
  {
    message = msg;
  }
  public String getError(){ return message; }

  public ArrayList<Binding> unifies(AST ast, ArrayList<Binding> bindings){
    return new ArrayList<Binding>();
  }
  
  public AST copy()
  {
    return null;
  }
  
  public AST apply(Binding b)
  {
    return null;
  }

  public String toString(){
    return "[Error : " + message + "]";
  }

  public String formatString(){
    return "[Error]";
  }
}
