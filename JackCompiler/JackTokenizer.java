import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.Arrays;


public class JackTokenizer {

	
	private PushbackReader reader;
	private boolean hasMoreTokens;
	private char ch;
	private TokenType tokenType;
	private KeyWord keyWord;
	private char symbol;
	private String identifier;
	private State state;

	public JackTokenizer(File src) {
		hasMoreTokens = true;

		try {
			reader = new PushbackReader(new FileReader(src));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}	
	}

	public boolean hasMoreTokens() {
		return hasMoreTokens;
	}
	
	public TokenType tokenType(){
		return tokenType;
	}
	
	public KeyWord keyWord(){
		return keyWord;
	}
	
	public char symbol(){
		return symbol;
	}
	
	public String identifier(){
		return identifier;
	}
	
	public int intVal(){
		try{
			return Integer.parseInt(identifier);
		}catch (NumberFormatException e){
			e.printStackTrace();
			System.exit(1);
		}
		return -1;
	}
	
	// Return string without the enclosing quotes.
	public String stringVal(){
		return identifier;
	}
	
	private void getNextChar(){
		try{
			int data = reader.read();
			if (data == -1)
				hasMoreTokens = false;
			else
				ch = (char)data;
		} catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void unread(){
		try{
			reader.unread(ch);
		} catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}
	}
		

	public void advance() {
		
		// Complete to advance over next token, setting values in the fields
		this.getNextChar();
		state = State.START;
		identifier = "";
		while(hasMoreTokens()) {
			switch(state) {
			case START:
				if(Character.isDigit(ch)) {
					identifier += ch;
					state = State.NUMBER;
					}
				else if(ch == '"') {
					state = State.STRING;
				}
				else if(Character.isLetter(ch)) {
					identifier += ch;
					state = State.IN_ID;
				}
				else if(ch == '/') {
				
					state = State.SLASH;
				}
				else if(ch == '+' || ch == '-' || ch == '*' || ch == '&' || ch == '|' || ch == '~' || ch == '<' || ch == '>' || ch == '=' || ch == '(' || ch == ')' || ch == '[' || ch == ']' || ch == '{' || ch == '}' || ch == ',' || ch == ';' || ch == '.'){
					tokenType = tokenType.SYMBOL;
					symbol = ch;
					return;
					
				}
				break;
			case NUMBER:
				if (!Character.isDigit(ch)){
					this.unread();
					tokenType = tokenType.INT_CONST;
					return;
				} else {
			
					identifier += ch;
					
				}
				break;
			case IN_ID:
				if(!Character.isLetter(ch) && !Character.isDigit(ch)) {
					this.unread();
					tokenType = tokenType.IDENTIFIER;
					switch(identifier) {
					case "class":
						if(identifier.equals("class")) {
							keyWord = KeyWord.CLASS;
							tokenType = tokenType.KEYWORD;
							return;
						}
						break;
					case "method":
						if(identifier.equals("method")) {
							keyWord = KeyWord.METHOD;
							tokenType = tokenType.KEYWORD;
							return;
						}
						break;
						
					case "function":
						if(identifier.equals("function")) {
							keyWord = KeyWord.FUNCTION;
							tokenType = tokenType.KEYWORD;
							return;
						}
						break;
					case "constructor":
						if(identifier.equals("constructor")) {
							keyWord = KeyWord.CONSTRUCTOR;
							tokenType = tokenType.KEYWORD;
							return;
						}
						break;
					case "int":
						if(identifier.equals("int")) {
							keyWord = KeyWord.INT;
							tokenType = tokenType.KEYWORD;
							return;
						}
						break;
					case "boolean":
						if(identifier.equals("boolean")) {
							keyWord = KeyWord.BOOLEAN;
							tokenType = tokenType.KEYWORD;
							return;
						}
						break;
					case "char":
						if(identifier.equals( "char")){
							keyWord = KeyWord.CHAR;
							tokenType = tokenType.KEYWORD;
							return;
						}
						break;
					case "void":
						if(identifier.equals("void")) {
							keyWord = KeyWord.VOID;
							tokenType = tokenType.KEYWORD;
							return;
						}
						break;
					case "var":
						if(identifier.equals( "var")) {
							keyWord = KeyWord.VAR;
							tokenType = tokenType.KEYWORD;
							return;
						}
						break;
					case "static":
						if(identifier.equals( "static") ){
							keyWord = KeyWord.STATIC;
							tokenType = tokenType.KEYWORD;
							return;			
						}
						break;
					case "field":
						if(identifier.equals( "field")) {
							keyWord = KeyWord.FIELD;
							tokenType = tokenType.KEYWORD;
							return;
						}
						break;
					case "let":
						if(identifier.equals("let")) {
							keyWord = KeyWord.LET;
							tokenType = tokenType.KEYWORD;
							return;
						}
						break;
					case "do":
						if(identifier.equals("do")){
							keyWord = KeyWord.DO;
							tokenType = tokenType.KEYWORD;
							return;
						}
						
						break;
					case "if":
						if(identifier.equals("if")) {
							keyWord = KeyWord.IF;
							tokenType = tokenType.KEYWORD;
							return;
						}
						break;
					case "else":
						if(identifier.equals("else")) {
							keyWord = KeyWord.ELSE;
							tokenType = tokenType.KEYWORD;
							return;
						}
						break;
					case "while":
						if(identifier.equals("while")) {
							keyWord = KeyWord.WHILE;
							tokenType = tokenType.KEYWORD;
							return;
						}
						break;
					case "return":
						if(identifier.equals("return")) {
							keyWord = KeyWord.RETURN;
							tokenType = tokenType.KEYWORD;
							return;
						}
						break;
					case "true":
						if(identifier.equals("true")) {
							keyWord = KeyWord.TRUE;
							tokenType = tokenType.KEYWORD;
							return;
						}
						break;
					case "false":
						if(identifier.equals("false")) {
							keyWord = KeyWord.FALSE;
							tokenType = tokenType.KEYWORD;
							return;
						}
						break;
					case "null":
						if(identifier.equals( "null")) {
							keyWord = KeyWord.NULL;
							tokenType = tokenType.KEYWORD;
							return;
						}
						break;
					case "this":
						if(identifier.equals( "this")) {
							keyWord = KeyWord.THIS;
							tokenType = tokenType.KEYWORD;
							return;
						}
						break;
					}
					return;
				} else {
					
					identifier += ch;
				}
				break;
			case SLASH:
				if(ch == '/') {
					state = State.LINECOMMENT;
				} 
				else if(ch == '*' ) {
					state = State.STARSLASH;
				}
				else {
					this.unread();
					tokenType = TokenType.SYMBOL;
					symbol = '/';
					return;
				}
				break;
			case LINECOMMENT:
				if(ch == '\n') {
					state = State.START;
				}
				
				break;
			case STARSLASH:
				if(ch == '*') {
					state = State.STARSTARSLASH;
				}
				
				break;
			case STARSTARSLASH:
				if(ch == '/') {
					state = State.START;
				}
				else {
					
					state = State.STARSLASH;
				}
				break;
			case STRING:
				if(ch == '"') {
					tokenType = tokenType.STRING_CONST;
				return;
				} 
				else {
					
					identifier += ch;				
				}				
			}
			this.getNextChar();
			
		}		
	}


}
