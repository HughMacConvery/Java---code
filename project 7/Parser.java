import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.ArrayList;
public class Parser {
	// current is the current command, i.e., the command that the Parser
	// should act on.
	private String current, next;
	private Scanner scanner;
	private boolean hasMoreCommands;
	public Parser(File src) {
		hasMoreCommands = true;
		try {
			scanner = new Scanner(src);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		current = "";
		next = getNextCommand();
	}
	public boolean hasMoreCommands() {
		return hasMoreCommands;
	}
	public void advance() {
		current = next;
		next = getNextCommand();
	}
	private String getNextCommand() {
		String next;
		// The following regular expression matches any line that is all
		// whitespace or is only a comment.  Continue scanning the input
		// file until we get an actual command or empty the file.
		do {
			if (scanner.hasNextLine())
				next = scanner.nextLine();
			else {
				hasMoreCommands = false;
				return "";
			}
		} while (next.matches("(^\\s*$)|(^\\s*//.*$)"));
		return next;
	}
	public String commandType() {
		String ct = "";
		String [] currentSplit = current.split(" ") ;
		String commandCall = currentSplit [0];
		switch(commandCall) {
		case "add":
		case "sub":
		case "neg":
		case "eq":
		case "gt":
		case "lt":
		case "and":
		case "or":
		case "not": 
			ct = "C_ARITHMETIC";
			break;
		case "push":
			ct = "C_PUSH";
			break;
		case "pop":
			ct = "C_POP";
			break;
		case "label":
			ct = "C_LABLE";
			break;
		case "goto":
			ct = "C_GOTO";
			break;
		case "if-goto":
			ct = "C_IF";
			break;
		case "function":
			ct = "C_FUNCTION";
			break;
		case "return":
			ct = "C_RETURN";
			break;
		case "call":
			ct = "C_CALL";
			break;
		default:
			ct = current;
			System.out.println("no match");
			break;
		}
		return ct;
	}
	public String arg1() {
		String a1 = null;
		String [] argument = current.split(" ");
		if (commandType().equals("C_ARITHMETIC")) {
			a1 = argument[0];
		}
        if (!(commandType().equals("C_RETURN"))) {
        	if (argument.length > 1) {
        	a1 = argument[1];}
		} else {
			throw new IllegalStateException("RETURN can not be called");
		}
        return a1;
	}
	public int arg2() {
		int a2 = 0;
		String argument2 = null;
		String [] argument = current.split(" ");
		if (argument.length > 2) {
			argument2 = argument[2];
		}
		if (commandType().equals("C_PUSH")||commandType().equals("C_POP")||commandType().equals("C_FUNCTION")||commandType().equals("C_CALL")) {
			a2 = Integer.parseInt(argument2);
		}
		else {
			throw new IllegalStateException("This cannot be called");
		}
		return a2;
	}
}