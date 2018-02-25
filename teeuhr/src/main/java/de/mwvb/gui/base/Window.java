package de.mwvb.gui.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;

import com.sun.javafx.scene.control.skin.TextAreaSkin;

import de.mwvb.teeuhr.service.Config;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Basis-Fensterklasse
 * 
 * @param <CTR> Controllerklasse
 */
public abstract class Window<CTR> {
	protected CTR controller;
	public static boolean testmodus = false;
	
	public final void show(final Stage stage, boolean modal, final javafx.stage.Window owner) {
		try {
			stage.getIcons().add(new Image(getIcon()));
			Scene scene = new Scene(root());
			stage.setScene(scene);
			if (owner != null) {
				stage.initOwner(owner);
			}
			keyBindings(scene);
			initWindow(stage);
			new Config().loadWindowPosition(getName(), stage, computeAllScreenBounds());
			onCloseRequest(stage);
			if (modal) {
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.showAndWait();
			} else {
				stage.show();
				displayed();
			}
		} catch (Exception e) {
			Window.errorAlert(e);
		}
	}
	
	/** Liefert Stream des Fenstericons */
	protected abstract InputStream getIcon();

	protected void keyBindings(Scene scene) {
		// Template-Methode
	}

	protected void initWindow(Stage stage) {
		// Template-Methode
	}

	public Bounds computeAllScreenBounds() { // Quelle: http://stackoverflow.com/a/26204372
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		for (Screen screen : Screen.getScreens()) {
			Rectangle2D screenBounds = screen.getBounds();
			if (screenBounds.getMinX() < minX) {
				minX = screenBounds.getMinX();
			}
			if (screenBounds.getMinY() < minY) {
				minY = screenBounds.getMinY();
			}
			if (screenBounds.getMaxX() > maxX) {
				maxX = screenBounds.getMaxX();
			}
			if (screenBounds.getMaxY() > maxY) {
				maxY = screenBounds.getMaxY();
			}
		}
		return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
	}

	public static void disableTabKey(final TextArea textArea) {
		textArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode().equals(KeyCode.TAB)) {
				TextAreaSkin skin = (TextAreaSkin) textArea.getSkin();
				if (event.isShiftDown()) {
					skin.getBehavior().traversePrevious();
				} else {
					skin.getBehavior().traverseNext();
				}
				event.consume();
			}
		});
	}

	protected String getName() {
		return getClass().getSimpleName();
	}

	protected void displayed() {
		// Template Methode
	}
	
	private void onCloseRequest(final Stage stage) {
		stage.setOnCloseRequest(event -> {
			int mode = onClose();
			if (mode == 1) { // Fenster ganz normal schlieﬂen
				new Config().saveWindowPosition(getName(), stage);
			} else {
				event.consume(); // Fenster nicht schlieﬂen
				if (mode == 2) { // Fenster ausblenden
					stage.setIconified(true);
				}
			}
		});
	}

	/**
	 * @return 0: Fenster nicht schlieﬂen, 1: Fenster ganz normal schlieﬂen, 2: Fenster ausblenden
	 */
	protected int onClose() {
		return 1; // Schlieﬂen ok
	}
	
	protected Parent root() {
		try {
			FXMLLoader loader = new FXMLLoader(Charset.forName("windows-1252"));
			loader.setLocation(getClass().getResource(getClass().getSimpleName() + ".fxml"));
			controller = createController();
			loader.setController(controller);
			return loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected abstract CTR createController();
	
	public static void alert(String meldung) {
		if (testmodus) return;
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Meldung");
		alert.setHeaderText("");
		alert.setContentText(meldung);
		alert.showAndWait();
	}

	public static void topAlert(String meldung) {
		if (testmodus) return;
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Meldung");
		alert.setHeaderText("");
		alert.setContentText(meldung);
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.setAlwaysOnTop(true);
		stage.toFront();
		alert.showAndWait();
	}

	public static void errorAlert(Exception ex) {
		ex.printStackTrace();
		
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Entschuldigung, das h‰tte nicht passieren d¸rfen.");
		alert.setHeaderText("Die Stechuhr hat ein Problem festgestellt.");
		if (ex.getMessage() == null || ex.getMessage().isEmpty()) {
			alert.setContentText("Es ist ein " + ex.getClass().getSimpleName() + " Fehler aufgetreten.");
		} else {
			alert.setContentText(ex.getMessage());
		}
		
		// ausklappbarer Bereich mit Stacktrace
		Label label = new Label("Details:");
		TextArea textArea = new TextArea(getExceptionText(ex));
		textArea.setEditable(false);
		textArea.setWrapText(true);
		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);
		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);
		alert.getDialogPane().setExpandableContent(expContent);
		
		alert.showAndWait();
	}
	
	/**
	 * @param ex Fehlermeldung als Objekt
	 * @return Stacktrace als String
	 */
	public static String getExceptionText(Throwable ex) {
		StringWriter buffer = new StringWriter();
		PrintWriter printer = new PrintWriter(buffer);
		ex.printStackTrace(printer);
		return buffer.toString();
	}
}
