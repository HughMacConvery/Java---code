import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class CodeWriter {
	private FileWriter asmFile;
	private String fileName = "";
	private String functionName = "";
	private int jump = 0;
	public CodeWriter(String basename) {
		try {  // Open the ASM file for writing.
			asmFile = new FileWriter(basename + ".asm");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
		functionName = "";
	}
	// You might find this useful.
	// Write a sequence of ASM code to the ASM file.
	// The parameter code is expected to contain all necessary newline
	// characters.
	private void writeCode(String code) {
		try {
			asmFile.write(code);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	public String negCode() {
		String negHack = "";
		negHack = negHack.concat("D=0\n");
		negHack = negHack.concat("@SP\n");
		negHack = negHack.concat("A=M-1\n");
		negHack = negHack.concat("M=D-M\n");
		return negHack;
	}
	public String notCode() {
		String notHack = "";
		notHack = notHack.concat("@SP\n");
		notHack = notHack.concat("A=M-1\n");
		notHack = notHack.concat("M=!M\n");
		return notHack;
	}
	public String ArithmeticHack1() {
		String hack1 = "";
		hack1 = hack1.concat("@SP\n");
		hack1 = hack1.concat("AM=M-1\n");
		hack1 = hack1.concat("D=M\n");
		hack1 = hack1.concat("A=A-1\n");
		return hack1;}
	public String ArithmeticHack2(String jumpString) {
		String hack2 = "";
		hack2 = hack2.concat("@SP\n");
		hack2 = hack2.concat("AM=M-1\n");
		hack2 = hack2.concat("D=M\n");
		hack2 = hack2.concat("A=A-1\n");
		hack2 = hack2.concat("D=M-D\n");
		hack2 = hack2.concat("@FALSE" + jump + "\n");
		hack2 = hack2.concat("D;");
		hack2 = hack2.concat(jumpString);
		hack2 = hack2.concat("\n@SP\n");
		hack2 = hack2.concat("A=M-1\n");
		hack2 = hack2.concat("M=-1\n");
		hack2 = hack2.concat("@CONTINUE" + jump);
		hack2 = hack2.concat("\n0;JMP\n");
		hack2 = hack2.concat("(FALSE" + jump + ")\n");
		hack2 = hack2.concat("@SP\n");
		hack2 = hack2.concat("A=M-1\n");
		hack2 = hack2.concat("M=0\n");
		hack2 = hack2.concat("(CONTINUE" + jump + ")\n");
		return hack2;}
	public void writeArithmetic(String command) {
		//System.out.println("enter WA");
		String arithCode = "";
		if (command.equals("add")) {
			arithCode = ArithmeticHack1().concat("M=M+D\n");}
		else if (command.equals("sub")) {
			arithCode = ArithmeticHack1().concat("M=M-D\n");}
		else if (command.equals("neg")) {
			arithCode = negCode();}
		else if (command.equals("eq")) {
			arithCode = ArithmeticHack2("JNE");
			jump++;}
		else if (command.equals("gt")) {
			arithCode = ArithmeticHack2("JLE");
			jump++;}
		else if (command.equals("lt")) {
			arithCode = ArithmeticHack2("JGE");
			jump++;}
		else if (command.equals("and")) {
			arithCode = ArithmeticHack1().concat("M=M&D\n");}
		else if (command.equals("or")) {
			arithCode = ArithmeticHack1().concat("M=M|D\n");}
		else if (command.equals("not")) {
			arithCode = notCode();}
		try {
			asmFile.write(arithCode);
		}catch (IOException e) {
			e.printStackTrace();}
		System.out.println(arithCode);
		}
    public String pushCode1(String segment, int index) {
        String push1 = "";
        push1 = push1.concat("@");
        push1 = push1.concat(segment);
        push1 = push1.concat("\nD=M\n@" + index + "\n");
        push1 = push1.concat("A=D+A\n");
        push1 = push1.concat("D=M\n");
        push1 = push1.concat("@SP\n");
        push1 = push1.concat("A=M\n");
        push1 = push1.concat("M=D\n");
        push1 = push1.concat("@SP\n");
        push1 = push1.concat("M=M+1\n");
        return push1;}
    public String pushCode2(String segment) {
        String push2 = "";
        push2 = push2.concat("@");
        push2 = push2.concat(segment);
        push2 = push2.concat("\nD=M\n");
        push2 = push2.concat("@SP\n");
        push2 = push2.concat("A=M\n");
        push2 = push2.concat("M=D\n");
        push2 = push2.concat("@SP\n");
        push2 = push2.concat("M=M+1\n");
        return push2;}
    public String popCode1(String segment, int index) {
        String pop1 = "";
        pop1 = pop1.concat("@");
        pop1 = pop1.concat(segment);
        pop1 = pop1.concat("\nD=M\n@" + index + "\n");
        pop1 = pop1.concat("D=D+A\n");
        pop1 = pop1.concat("@R13\n");
        pop1 = pop1.concat("M=D\n");
        pop1 = pop1.concat("@SP\n");
        pop1 = pop1.concat("AM=M-1\n");
        pop1 = pop1.concat("D=M\n");
        pop1 = pop1.concat("@R13\n");
        pop1 = pop1.concat("A=M\n");
        pop1 = pop1.concat("M=D\n");
        return pop1;}
    public String popCode2(String segment) {
        String pop2 = "";
        pop2 = pop2.concat("@");
        pop2 = pop2.concat(segment);
        pop2 = pop2.concat("\nD=A\n");
        pop2 = pop2.concat("@R13\n");
        pop2 = pop2.concat("M=D\n");
        pop2 = pop2.concat("@SP\n");
        pop2 = pop2.concat("AM=M-1\n");
        pop2 = pop2.concat("D=M\n");
        pop2 = pop2.concat("@R13\n");
        pop2 = pop2.concat("A=M\n");
        pop2 = pop2.concat("M=D\n");
        return pop2;}
	public void writePushPop(String command, String segment, int index) {
		String pushPopCode = null;
		//System.out.println("enter wPP");
		//System.out.println(segment);
		if (command.equals("C_PUSH")) {
			//System.out.println("enter push");
			if (segment.equals("static")) {
				pushPopCode = pushCode2(String.valueOf(16 + index));
			}
			else if (segment.equals("constant")) {
				String pushC = "";
				pushC = pushC.concat("@" + index + "\n");
				pushC = pushC.concat("D=A\n");
				pushC = pushC.concat("@SP\n");
				pushC = pushC.concat("A=M\n");
				pushC = pushC.concat("M=D\n");
				pushC = pushC.concat("@SP\n");
				pushC = pushC.concat("M=M+1\n");
				pushPopCode = pushC;
			}
			else if (segment.equals("this")) {
				pushPopCode = pushCode1("THIS", index);
			}
			else if (segment.equals("that")) {
				pushPopCode = pushCode1("THAT", index);
			}
			else if (segment.equals("local")) {
				pushPopCode = pushCode1("LCL", index);
			}
			else if (segment.equals("argument")) {
				pushPopCode = pushCode1("ARG", index);
			}
			else if (segment.equals("temp")) {
				pushPopCode = pushCode1("R5", index + 5);
			}
			else if (segment.equals("pointer") && index == 0) {
				pushPopCode = pushCode2("THIS");
			}
			else if (segment.equals("pointer") && index == 1) {
				pushPopCode = pushCode2("THAT");
			}
		}
		if (command.equals("C_POP")) {
			if (segment.equals("static")) {
				pushPopCode = popCode2(String.valueOf(16 + index));
			}
			else if (segment.equals("constant")) {
				String popC = "";
				popC = popC.concat("@" + index + "\n");
				popC = popC.concat("D=A\n");
				popC = popC.concat("@SP\n");
				popC = popC.concat("A=M\n");
				popC = popC.concat("M=D\n");
				popC = popC.concat("@SP\n");
				popC = popC.concat("M=M+1\n");
				pushPopCode = popC;
			}
			else if (segment.equals("this")) {
				pushPopCode = popCode1("THIS", index);
			}
			else if (segment.equals("that")) {
				pushPopCode = popCode1("THAT", index);
			}
			else if (segment.equals("local")) {
				pushPopCode = popCode1("LCL", index);
			}
			else if (segment.equals("argument")) {
				pushPopCode = popCode1("ARG", index);
			}
			else if (segment.equals("temp")) {
				pushPopCode = popCode1("R5", index + 5);
			}
			else if (segment.equals("pointer") && index == 0) {
				pushPopCode = popCode2("THIS");
			}
			else if (segment.equals("pointer") && index == 1) {
				pushPopCode = popCode2("THAT");
			}
		}
		try {
			asmFile.write(pushPopCode);
		}catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(pushPopCode);
	}
	public void close() {
		try {
			asmFile.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		}
	public void wInit() {
		String InIt = "";
		InIt = InIt.concat("@256\n");
		InIt = InIt.concat("D=A\n");
		InIt = InIt.concat("@SP\n");
		InIt = InIt.concat("M=D\n");
		try {
			asmFile.write(InIt);
			wCall("Sys.init", 0);
		} catch (IOException e) {
			e.printStackTrace();
			}
		}
	public void wLabel(String label) {
		String L = "";
		L = L.concat("(");
		L = L.concat(label);
		L = L.concat(")\n");
		try {
			asmFile.write(L);
			} catch (IOException e) {
				e.printStackTrace();
				}
		System.out.println(L);
		}
	public void wGoto (String label) {
		String Gt = "";
		Gt = Gt.concat("@");
		Gt = Gt.concat(label);
		Gt = Gt.concat("\n0;JMP\n");
		try {
			asmFile.write(Gt);
			} catch (IOException e) {
				e.printStackTrace();
				}
		System.out.println(Gt);
		}
	public void wIf (String label) {
		String ifState = "";
		ifState = ifState.concat(ArithmeticHack1());
		ifState = ifState.concat("@");
		ifState = ifState.concat(label);
		ifState = ifState.concat("\nD;JNE\n");
		try {
			asmFile.write(ifState);
			} catch (IOException e) {
				e.printStackTrace();
				}
		System.out.println(ifState);
		}
	
	public void wCall (String functionName, int numArgs) {
		String call = "";
		String label = "RETURN_LABEL" + numArgs;
		numArgs++;call = call.concat("@");
		call = call.concat(label);
		call = call.concat("\n");
		call = call.concat("D=A\n");
		call = call.concat("@SP\n");
		call = call.concat("A=M\n");
		call = call.concat("M=D\n");
		call = call.concat("@SP\n");
		call = call.concat("M=M+1\n");
		call = call.concat(pushCode2("LCL"));
		call = call.concat(pushCode2("ARG"));
		call = call.concat(pushCode2("THIS"));
		call = call.concat(pushCode2("THAT"));
		call = call.concat("@SP\n");
		call = call.concat("D=M\n");
		call = call.concat("@5\n");
		call = call.concat("D=D-A\n");
		call = call.concat("@" + numArgs + "\n");
		call = call.concat("D=D-A\n");
		call = call.concat("@ARG\n");
		call = call.concat("M=D\n");
		call = call.concat("@SP\n");
		call = call.concat("D=M\n");
		call = call.concat("@LCL\n");
		call = call.concat("M=D\n");
		call = call.concat("@");
		call = call.concat(functionName);
		call = call.concat("\n0;JMP\n(");
		call = call.concat(label);
		call = call.concat(")\n");
		try {
			asmFile.write(call);
			} catch (IOException e) {
				e.printStackTrace();
				}
		System.out.println(call);
		}
	public void wReturn() {
		String Re = "";
		Re = Re.concat("@LCL\n");
		Re = Re.concat("D=M\n");
		Re = Re.concat("@FRAME\n");
		Re = Re.concat("M=D\n");
		Re = Re.concat("@5\n");
		Re = Re.concat("A=D-A\n");
		Re = Re.concat("D=M\n");
		Re = Re.concat("@RET\n");
		Re = Re.concat("M=D\n");
		Re = Re.concat(popCode1("ARG", 0));
		Re = Re.concat("@ARG\n");
		Re = Re.concat("D=M\n");
		Re = Re.concat("@SP\n");
		Re = Re.concat("M=D+1\n");
		Re = Re.concat("@FRAME\n");
		Re = Re.concat("D=M-1\n");
		Re = Re.concat("AM=D\n");
		Re = Re.concat("D=M\n");
		Re = Re.concat("@THAT\n");
		Re = Re.concat("M=D\n");
		Re = Re.concat("@FRAME\n");
		Re = Re.concat("D=M-1\n");
		Re = Re.concat("AM=D\n");
		Re = Re.concat("D=M\n");
		Re = Re.concat("@THIS\n");
		Re = Re.concat("M=D\n");
		Re = Re.concat("@FRAME\n");
		Re = Re.concat("D=M-1\n");
		Re = Re.concat("AM=D\n");
		Re = Re.concat("D=M\n");
		Re = Re.concat("@ARG\n");
		Re = Re.concat("M=D\n");
		Re = Re.concat("@FRAME\n");
		Re = Re.concat("D=M-1\n");
		Re = Re.concat("AM=D\n");
		Re = Re.concat("D=M\n");
		Re = Re.concat("@LCL\n");
		Re = Re.concat("M=D\n");
		Re = Re.concat("@RET\n");
		Re = Re.concat("A=M\n");
		Re = Re.concat("0;JMP\n");
		try {
			asmFile.write(Re);
			} catch (IOException e) {
				e.printStackTrace();
				}
		System.out.println(Re);
		}
	public void wFunction (String functionName, int numLocals) {
		String func = "";
		func = func.concat("(");
		func = func.concat(functionName);
		func = func.concat(")\n");
		try {
			asmFile.write(func);
			} catch (IOException e) {
				e.printStackTrace();
				}
		for (int x = 0; x < numLocals; x++){
			writePushPop("C_PUSH", "constant", 0);
			}
		System.out.println(func);
		}
	}