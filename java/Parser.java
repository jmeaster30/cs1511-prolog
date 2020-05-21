import java.util.ArrayList;
import java.lang.StringBuilder;

public class Parser{
  public int top_idx;

  private static Parser instance;

  private Parser(){}

  public static Parser getInstance()
  {
    if(instance == null)
      instance = new Parser();
    return instance;
  }

  public enum TokenType{
    LEFT_PAREN, RIGHT_PAREN, 
    ATOM, VARIABLE, 
    NUMBER, SEMICOLON, 
    COMMA, DOT, 
    IMPLIES, WHITESPACE, 
    UNKNOWN
  }

  class Token{
    private String lexeme;
    private TokenType type;
    
    public Token(String s, TokenType t)
    {
      lexeme = s;
      type = t;
    }

    public String getLexeme(){ return lexeme; }
    public TokenType getType(){ return type; }
  }

  private ArrayList<Token> lex(String input)
  {
    ArrayList<Token> token_list = new ArrayList<Token>();
    
    TokenType type = TokenType.UNKNOWN;
    StringBuilder sb = new StringBuilder();
    boolean cont = false;
    for(int i = 0; i < input.length(); i++)
    {
      sb.append(input.charAt(i));
      switch(input.charAt(i))
      {
        case '(':
          type = TokenType.LEFT_PAREN;
          break;
        case ')':
          type = TokenType.RIGHT_PAREN;
          break;
        case '.':
          type = TokenType.DOT;
          break;
        case ':':
          if(i != input.length() - 1)
          {
            char next = input.charAt(i + 1);
            if(next == '-')
            {
              type = TokenType.IMPLIES;
              sb.append(next);
              i += 1;
            }
          }
          break;
        case ',':
          type = TokenType.COMMA;
          break;
        case ';':
          type = TokenType.SEMICOLON;
          break;
        default:
          if(Character.isWhitespace(input.charAt(i)))
          {
            type = TokenType.WHITESPACE;
          }
          else if(Character.isLowerCase(input.charAt(i)) || Character.isDigit(input.charAt(i)) || input.charAt(i) == '\'')
          {
            type = TokenType.ATOM;
            boolean quote = input.charAt(i) == '\'';
            int j = i + 1;
            for(; j < input.length(); j++)
            {
              if(quote)
              {
                if(input.charAt(j) == '\'')
                {
                  i = j;
                  break;
                }
                if(j == i + 1){
                  if(sb.length() > 0){
                    sb.deleteCharAt(sb.length() - 1);
                  }
                }
                sb.append(input.charAt(j));
              }
              else
              {
                if(!(Character.isLowerCase(input.charAt(j)) || 
                     Character.isUpperCase(input.charAt(j)) ||
                     Character.isDigit(input.charAt(j)) || 
                     input.charAt(j) == '_'))
                {
                  i = j - 1;
                  break;
                }
                else
                {
                  sb.append(input.charAt(j));
                }
              }
            }
            if(j == input.length())
            {
              i = j + 1;
            }
          }
          else if(Character.isUpperCase(input.charAt(i)) || input.charAt(i) == '_')
          {
            type = TokenType.VARIABLE;
            int j = i + 1;
            for(; j < input.length(); j++)
            {
              if(!(Character.isLowerCase(input.charAt(j)) || 
                   Character.isUpperCase(input.charAt(j)) || 
                   input.charAt(j) == '_'))
              {
                i = j - 1;
                break;
              }
              else
              {
                sb.append(input.charAt(j));
              }
            }
            if(j == input.length())
            {
              i = j - 1;
            }
          }
          break;
      }
      token_list.add(new Token(sb.toString(), type));
      sb.setLength(0);
      type = TokenType.UNKNOWN;
    }

    return token_list;
  }

  //recursive descent parsing
  //atom, var -> TERM FTERM
  public AST STMT(ArrayList<Token> token_list)
  {
    Token top = token_list.get(top_idx);
    if(top == null)
    {
      return new Error("Unexpected end of token stream.");
    }
    else if(top.getType() == TokenType.ATOM || top.getType() == TokenType.VARIABLE)
    {
      AST term = TERM(token_list);
      AST fterm = FTERM(token_list);
      if(fterm != null)
      {
        if(fterm instanceof Implicate) {
          //fterm is going to be of type "implicate"
          ((Implicate) fterm).setLeft(term);
          return fterm;
        } else {
          return fterm; // this case is if we got an error
        }
      }
      else
      {
        return term;
      }
    }
    else
    {
      return new Error("Error unrecognized token: " + top.getLexeme());
    }
  }

  //dot -> dot
  //implies -> implies BODY dot
  public AST FTERM(ArrayList<Token> token_list)
  {
    Token top = token_list.get(top_idx);
    if(top == null)
    {
      return new Error("Unexpected end of token stream.");
    }
    else if(top.getType() == TokenType.DOT)
    {
      top_idx++; // consume dot
      return null;
    }
    else if(top.getType() == TokenType.IMPLIES)
    {
      top_idx++; // consume implies
      AST rhs = BODY(token_list);
      
      top = token_list.get(top_idx);
      if(top.getType() == TokenType.DOT){
        top_idx++; // consume dot
        return new Implicate(null, rhs);
      } else {
        return new Error("Error unexxpected token: " + top.getLexeme());  
      }
    }
    else
    {
      return new Error("!!Error unexpected token: " + top.getLexeme());
    }
  }

  //lparen -> lparen BODY rparen
  //atom, var -> TERM FTERM1
  public AST BODY(ArrayList<Token> token_list)
  {
    Token top = token_list.get(top_idx);
    if(top == null)
    {
      return new Error("Unexpected end of token stream.");
    }
    else if(top.getType() == TokenType.LEFT_PAREN)
    {
      top_idx++;//consume left paren
      AST body = BODY(token_list);

      top = token_list.get(top_idx);
      if(top == null)
      {
        return new Error("Unexpected end of token stream.");
      }
      else if(top.getType() == TokenType.RIGHT_PAREN)
      {
        top_idx++;//consume right paren
        return body;
      }
      else
      {
        return new Error("Unexpected token: " + top.getLexeme());
      }
    }
    else if(top.getType() == TokenType.ATOM || top.getType() == TokenType.VARIABLE)
    {
      AST term = TERM(token_list);
      AST fterm = FTERM1(token_list);

      if(fterm == null){
        return term;
      } else if(fterm instanceof Error) {
        return term;
      } else if(fterm instanceof Conjunct) {
        ((Conjunct) fterm).setLeft(term);
        return fterm;
      } else if(fterm instanceof Disjunct) {
        ((Disjunct) fterm).setLeft(term);
        return fterm;
      } else {
        return new Error("Parser error.");
      }
    }
    else
    {
      return new Error("Unexpected token: " + top.getLexeme());
    }
  }

  //rparen, dot -> epsilon
  //semicolon -> semicolon BODY
  //comma -> comma BODY
  public AST FTERM1(ArrayList<Token> token_list)
  {
    Token top = token_list.get(top_idx);
    if(top == null)
    {
      return new Error("Unexpected end of token stream.");
    }
    else if(top.getType() == TokenType.RIGHT_PAREN || top.getType() == TokenType.DOT)
    {
      return null; //epsilon
    }
    else if(top.getType() == TokenType.SEMICOLON)
    {
      top_idx++; // consume semicolon
      
      AST body = BODY(token_list);

      return new Disjunct(null, body);
    }
    else if(top.getType() == TokenType.COMMA)
    {
      top_idx++; // consume comma

      AST body = BODY(token_list);

      return new Conjunct(null, body);
    }
    else
    {
      return new Error("Unexpected token: " + top.getLexeme());
    }
  }
  
  //atom -> atom FATOM
  //var -> var
  public AST TERM(ArrayList<Token> token_list)
  {
    Token top = token_list.get(top_idx);
    if(top == null)
    {
      return new Error("Unexpected end of token stream.");
    }
    else if(top.getType() == TokenType.ATOM)
    {
      AST atom = new Atom(top.getLexeme());
      top_idx++; // consume atom

      AST fatom = FATOM(token_list);
      if(fatom == null) {
        return atom;
      } else if(fatom instanceof Functor){
        //we have a functor with the arguments but no name
        ((Functor)fatom).setName(((Atom)atom).getName());
        return fatom;
      } else {
        return fatom;
      }
    }
    else if(top.getType() == TokenType.VARIABLE)
    {
      AST vari = new Variable(top.getLexeme());
      top_idx++; // consume variable
      
      return vari;
    }
    else
    {
      return new Error("Unexpected token: " + top.getLexeme());
    }
  }
  
  //lparen -> lparen ARGS rparen
  //comma, rparen, semicolon, dot, implies -> epsilon
  public AST FATOM(ArrayList<Token> token_list)
  {
    Token top = token_list.get(top_idx);
    if(top == null)
    {
      return new Error("Unexpected end of token stream.");
    }
    else if(top.getType() == TokenType.LEFT_PAREN)
    {
      top_idx++; // consume left paren
      AST body = ARGS(token_list);

      top = token_list.get(top_idx);
      if(top == null)
      {
        return new Error("Unexpected end of token stream.");
      } 
      else if(top.getType() == TokenType.RIGHT_PAREN) 
      {
        //consume right paren
        top_idx++; 
        return new Functor(null, body);
      }
      else
      {
        return new Error("Unexpected token: " + top.getLexeme());
      }
    }
    else if(top.getType() == TokenType.COMMA || top.getType() == TokenType.RIGHT_PAREN ||
            top.getType() == TokenType.SEMICOLON || top.getType() == TokenType.DOT ||
            top.getType() == TokenType.IMPLIES)
    {
      return null; // epsilon
    }
    else
    {
      return new Error("Unexpected token: " + top.getLexeme());
    }
  }
  
  //atom, var -> TERM FTERM2
  public AST ARGS(ArrayList<Token> token_list)
  {
    Token top = token_list.get(top_idx);
    if(top == null)
    {
      return new Error("Unexpected end of token stream.");
    }
    else if(top.getType() == TokenType.ATOM || top.getType() == TokenType.VARIABLE)
    {
      AST term = TERM(token_list);
      AST fterm = FTERM2(token_list);

      if(fterm == null)
      {
        return term;
      }
      else
      {
        //we got an Args AST node here
        ((Args)fterm).setLeft(term);
        return fterm;
      }
    }
    else
    {
      return new Error("Unexpected token: " + top.getLexeme());
    }
  }
  
  //comma -> comma ARGS
  //rparen -> epsilon
  public AST FTERM2(ArrayList<Token> token_list)
  {
    Token top = token_list.get(top_idx);
    if(top == null)
    {
      return new Error("Unexpected end of token stream.");
    }
    else if(top.getType() == TokenType.COMMA)
    {
      top_idx++; // consume comma

      AST nargs = ARGS(token_list);
      return new Args(null, nargs);
    }
    else if(top.getType() == TokenType.RIGHT_PAREN)
    {
      return null; //epsilon
    }
    else
    {
      return new Error("Unexpected token: " + top.getLexeme());
    }
  }
  
  public boolean errored(AST query)
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

  public AST parse(String input)
  {
    ArrayList<Token> token_list = lex(input);
    
    ArrayList<Token> fixed = new ArrayList<Token>();
    for(Token token : token_list){
      //System.out.println("[" + token.getType().toString() + "] \'" + token.getLexeme() + "\'");
      if(token.getType() != TokenType.WHITESPACE){
        fixed.add(token);
      }
    }

    if(fixed.size() == 0) return null;

    top_idx = 0;   
    return STMT(fixed);
  }
}
