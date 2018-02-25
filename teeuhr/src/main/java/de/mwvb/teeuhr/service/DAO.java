package de.mwvb.teeuhr.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DAO {
	private static String pfad;
	
	static {
		pfad = System.getProperty("user.home").replace("\\", "/") + "/teeuhr";
		File file = new File(pfad);
		if (!file.isDirectory() && !file.mkdirs()) {
			throw new RuntimeException("Fehler bei init(). Verzeichnis konnte nicht erstellt werden: " + file.getAbsolutePath());
		}
	}

	public void save(String id, List<String> data) {
		if (id == null || id.trim().isEmpty() || data == null) {
			throw new IllegalArgumentException("Bitte id angeben!");
		}
		try {
			FileWriter w = new FileWriter(getFile(id));
			try {
				for (String line : data) {
					w.write(line + "\r\n");
				}
			} finally {
				w.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<String> load(String id) {
		if (id == null || id.trim().isEmpty()) {
			throw new IllegalArgumentException("Bitte id angeben!");
		}
		List<String> ret = new ArrayList<>();
		try {
			File file = getFile(id);
			if (!file.exists()) {
				return ret;
			}
			BufferedReader r = new BufferedReader(new FileReader(file));
			try {
				String line;
				while ((line = r.readLine()) != null) {
					ret.add(line);
				}
				return ret;
			} finally {
				r.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private File getFile(String id) {
		return new File(pfad.toString() + "/" + id + ".txt");
	}
}
