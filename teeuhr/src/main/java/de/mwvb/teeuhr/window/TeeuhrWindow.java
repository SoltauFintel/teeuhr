package de.mwvb.teeuhr.window;

import java.io.InputStream;

import de.mwvb.gui.base.Window;
import de.mwvb.teeuhr.TeeuhrApp;
import javafx.stage.Stage;

public class TeeuhrWindow extends Window<TeeuhrController> {
	
	@Override
	protected InputStream getIcon() {
		return getClass().getResourceAsStream(getClass().getSimpleName() + ".gif");
	}

	@Override
	protected TeeuhrController createController() {
		return new TeeuhrController();
	}
	
	@Override
	protected void initWindow(Stage stage) {
		stage.setTitle(TeeuhrApp.APP_NAME + " " + TeeuhrApp.APP_VERSION);
		stage.setWidth(415);
		stage.setHeight(454);
	}
	
	@Override
	protected int onClose() {
		controller.onClose();
		return super.onClose();
	}
}
