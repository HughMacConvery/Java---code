import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class CompilationEngine {
	JackTokenizer tokenizer = null;
	XMLWriter xmlWriter = null;
	SymbolTable symbolTable = null;
	VMWriter vmwriter = null;

	private String symbol;
	private String type;
	private STKind kind;
	private String x;
	private int locals;
	private String Name;
	private String fileName;
	private String j;
	private KeyWord savekeyword;
	private char tilda;
	private int ifs;
	private int whiles;
	private String method;

	public CompilationEngine(String filename, File file){
	    tokenizer = new JackTokenizer(file);
	    xmlWriter = new XMLWriter(filename);
	    vmwriter = new VMWriter(filename);
	    symbolTable = new SymbolTable();
	    x = tokenizer.identifier();
	   ifs =0;
	   whiles = 0;    

	}
	public void close(){
		try {
			xmlWriter.close();
			vmwriter.close();
		}
		catch(Exception e){e.printStackTrace();}

	}
	private void symbolInfo(String use, String identifier){
		xmlWriter.writeCode(use+" "+identifier+ " type:"+symbolTable.typeOf(identifier)+" kind:"+
				symbolTable.kindOf(identifier)+ " index:"+symbolTable.indexOf(identifier)+'\n');
		System.out.println(use+" "+identifier+ " type:"+symbolTable.typeOf(identifier)+" kind:"+
				symbolTable.kindOf(identifier)+ " index:"+symbolTable.indexOf(identifier)+'\n');

	}
	/* REPLACE METHODS BELOW WITH YOUR CODE FROM PROJECT 2 AND THEN EDIT FOR PROJECT 3.
	 *  USE symbolInfo TO PRINT OUT INFO FOR DEFINING OR USING VARIABLES IN SYMBOL TABLE*/
	public Segment ktoS(STKind x) {
		if(x == STKind.ARG) {
			return Segment.ARG;
		}
		else if(x == STKind.FIELD) {
			return Segment.THIS;
		}
		else if(x == STKind.VAR) {
			return Segment.LOCAL;

		}
		else if(x == STKind.STATIC) {
			return Segment.STATIC;

		} else {
			return null;
		}
	}

	public void compileClass() {
		xmlWriter.writeCode("<class>\n");
		// COMPLETE THIS CODE
		tokenizer.advance();
		xmlWriter.writeCode("<keyword> class  </keyword>\n");
		tokenizer.advance();
		xmlWriter.writeCode("<identifier>" + tokenizer.identifier() + "</identifier>\n");
		fileName = tokenizer.identifier();
		tokenizer.advance();
		xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
		tokenizer.advance();
		while (tokenizer.tokenType() == TokenType.KEYWORD && (tokenizer.keyWord() == KeyWord.FIELD || tokenizer.keyWord() == KeyWord.STATIC)) {
			this.compileClassVarDec();
		}

		while (tokenizer.tokenType() == TokenType.KEYWORD && (tokenizer.keyWord() == KeyWord.CONSTRUCTOR
				|| tokenizer.keyWord() == KeyWord.FUNCTION || tokenizer.keyWord() == KeyWord.METHOD)) {
			this.compileSubroutineDec();

		}
		xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
		tokenizer.advance();
		xmlWriter.writeCode("</class>\n");
		close();
	}
	public void compileClassVarDec() {
		xmlWriter.writeCode("<classVarDec>\n");
		if (tokenizer.tokenType() == TokenType.KEYWORD) {
			if (tokenizer.keyWord() == KeyWord.STATIC) {
				xmlWriter.writeCode("<keyword> static </keyword>\n");
				kind = kind.STATIC;
				tokenizer.advance();

			} else if (tokenizer.keyWord() == KeyWord.FIELD) {
				xmlWriter.writeCode("<keyword> field </keyword>\n");
				kind = kind.FIELD;
				tokenizer.advance();	
			}
		}

		this.compileType();
		symbolTable.define(tokenizer.identifier(), type, kind);
		this.symbolInfo("Define", tokenizer.identifier());
		xmlWriter.writeCode("<identifier>" + tokenizer.identifier() + "</identifier>\n");
		tokenizer.advance();
		this.compileVarNameList();
		xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
		tokenizer.advance();
		xmlWriter.writeCode("</classVarDec>\n");

	}
	public void compileType() {
		if (tokenizer.tokenType() == TokenType.KEYWORD) {
			if (tokenizer.keyWord() == KeyWord.INT) {
				xmlWriter.writeCode("<keyword> int </keyword>\n");
				type = "int";
				tokenizer.advance();

			} else if (tokenizer.keyWord() == KeyWord.CHAR) {
				xmlWriter.writeCode("<keyword> char </keyword>\n");
				type = "char";
				tokenizer.advance();

			} else if (tokenizer.keyWord() == KeyWord.BOOLEAN) {
				xmlWriter.writeCode("<keyword> boolean </keyword>\n");
				type = "boolean";
				tokenizer.advance();
			}

		} else if (tokenizer.tokenType() == TokenType.IDENTIFIER) {
			xmlWriter.writeCode("<identifier>" + tokenizer.identifier() + "</identifier>\n");
			type = tokenizer.identifier();
			tokenizer.advance();
		}
	}

	public void compileVarNameList() {
		while (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ',') {
			xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
			tokenizer.advance();
			symbolTable.define(tokenizer.identifier(), type, kind);
			this.symbolInfo("Define", tokenizer.identifier());
			locals++;
			xmlWriter.writeCode("<identifier>" + tokenizer.identifier() + "</identifier>\n");
			tokenizer.advance();
		}
	}

	public void compileSubroutineDec() {
		xmlWriter.writeCode("<subroutineDec>\n");
		symbolTable.startSubroutine();
		if (tokenizer.tokenType() == TokenType.KEYWORD) {
			if (tokenizer.keyWord() == KeyWord.CONSTRUCTOR) {
				xmlWriter.writeCode("<keyword> constructor </keyword>\n");
				savekeyword = tokenizer.keyWord();
				tokenizer.advance();

			}
			else if (tokenizer.keyWord() == KeyWord.FUNCTION) {
				xmlWriter.writeCode("<keyword> function </keyword>\n");
				savekeyword = tokenizer.keyWord();
				tokenizer.advance();

			} else if (tokenizer.keyWord() == KeyWord.METHOD) {
				xmlWriter.writeCode("<keyword> method </keyword>\n");
				savekeyword = tokenizer.keyWord();
				symbolTable.define("this", type, kind.ARG);
				tokenizer.advance();

			}

		}
		if (tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() == KeyWord.VOID) {
			xmlWriter.writeCode("<keyword> void </keyword>\n");
			tokenizer.advance();

		} else {

			this.compileType();
		}
		xmlWriter.writeCode("<identifier>" + tokenizer.identifier() + "</identifier>\n");
		Name = tokenizer.identifier();
		tokenizer.advance();
		xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
		tokenizer.advance();
		this.compileParameterList();
		xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
		tokenizer.advance();
		this.compileSubroutineBody();
		xmlWriter.writeCode("</subroutineDec>\n");
	}
	public void compileParameterList() { 
		xmlWriter.writeCode("<parameterList>\n");
		if (!(tokenizer.symbol() == ')')) {
			this.compileType();
			kind = kind.ARG;
			symbolTable.define(tokenizer.identifier(), type, kind);
			this.symbolInfo("Define", tokenizer.identifier());
			xmlWriter.writeCode("<identifier>" + tokenizer.identifier() + "</identifier>\n");
			tokenizer.advance();
			while (tokenizer.symbol() == ',') {
				xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
				tokenizer.advance();
				this.compileType();
				symbolTable.define(tokenizer.identifier(), type, kind);
				this.symbolInfo("Define", tokenizer.identifier());
				xmlWriter.writeCode("<identifier>" + tokenizer.identifier() + "</identifier>\n");
				tokenizer.advance();
			}
		}
		xmlWriter.writeCode("</parameterList>\n");

	}

	public void compileSubroutineBody() {
		xmlWriter.writeCode("<subroutineBody>\n");
		xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
		tokenizer.advance();
		locals = 0;
		while (!(tokenizer.tokenType() == TokenType.KEYWORD && (tokenizer.keyWord() == KeyWord.LET
				|| tokenizer.keyWord() == KeyWord.IF || tokenizer.keyWord() == KeyWord.WHILE
				|| tokenizer.keyWord() == KeyWord.DO || tokenizer.keyWord() == KeyWord.RETURN))) {

			this.compileVarDec();
		}
		vmwriter.writeFunction(fileName+ "." + Name, locals);
		if(savekeyword == KeyWord.CONSTRUCTOR) {
			int fieldcnt =symbolTable.varCount(STKind.FIELD);
			vmwriter.push(fieldcnt, Segment.CONST);
			vmwriter.writeCall("Memory"+"."+"alloc", 1);
			vmwriter.pop(0, Segment.POINTER); //might be wrong
		}
		else if(savekeyword == KeyWord.METHOD) {
			vmwriter.push(0,Segment.ARG );
			vmwriter.pop(0,Segment.POINTER);
		}
		locals = 0;
		this.compileStatements();
		xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
		tokenizer.advance();
		xmlWriter.writeCode("</subroutineBody>\n");
	}
	public void compileVarDec() {
		xmlWriter.writeCode("<varDec>\n");
		if (tokenizer.tokenType() == TokenType.KEYWORD && (tokenizer.keyWord() == KeyWord.VAR)) {
			xmlWriter.writeCode("<keyword> var </keyword>\n");
			kind = kind.VAR;
			tokenizer.advance();
		} 
		this.compileType();
		locals++;
		symbolTable.define(tokenizer.identifier(), type, kind);
		this.symbolInfo("Define", tokenizer.identifier());
		xmlWriter.writeCode("<identifier>" + tokenizer.identifier() + "</identifier>\n");
		tokenizer.advance();
		this.compileVarNameList();
		xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
		tokenizer.advance();
		xmlWriter.writeCode("</varDec>\n");

	}
	public void compileStatements() {
		xmlWriter.writeCode("<statements>\n");
		while (tokenizer.tokenType() == TokenType.KEYWORD && (tokenizer.keyWord() == KeyWord.LET
				|| tokenizer.keyWord() == KeyWord.IF || tokenizer.keyWord() == KeyWord.WHILE
				|| tokenizer.keyWord() == KeyWord.DO || tokenizer.keyWord() == KeyWord.RETURN))
			if (tokenizer.keyWord() == KeyWord.LET) {
				this.compileLetStatement();
			}
			else if (tokenizer.keyWord() == KeyWord.IF) {
			this.compileIfStatement();

		}
			else if (tokenizer.keyWord() == KeyWord.WHILE) {
			this.compileWhileStatement();
		}
			else if (tokenizer.keyWord() == KeyWord.DO) {
			this.compileDoStatement();
		}
			else if (tokenizer.keyWord() == KeyWord.RETURN) {
			this.compileReturnStatement();
		}

		xmlWriter.writeCode("</statements>\n");

	}
	public void compileLetStatement() {
		xmlWriter.writeCode("<letStatement>\n");
		if (tokenizer.keyWord() == KeyWord.LET) {
			xmlWriter.writeCode("<keyword> let </keyword>\n");
			tokenizer.advance();

		}
		xmlWriter.writeCode("<identifier>" + tokenizer.identifier() + "</identifier>\n");
		String g = tokenizer.identifier();
		this.symbolInfo("Use", tokenizer.identifier());
		tokenizer.advance();

		if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '[') {
			xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
			tokenizer.advance();
			method = tokenizer.identifier();
			this.compileExpression();
			xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
			tokenizer.advance();
		}
		xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
		tokenizer.advance();
		method = tokenizer.identifier();
		this.compileExpression();
		xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
		tokenizer.advance();
		vmwriter.pop(symbolTable.indexOf(g), this.ktoS(symbolTable.kindOf(g)));
		xmlWriter.writeCode("</letStatement>\n");

	}
	public void compileIfStatement() {
		xmlWriter.writeCode("<ifStatement>\n");
		if (tokenizer.keyWord() == KeyWord.IF) {
			xmlWriter.writeCode("<keyword> if </keyword>\n");
			tokenizer.advance();

		}
		xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
		tokenizer.advance();
		this.compileExpression();
		xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
		tokenizer.advance();
		xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
		tokenizer.advance();
		String iftrue= "IF_TRUE";
		String iffalse = "IF_FALSE";
		String ifend = "IF_END";

		vmwriter.writeIf(iftrue + ifs); //check the locals
		vmwriter.writeGoto(iffalse + ifs); //check the locals
		vmwriter.returnLabel(iftrue+ ifs);

		int oldifs = ifs;
		ifs++;
		this.compileStatements();
	
		xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
		tokenizer.advance();
		if (tokenizer.keyWord() == KeyWord.ELSE) {
			vmwriter.writeGoto(ifend + oldifs);
			vmwriter.returnLabel(iffalse + oldifs);
			xmlWriter.writeCode("<keyword> else </keyword> \n");
			tokenizer.advance();
			xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
			tokenizer.advance();
			this.compileStatements();

			xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
			tokenizer.advance();
			vmwriter.returnLabel(ifend + oldifs);

		} else {
			vmwriter.returnLabel(iffalse+ oldifs);
		}
		xmlWriter.writeCode("</ifStatement>\n");
	}

	public void compileWhileStatement() {
		xmlWriter.writeCode("<whileStatement>\n");
		if (tokenizer.keyWord() == KeyWord.WHILE) {
			xmlWriter.writeCode("<keyword> while </keyword> \n");
			tokenizer.advance();

		}
		xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
		tokenizer.advance();
		String whileexp = "WHILE_EXP";
		String whileend = "WHILE_END";
		vmwriter.returnLabel(whileexp + whiles);
		int firstoldlabel = whiles;
		this.compileExpression();
		vmwriter.writeArithmetic(Command.NOT);
		xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
		tokenizer.advance();
		xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
		tokenizer.advance();
		vmwriter.writeIf(whileend+ whiles);
		int secondoldlabel = whiles;
		whiles++;
		this.compileStatements();
		vmwriter.writeGoto(whileexp+firstoldlabel);
		vmwriter.returnLabel(whileend + secondoldlabel);
		xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
		tokenizer.advance();
		xmlWriter.writeCode("</whileStatement>\n");

	}

	public void compileDoStatement() {
		xmlWriter.writeCode("<doStatement>\n");
		if (tokenizer.keyWord() == KeyWord.DO) {
			xmlWriter.writeCode("<keyword> do </keyword> \n");
			tokenizer.advance();

		}
		xmlWriter.writeCode("<identifier>" + tokenizer.identifier() + "</identifier> \n");
		x= tokenizer.identifier(); 
		method = tokenizer.identifier();
		tokenizer.advance();
		this.compileSubroutineCall();
		vmwriter.pop(0, Segment.TEMP);
		xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
		tokenizer.advance();
		xmlWriter.writeCode("</doStatement>\n");

	}
	public void compileReturnStatement() {
		xmlWriter.writeCode("<returnStatement>\n");
		if (tokenizer.keyWord() == KeyWord.RETURN) {
			xmlWriter.writeCode("<keyword> return </keyword> \n");
			tokenizer.advance();
		}
		if (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ';')) {
			this.compileExpression();
		} else {
			vmwriter.push(0, Segment.CONST);
		}
		xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
		tokenizer.advance();
		vmwriter.writeReturn();
		xmlWriter.writeCode("</returnStatement>\n");
	}

	public void compileExpression() {
		xmlWriter.writeCode("<expression>\n");
		char less = 'l' ;
		char greater= 'g';
		char and = 'a';
		char multiply = 'm';
		char divide = 'd';
		char or = 'o';
		char eq = 'e';
		char add= 'a';
		this.compileTerm();
		multiply = tokenizer.symbol();
		divide = tokenizer.symbol();
		eq = tokenizer.symbol();
		add = tokenizer.symbol();
		or = tokenizer.symbol();
		symbol = "+-*/&|<>=";
		while (symbol.indexOf(tokenizer.symbol()) != -1) {
			if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '<') {
				xmlWriter.writeCode("<symbol> &lt; </symbol>\n");
				less = tokenizer.symbol();
				tokenizer.advance();

			} else if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '>') {
				xmlWriter.writeCode("<symbol> &gt; </symbol>\n");
				greater = tokenizer.symbol();
				tokenizer.advance();

			} else if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '<') {
				xmlWriter.writeCode("<symbol> &lt; </symbol>\n");
				tokenizer.advance();
			} else if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '&') {
				xmlWriter.writeCode("<symbol> &amp; </symbol>\n");
				and = tokenizer.symbol();
				tokenizer.advance();

			} else {
				xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
				tokenizer.advance();
			}

			char tokensymbol = tokenizer.symbol();
			this.compileTerm();
			if(tokensymbol == '+') {
				vmwriter.writeArithmetic(Command.ADD);
			}

			else if(less == '<') {
				vmwriter.writeArithmetic(Command.LT);
				less = ' ';

			}
			else if(greater == '>') {
				vmwriter.writeArithmetic(Command.GT);
				greater = ' ';
			}	
			else if(and == '&') {
				vmwriter.writeArithmetic(Command.AND);
				and = ' ';
			}
			else if(tokensymbol == '-') {
				vmwriter.writeArithmetic(Command.SUB);
			}
			else if(tokensymbol == '=') {
				vmwriter.writeArithmetic(Command.EQ);
			} 
			else if(multiply == '*') {
				vmwriter.writeCall("Math.multiply",2);
				multiply = ' ';
			}
			else if(divide == '/') {
				vmwriter.writeCall("Math.divide", 2);
				divide = ' ';
			}
			else if(or == '|') {
				vmwriter.writeArithmetic(Command.OR);
				or = ' ';
			}
			else if(eq == '=') {
				vmwriter.writeArithmetic(Command.EQ);
				eq = ' ';
			}
			else if(add == '+') {
				vmwriter.writeArithmetic(Command.ADD);
				add = ' ';
			}

		}
		xmlWriter.writeCode("</expression>\n");

	}

	public void compileTerm() {
		xmlWriter.writeCode("<term>\n");
		if (tokenizer.tokenType() == TokenType.INT_CONST) {
			xmlWriter.writeCode("<integerConstant>" + tokenizer.identifier() + "</integerConstant> \n");
			String num = tokenizer.identifier();
			int constant = Integer.parseInt(num);
			vmwriter.push(constant, Segment.CONST);
			tokenizer.advance();

		} else if (tokenizer.tokenType() == TokenType.STRING_CONST) {
			xmlWriter.writeCode("<stringConstant>" + tokenizer.identifier() + "</stringConstant> \n");
			String s = tokenizer.identifier();
			vmwriter.push(s.length(), Segment.CONST);
			vmwriter.writeCall("String.new", 1);
			for(int i = 0 ; i<s.length(); i++ ) {
				vmwriter.push(s.charAt(i), Segment.CONST);
				vmwriter.writeCall("String.appendChar", 2);
			}
			tokenizer.advance();

		} else if (tokenizer.tokenType() == TokenType.KEYWORD
				&& (tokenizer.keyWord() == KeyWord.TRUE || tokenizer.keyWord() == KeyWord.FALSE
						|| tokenizer.keyWord() == KeyWord.NULL || tokenizer.keyWord() == KeyWord.THIS)) {
			if (tokenizer.keyWord() == KeyWord.TRUE) {
				xmlWriter.writeCode("<keyword> true </keyword> \n");
				vmwriter.push(0, Segment.CONST);
				vmwriter.writeArithmetic(Command.NOT);
				tokenizer.advance();

			} else if (tokenizer.keyWord() == KeyWord.FALSE) {
				xmlWriter.writeCode("<keyword> false </keyword> \n");
				vmwriter.push(0, Segment.CONST);
				tokenizer.advance();

			} else if (tokenizer.keyWord() == KeyWord.NULL) {
				xmlWriter.writeCode("<keyword> null </keyword> \n");
				tokenizer.advance();

			} else if (tokenizer.keyWord() == KeyWord.THIS) {
				xmlWriter.writeCode("<keyword> this </keyword> \n");
				vmwriter.push(0, Segment.POINTER);
				tokenizer.advance();
			}

		} else if (tokenizer.tokenType() == TokenType.IDENTIFIER) {
			xmlWriter.writeCode("<identifier>" + tokenizer.identifier() + "</identifier> \n");
			x = tokenizer.identifier();
			tokenizer.advance();
			if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '[') {
				this.symbolInfo("Use", x);//
				xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
				tokenizer.advance();
				this.compileExpression();
				xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
				tokenizer.advance();
			} 

			else if ( tokenizer.symbol() == '.' || tokenizer.symbol() == '(') {
				this.compileSubroutineCall();
			}
			else if(tokenizer.symbol() == '.') {
				if(symbolTable.indexOf(x) != -1) {
					this.symbolInfo("Use",x);
				}	

			}
			else {
				this.symbolInfo("Use",x);
				vmwriter.push(symbolTable.indexOf(x), this.ktoS(symbolTable.kindOf(x)));

			}

		} else if(tokenizer.tokenType() == TokenType.SYMBOL&& tokenizer.symbol() == '(') {
			xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
			tokenizer.advance();
			this.compileExpression();
			xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
			tokenizer.advance();

		} else if(tokenizer.tokenType() == TokenType.SYMBOL && (tokenizer.symbol() == '-' || tokenizer.symbol() == '~' )) {
			char neg = ' ';
			if(tokenizer.symbol() == '~') {
				tilda = tokenizer.symbol();
				}
			else if(tokenizer.symbol() == '-') {
				neg = tokenizer.symbol();

			}
			xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
			tokenizer.advance();
			this.compileTerm();
			if(tilda == '~') {
				vmwriter.writeArithmetic(Command.NOT);
				tilda = ' ';
			}
			if(neg == '-') {
				vmwriter.writeArithmetic(Command.NEG);
				neg = ' ';

			}

		}

		xmlWriter.writeCode("</term>\n");

	}
	public void compileSubroutineCall() {
		locals = 0;
		if(tokenizer.tokenType() == TokenType.SYMBOL && (tokenizer.symbol() == '.' || tokenizer.symbol() == '(')){
			if(tokenizer.symbol() == '(') {
				vmwriter.push(locals, Segment.POINTER);
				xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
				tokenizer.advance();
				locals++;
				if(!(tokenizer.tokenType() == TokenType.SYMBOL &&(tokenizer.symbol() == ')'))){
					this.compileExpressionList();

					} else {
						xmlWriter.writeCode("<expressionList>\n");
						xmlWriter.writeCode("</expressionList>\n");
					}
				vmwriter.writeCall(fileName + "." + method  , locals);
				xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
				tokenizer.advance();

			} else if(tokenizer.symbol() == '.') {
				if(symbolTable.indexOf(x) != -1) {
					this.symbolInfo("Use",x);
					vmwriter.push( symbolTable.indexOf(x),this.ktoS(symbolTable.kindOf(x)) );
					xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
					tokenizer.advance();
					xmlWriter.writeCode("<identifier>" + tokenizer.identifier() + "</identifier> \n");
					j = tokenizer.identifier();
					tokenizer.advance();
					xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
					tokenizer.advance();
					if(!(tokenizer.tokenType() == TokenType.SYMBOL &&(tokenizer.symbol() == ')'))){
						this.compileExpressionList();
						locals++;

						} else {
							locals++;
							xmlWriter.writeCode("<expressionList>\n");
							xmlWriter.writeCode("</expressionList>\n");
						}
					vmwriter.writeCall(symbolTable.typeOf(method) + "." + j, locals);
					xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
					tokenizer.advance();

				} else {
				xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
				tokenizer.advance();
				xmlWriter.writeCode("<identifier>" + tokenizer.identifier() + "</identifier> \n");
				String j = x;
				String method = tokenizer.identifier();
				tokenizer.advance();
				xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
				tokenizer.advance();
				if(!(tokenizer.tokenType() == TokenType.SYMBOL &&(tokenizer.symbol() == ')'))){
					this.compileExpressionList();
					} else {
						xmlWriter.writeCode("<expressionList>\n");
						xmlWriter.writeCode("</expressionList>\n");
					}
				vmwriter.writeCall(j + "." + method, locals);
				xmlWriter.writeCode("<symbol>" + tokenizer.symbol() + "</symbol>\n");
				tokenizer.advance();
				}

			}

		}

	}

	public void compileExpressionList() {
		xmlWriter.writeCode("<expressionList>\n");
			this.compileExpression();
			locals++;	
			while (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ',') {
				locals++;
				xmlWriter.writeCode("<symbol>"+ tokenizer.symbol() + "</symbol>\n");
				tokenizer.advance();
				this.compileExpression();
			}


		xmlWriter.writeCode("</expressionList>\n");

	}

}

