package de.mwvb.teeuhr.window;

import java.util.ArrayList;
import java.util.List;

import de.mwvb.gui.base.Window;
import de.mwvb.teeuhr.model.Dauer;
import de.mwvb.teeuhr.service.Config;
import de.mwvb.teeuhr.service.DAO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TeeuhrController {
	@FXML
	private TextField zeit1;
	@FXML
	private TextField zeit2;
	@FXML
	private TextField zeit3;
	@FXML
	private TextField zeit4;
	@FXML
	private Label info;

	private static final String KAA = "keine Aktion aktiv";
	private Thread timer;

	@FXML
	protected void initialize() {
		try {
			List<String> list = new DAO().load("dauer");
			if (list.size() >= 4) {
				zeit1.setText(list.get(0));
				zeit2.setText(list.get(1));
				zeit3.setText(list.get(2));
				zeit4.setText(list.get(3));
			}
		} catch (Exception e) {
			Window.errorAlert(e);
		}
	}

	@FXML
	public void onEvent1() {
		event(1);
	}

	@FXML
	public void onEvent2() {
		event(2);
	}

	@FXML
	public void onEvent3() {
		event(3);
	}

	@FXML
	public void onEvent4() {
		event(4);
	}

	@FXML
	public void onCancel() {
		cancel();
	}

	@FXML
	public void onSave() {
		List<String> list = new ArrayList<>();
		list.add(zeit1.getText());
		list.add(zeit2.getText());
		list.add(zeit3.getText());
		list.add(zeit4.getText());
		new DAO().save("dauer", list);
		System.out.println("saved");
	}

	private void event(int nr) {
		Dauer d = getDauer(nr);
		if (d.isLeer()) {
			Window.alert("Bitte geben Sie erst eine Dauer ein!");
			return;
		}
		
		cancel(); // etwaige Aktion abbrechen
		
		timer = new Thread() {
		    @Override
			public void run() {
		    	final long ziel = System.currentTimeMillis() + d.getMillis();
		    	while (System.currentTimeMillis() < ziel) {
		    		try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						break;
					}
		    	}
		    	if (System.currentTimeMillis() >= ziel) {
			        Platform.runLater(() -> {
		            	String topic = getTopic(nr);
		            	System.out.println(topic);
		                info.setText(topic);
		                Window.topAlert(topic);
		                info.setText(KAA);
			        });
		    	} else {
		    		System.out.println("Timer stopped");
		    	}
		    }
		};
		String topic = "aktiv: " + getAktion(nr);
		System.out.println(topic);
		info.setText(topic);
		timer.start();
	}

	private void cancel() {
		info.setText(KAA);
		if (timer != null && timer.isAlive()) {
			System.out.println("trying to stop timer...");
			timer.interrupt();
		}
		timer = null;
	}
	
	private Dauer getDauer(int nr) {
		TextField tf = null;
		if (nr == 1) tf = zeit1;
		else if (nr == 2) tf = zeit2;
		else if (nr == 3) tf = zeit3;
		else if (nr == 4) tf = zeit4;
		else throw new RuntimeException();
		return new Dauer(tf.getText());
	}
	
	private String getTopic(int nr) {
		switch (nr) {
		case 1: return "Wasser kocht!";
		case 2: return "Tee ziehen fertig!";
		case 3: return "Tee ist trinkfertig!";
		case 4: return "Sonderaktion";
		default: return "Aktion " + nr;
		}
	}

	private String getAktion(int nr) {
		switch (nr) {
		case 1: return "Warten auf kochendes Wasser...";
		case 2: return "Tee ziehen lassen...";
		case 3: return "Warten auf Trinktemperatur...";
		case 4: return "Auf Sonderaktion warten...";
		default: return "Warten auf Aktion " + nr;
		}
	}

	private Stage getStage() {
		return (Stage) zeit1.getScene().getWindow();
	}

	public void onClose() {
		System.out.println("on close");
		cancel();
		new Config().saveWindowPosition(TeeuhrWindow.class.getSimpleName(), getStage());
	}
}
