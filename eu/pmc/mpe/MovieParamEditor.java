package eu.pmc.mpe;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.intarsys.tools.randomaccess.LERandomAccessByteArray;


public class MovieParamEditor {
	public MovieParamEditor(File f) {
		this.f = f;
	}

	private byte[] fwbytes;
	private Pattern p = null;

	public void open() throws IOException {
		RandomAccessFile raf = new RandomAccessFile(f, "r");
		fwbytes = new byte[(int) f.length()];
		raf.read(fwbytes);
		raf.close();

	}

	public int analyze() throws IOException {
		p = Pattern.compile(".{2}\\x00{2}.{2}\\x00{2}[^\\x00]{1}\\x00{3}\\x00.{2}\\x00.{1}\\x00{3}", Pattern.DOTALL);
		if (fwbytes == null)
			throw new UnsupportedOperationException();
		int count = 0;
		String binaryAsString = new String(fwbytes, "ISO-8859-1");
		LERandomAccessByteArray ra = new LERandomAccessByteArray(fwbytes);
		Matcher m = p.matcher(binaryAsString);
		while (m.find()) {
			int position = m.start();
			MovieParam mp = new MovieParam(position, ra);
			System.out.println(mp);
			if (mp.isValid()) {
				movieParams.add(mp);
				count++;
			}
		}
		return count;
	}
	
	public int apply() throws IOException {
		if(movieParams == null) {
			throw new UnsupportedOperationException();
		}
		RandomAccessFile raf = new RandomAccessFile(f, "rw");
		int count = 0;
		for(MovieParam mp : movieParams) {
			if(mp.isModified()) {
				raf.seek(mp.getAddress());
				System.out.println("Writing to " + String.format("%08x", mp.getAddress()));
				raf.write(mp.toBytes());
				count++;
			}
		}
		raf.close();
		return count;
	}

	private File f;

	private List<MovieParam> movieParams = new LinkedList<>();

	public List<MovieParam> getParams() {
		if (movieParams == null)
			throw new UnsupportedOperationException();
		return movieParams;
	}

}
