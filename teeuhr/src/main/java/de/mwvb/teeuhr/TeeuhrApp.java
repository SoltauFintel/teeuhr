package de.mwvb.teeuhr;

import de.mwvb.teeuhr.window.TeeuhrWindow;
import javafx.stage.Stage;

/**
 * Erinnerung auf Knopfdruck
 */
public class TeeuhrApp extends javafx.application.Application {
	public static final String APP_NAME = "Teeuhr";
	public static final String APP_VERSION = "0.1.0";

	public static void main(String[] args) {
		launch(TeeuhrApp.class, new String[] {});
	}

	@Override
	public void start(Stage stage) throws Exception {
		new TeeuhrWindow().show(stage, false, null);
	}
}
