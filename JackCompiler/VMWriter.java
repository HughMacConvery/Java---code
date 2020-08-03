import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class VMWriter{

	  private FileWriter vmFile;
	  private int labels; 
	  public ArrayList<String> lastwritten;
	  


	    public VMWriter(String basename) {
		 try {
		    vmFile = new FileWriter(basename + ".vm");
		    this.lastwritten = new ArrayList<String>();
		    lastwritten.add("");
		    lastwritten.add("");
		    lastwritten.add("");
		 } catch (IOException e) {
		    e.printStackTrace();
		    System.exit(1);
		 }
		 labels =0;
		}
	    public void writeCode(String code) {
	    	System.out.println(code);
	    	lastwritten.remove(2);
	    	lastwritten.add(0,code);
	    	try {
	    	    vmFile.write(code);
	    	} catch (IOException e) {
	    	    e.printStackTrace();
	    	    System.exit(1);
	    	}
	    	
	        }
	   public void push(int index, Segment segment ) {
		   
		   switch (segment){
		   case STATIC:  writeCode("push static "  + index + "\n");
		   break;
		   case CONST: writeCode("push constant " + index + "\n");
		   break;
		   case ARG: writeCode("push argument " + index + "\n" );
		   break;
		   case LOCAL: writeCode("push local " + index + "\n" );
		   break;
		   case THIS: writeCode("push this " + index + "\n" );
		   break;
		   case THAT: writeCode("push that " + index + "\n" );
		   break;
		   case TEMP: writeCode("push temp " + index + "\n");
		   break;
		   case POINTER: writeCode("push pointer " + index + "\n" );
		   break;
		   	   
		   }
	   }
	   public void pop(int index, Segment segment) {
		   switch (segment){
		   case STATIC:  writeCode("pop static "  + index + "\n");
		   break;
		   case CONST: writeCode("pop constant " + index + "\n");
		   break;
		   case ARG: writeCode("pop argument " + index + "\n");
		   break;
		   case LOCAL: writeCode("pop local " + index + "\n");
		   break;
		   case THIS: writeCode("pop this " + index + "\n");
		   break;
		   case THAT: writeCode("pop that " + index + "\n");
		   break;
		   case TEMP: writeCode("pop temp " + index + "\n" );
		   break;
		   case POINTER: writeCode("pop pointer " + index + "\n" );
		   break;
		   	   
		   }
	   }
	   public void writeArithmetic(Command command) {
		   switch(command) {
		   case ADD: writeCode("add\n");
		   break;
		   case SUB: writeCode("sub\n");
		   break;
		   case NEG: writeCode("neg\n");
		   break;
		   case EQ: writeCode("eq\n");
		   break;
		   case GT: writeCode("gt\n");
		   break;
		   case LT: writeCode("lt\n");
		   break;
		   case AND: writeCode("and\n");
		   break;
		   case OR: writeCode("or\n");
		   break;
		   case NOT: writeCode("not\n");
		   break;
		   }
	   }
	   public void returnLabel(String label) {
		   writeCode("label " + label+ "\n");
		   labels++;
	   }
	   public void writeGoto(String label) {
		   writeCode("goto  " + label+ "\n");
	   }
	   public void writeIf(String label) {
		   writeCode("if-goto " + label+ "\n");
	   }
	   public void writeCall(String name, int nArgs) {
		   writeCode("call " + name + " "+ nArgs+ "\n");
	   }
	   public void writeFunction(String name, int nLocals) {
		   writeCode("function " + name + " "+  nLocals + "\n");
	   }
	   public void writeReturn() {
		   writeCode("return" + "\n");
	   }
	   public void close() {
			try {
			    vmFile.close();
			} catch (IOException e) {
			    e.printStackTrace();
			    System.exit(1);
			}
		    }
	   
	   }

