package JInputLibrarySetup;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

public class SetupTheStuff {

	static final String LIB_PATH = "/JInputLibrarySetup/lib/";
	static final String SEP = System.getProperty("file.separator");
	static final String BIN_PATH = System.getProperty("java.home");

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				String[] msg = { "Files written successfully" };
				try {
					writeLibraries();
				} catch (Exception e) {
					msg = new String[] {
						"Operation failed:",
						"<tt>" + e.getMessage() + "</tt>",
						"",
						"Be sure to run this program as an administrator to " +
						"give it write permissions to your program files."
					};
				}
				System.out.println("\nEnjoy the dialog");
				show(msg);
			}
		});
	}

	public static void show(String... msg) {
		// try to set LaF
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e2) {
				// do nothing
		} //end System
		JFrame x = new JFrame();
		x.setLayout(new BorderLayout());
		x.setTitle("JInputLibrarySetup");
		x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		x.add(new JLabel("<html>" + 
				"<div style=\"padding: 13px; font-size: 10px;\">" +
				String.join("<br />", msg) +
				"<div></html>"), BorderLayout.NORTH);
		x.setMinimumSize(new Dimension(400, 200));
		x.setLocation(200, 200);
		x.setVisible(true);
	}

	public static void writeLibraries() throws Exception {
		FileOutputStream f;
		System.out.println("Writing files...");
		for (LibFile x : LibFile.values()) {
			try {
				String path = BIN_PATH +
						SEP +
						"bin" +
						SEP +
						x.name +
						"." +
						x.ext;
				System.out.print("\t" + path);
				f = new FileOutputStream(path);
				f.write(x.data);
				f.close();
				System.out.println(" Done");
			} catch (Exception e) {
				System.out.println(" <- problems");
				throw e;
			}
		}
	}

	static enum LibFile {
		DX8 ("jinput-dx8", "dll"),
		DX8_64 ("jinput-dx8_64", "dll"),
		RAW ("jinput-raw", "dll"),
		RAW_64 ("jinput-raw", "dll"),
		WINTAB ("jinput-wintab", "dll"),
		LINUX ("libjinput-linux", "so"),
		LINUX_64 ("libjinput-linux64", "so"),
		OSX ("libjinput-osx", "jnilib");

		final String name;
		final String ext;
		final byte[] data;
		LibFile(String name, String ext) throws ExceptionInInitializerError {
			this.name = name;
			this.ext = ext;
			ArrayList<Byte> temp = new ArrayList<Byte>();
			try {
				InputStream s = SetupTheStuff.class.getResourceAsStream(
							LIB_PATH + 
							this.name +
							"." +
							this.ext);
				
				int nextByte;
				do {
					nextByte = s.read();
					if (nextByte == -1) {
						continue;
					}
					byte next = (byte) nextByte;
					temp.add(next);
				} while (nextByte != -1);
				s.close();
			} catch (Exception e) {
				throw new ExceptionInInitializerError(
						"The enum " + this.name() + " couldn't be created.");
			}
			data = new byte[temp.size()];
			for (int i = 0; i < data.length; i++) {
				data[i] = temp.get(i);
			}
		}
	}
}