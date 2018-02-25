package de.mwvb.teeuhr.service;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Bounds;
import javafx.stage.Stage;

public class Config {
	  
	public void saveWindowPosition(String name, Stage stage) {
		if (stage.isIconified()) { // x+y sind versaut
			return;
		}
		String data = stage.getX() + ";" + stage.getY();
		if (stage.isResizable()) {
			data += ";" + stage.getWidth() + ";" + stage.getHeight(); 
		}
		List<String> arr = new ArrayList<>();
		arr.add(data);
		new DAO().save("WindowPosition-" + name, arr);
	}

	public void loadWindowPosition(String name, Stage stage, Bounds allScreenBounds) {
		List<String> data = new DAO().load("WindowPosition-" + name);
		if (data.size() > 0 && data.get(0).contains(";")) {
			String w[] = data.get(0).split(";");
			stage.setX(Double.parseDouble(w[0]));
			stage.setY(Double.parseDouble(w[1]));
			if (stage.isResizable()) {
				stage.setWidth(Double.parseDouble(w[2]));
				stage.setHeight(Double.parseDouble(w[3]));
			}
		}
		if (allScreenBounds != null) {
			fensterSichtbarAufMonitor(stage, allScreenBounds);
		}
	}

	private void fensterSichtbarAufMonitor(Stage stage, Bounds allScreenBounds) {
		// Dies ist gerade wichtig beim Wechsel von 2-Monitor (Büro) auf 1-Monitor (HomeOffice/RemoteDesktop).
		double x = stage.getX();
		double y = stage.getY();
		double w = stage.getWidth();
		double h = stage.getHeight();
		if (x < allScreenBounds.getMinX()) {
			stage.setX(allScreenBounds.getMinX());
		}
		if (x + w > allScreenBounds.getMaxX()) {
			stage.setX(allScreenBounds.getMaxX() - w);
		}
		if (y < allScreenBounds.getMinY()) {
			stage.setY(allScreenBounds.getMinY());
		}
		if (y + h > allScreenBounds.getMaxY()) {
			stage.setY(allScreenBounds.getMaxY() - h);
		}
	}
}
