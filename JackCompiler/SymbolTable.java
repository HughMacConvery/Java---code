import java.util.Hashtable;

public class SymbolTable {
	Hashtable<String, STEntry> method;
	Hashtable<String, STEntry> classes;

/* Add fields as needed */
	private int arg;
	private int var;
	private int field;
	private int stati;

	public SymbolTable(){
		/* COMPLETE CODE */
		method = new Hashtable<String,STEntry>();
		classes = new Hashtable<String,STEntry>();
		arg = 0;
		var = 0;
		field = 0;
		stati = 0;
	}

	public void startSubroutine(){
		/* COMPLETE CODE */
		method.clear();
		arg = 0;
		var = 0;

	}
	public void define(String name, String type, STKind kind){
		/* COMPLETE CODE */
		if(kind == STKind.ARG) {
			method.put(name, new STEntry(type,kind,arg));
			arg++;
		}
		else if(kind == STKind.VAR) {
			method.put(name,new STEntry(type,kind,var));
			var++;
		}
		else if(kind == STKind.FIELD) {
			classes.put(name,new STEntry(type,kind,field));
			field++;
		}
		else if(kind == STKind.STATIC) {
			classes.put(name,new STEntry(type,kind,stati));
			stati++;

		}

	}
	public int varCount(STKind kind){
		/* COMPLETE CODE */
		if(kind == STKind.STATIC) {
			return stati;
		}
		else if(kind == STKind.ARG) {
			return arg;
		}
		else if(kind == STKind.FIELD) {
			return field;
		}
		else {
			return var;
		}
	}
	public STKind kindOf(String name){
		/* COMPLETE CODE */
		if(classes.containsKey(name)) {
			return classes.get(name).getKind();

		} else if(method.containsKey(name)) {
			return method.get(name).getKind();
		}
		else {
		return null;

	    }

	}

	public String typeOf(String name){
		if(classes.containsKey(name)) {
			return classes.get(name).getType();

		}
		else if(method.containsKey(name)) {
			return method.get(name).getType();

		} else {
		/* COMPLETE CODE */

		return "";
		}
	}

	public int indexOf(String name){
		/* COMPLETE CODE */
		if(classes.containsKey(name)) {
			return classes.get(name).getIndex();

		}
		else if(method.containsKey(name)) {
			return method.get(name).getIndex();

		} else {
		return -1;
	    }

	}

}



