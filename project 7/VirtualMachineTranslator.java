import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
public class VirtualMachineTranslator {
	public static void main(String[] args) {
		if (args.length != 0)   // Assume file/folder name passed on command line.
			new VirtualMachineTranslator().translate(args[0]);
		else {   // Pop-up a JFileChooser.
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					UIManager.put("swing.boldMetal", Boolean.FALSE);
					JFileChooser fc = new JFileChooser();
					fc.setFileFilter(new FileNameExtensionFilter("VM files", "vm"));
					fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
						new VirtualMachineTranslator().translate(fc.getSelectedFile().getPath());
					else
						System.out.println("No file selected; terminating.");
				}
			});
		}
	}
	private void translate(String path) {
		String basename;
		File folder = new File(path);
		ArrayList<File> files;
		if (folder.isFile()) {   // Folder is actually a file; translating a single
			// VM file.
			files = new ArrayList<File>();
			files.add(folder);
			basename = folder.getPath(); 
			basename = basename.substring(0, basename.indexOf('.'));
		}
		else {   // folder is a folder; translating a folder of VM files.
			files = new ArrayList<File>(Arrays.asList(folder.listFiles()));
			basename = folder.getPath() + "/" + folder.getName();
		}
		CodeWriter codeWriter = new CodeWriter(basename);
		// Iterate through all the VM files, or file, that we're translating.
		for (File file : files) {
			if (!file.getName().endsWith(".vm"))   // Skip non-VM files.
				continue;
			basename = file.getName(); 
			basename = basename.substring(0, basename.indexOf('.'));
			Parser parser = new Parser(file);
			codeWriter.setFileName(basename);
			while (parser.hasMoreCommands()) {
				parser.advance();
                //System.out.println("command type = "+parser.commandType());
				if (parser.commandType().equals("C_ARITHMETIC")) {
					codeWriter.writeArithmetic(parser.arg1());
				}else if (parser.commandType().equals("C_PUSH")||parser.commandType().equals("C_POP")) {
					codeWriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
				}else if (parser.commandType().equals("C_LABEL")) {
					codeWriter.wLabel(parser.arg1());
				}else if (parser.commandType().equals("C_GOTO")) {
					codeWriter.wGoto(parser.arg1());
				}else if (parser.commandType().equals("C_IF")) {
					codeWriter.wIf(parser.arg1());
				}else if (parser.commandType().equals("C_FUNCTION")) {
					codeWriter.wFunction(parser.arg1(), parser.arg2());
				}else if (parser.commandType().equals("C_RETURN")) {
					codeWriter.wReturn();
				}else if (parser.commandType().equals("C_CALL")) {
					codeWriter.wCall(parser.arg1(), parser.arg2());
				}
				
			}
		}
		codeWriter.close();
	}
}