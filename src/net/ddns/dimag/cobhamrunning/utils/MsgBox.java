package net.ddns.dimag.cobhamrunning.utils;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.ddns.dimag.cobhamrunning.utils.SystemCommands.*;

public interface MsgBox  {
	Logger LOGGER = LogManager.getLogger(MsgBox.class.getName());

	Image favicon = new Image("file:src/resources/images/cobham_C_64x64.png");



	Pattern patternArt = Pattern.compile("([a-zA-Z0-9]+)", Pattern.CASE_INSENSITIVE);
	Pattern patternMac = Pattern.compile(MACADDRESS_PATTERN, Pattern.CASE_INSENSITIVE);
	Pattern patternMacInner = Pattern.compile(MACADDRESS_PATTERN_INNER, Pattern.CASE_INSENSITIVE);
	Pattern patternIp = Pattern.compile(IPADDRESS_PATTERN, Pattern.CASE_INSENSITIVE);
	Pattern patternIpInner = Pattern.compile(IPADDRESS_PATTERN_INNER, Pattern.CASE_INSENSITIVE);
	Pattern patternNetwork = Pattern.compile(NETWORK_PATTERN, Pattern.CASE_INSENSITIVE);
	Pattern patternSn = Pattern.compile("^[\\d]{8,8}$");

	static void msgInfo(String title, String header, String content) {
		Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(title);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(favicon);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
	}

	static void msgInfo(String title, String content) {
		Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(title);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(favicon);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
	}

	static void msgWarning(String title, String content) {
		Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle(title);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(favicon);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
	}

	static boolean msgConfirm(String title, String content) {
		CountDownLatch latch = new CountDownLatch(1);
		BooleanProperty resultProperty = new SimpleBooleanProperty();
		Platform.runLater(new Runnable() {
			@Override public void run() {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle(title);
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				stage.getIcons().add(favicon);
				alert.setHeaderText(null);
				alert.setContentText(content);
				Optional<ButtonType> result = alert.showAndWait();
				resultProperty.setValue(result.get() == ButtonType.OK);
				latch.countDown();
			}
		});
		try {
			latch.await();
		} catch (InterruptedException e) {
			LOGGER.error(e);
			msgException(e);
		}
		return resultProperty.getValue();
	}

	static boolean msgConfirm2(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle(title);
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(favicon);
		alert.setHeaderText(null);
		alert.setContentText(content);
		Optional<ButtonType> result = alert.showAndWait();
		return result.filter(buttonType -> (buttonType == ButtonType.OK)).isPresent();
	}

	static boolean msgCloseWindow(Stage stage, String title, String content){
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.getButtonTypes().remove(ButtonType.OK);
		alert.getButtonTypes().add(ButtonType.CANCEL);
		alert.getButtonTypes().add(ButtonType.YES);
		stage.getIcons().add(favicon);
		alert.setTitle(title);
		alert.setContentText(content);
		alert.initOwner(stage.getOwner());
		Optional<ButtonType> res = alert.showAndWait();
		return res.get().equals(ButtonType.CANCEL);
	}

	static void msgError(String title, String content) {
		Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(title);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(favicon);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
	}

	static void msgError(String title, String header, String content) {
		Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(title);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(favicon);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
	}

	static void msgException(Exception e) {
		Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            System.out.println(e.getLocalizedMessage());
            alert.setTitle("Exception");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(favicon);
            alert.setHeaderText(e.getMessage());
            alert.setContentText(e.toString());

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String exceptionText = sw.toString();

            Label label = new Label("The exception stacktrace was:");

            TextArea textArea = new TextArea(exceptionText);
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
        });
	}

	static void msgException(Throwable e) {
		Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            System.out.println(e.getLocalizedMessage());
            alert.setTitle("Exception");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(favicon);
            alert.setHeaderText(e.getMessage());
            alert.setContentText(e.toString());

            // Create expandable Exception.
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String exceptionText = sw.toString();

            Label label = new Label("The exception stacktrace was:");

            TextArea textArea = new TextArea(exceptionText);
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

            // Set expandable Exception into the dialog pane.
            alert.getDialogPane().setExpandableContent(expContent);

            alert.showAndWait();
        });
	}

	static void msgException(String s, RuntimeException e) {
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.ERROR);
			System.out.println(e.getLocalizedMessage());
			alert.setTitle("Exception");
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(favicon);
			alert.setHeaderText(e.getMessage());
			alert.setContentText(e.toString());

			// Create expandable Exception.
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String exceptionText = sw.toString();

			Label label = new Label("The exception stacktrace was:");

			TextArea textArea = new TextArea(exceptionText);
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

			// Set expandable Exception into the dialog pane.
			alert.getDialogPane().setExpandableContent(expContent);

			alert.showAndWait();
		});
	}

	static void msgException(String s, IOException e) {
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.ERROR);
			System.out.println(e.getLocalizedMessage());
			alert.setTitle("Exception");
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(favicon);
			alert.setHeaderText(e.getMessage());
			alert.setContentText(e.toString());

			// Create expandable Exception.
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String exceptionText = sw.toString();

			Label label = new Label("The exception stacktrace was:");

			TextArea textArea = new TextArea(exceptionText);
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

			// Set expandable Exception into the dialog pane.
			alert.getDialogPane().setExpandableContent(expContent);

			alert.showAndWait();
		});
	}

	static Integer msgInputInt(String content) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Input");
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(favicon);
		dialog.setHeaderText(null);
		dialog.setContentText(content);

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			try {
				return Integer.parseInt(result.get());
			} catch (Exception e) {
				msgWarning("Input warning", "Incorrect number");
				return null;
			}
		}
		return null;
	}

	static String msgInputString(String content) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Input");
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(favicon);
		dialog.setHeaderText(null);
		dialog.setContentText(content);

		Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

	static String msgIntutText(String content) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Input text");
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(favicon);
		alert.setHeaderText(content);

		TextArea textArea = new TextArea();
		textArea.setEditable(true);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(textArea, 0, 1);
		alert.getDialogPane().setContent(expContent);

		Optional<ButtonType> result = alert.showAndWait();

		if (result.isPresent()) {
			return textArea.getText();
		}
		return null;
	}

	static String msgInputSN() {
		String sn = msgInputString("Enter serial number");
		try {
			sn = sn.toUpperCase();
			if (!Pattern.matches("^(?=[0-9]*$)(?:.{8}|.{12})$", sn)) {
				msgError("Input error", "Incorrect serial number");
				return msgInputSN();
			}
		} catch (NullPointerException e){
			return null;
		}
		return sn;
	}

	static String msgInputIP() {
		String ip = msgInputString("Enter IP address");
		try {
			ip = ip.toUpperCase();
			Matcher matcherIp = patternIp.matcher(ip);
			if (!matcherIp.find()) {
				msgError("Input error", "Incorrect IP address");
				return msgInputIP();
			}
		} catch (NullPointerException e){
			return null;
		}
		return ip;
	}

	static List<String> msgScanSystemBarcode() {
		String val = msgInputString("Scan system barcode:");
		Matcher matcherArt;
		try {
			matcherArt = patternArt.matcher(val);
		}catch (NullPointerException e){
			return null;
		}

		List<String> tmp = new ArrayList<>();
		while (matcherArt.find())
			tmp.add(val.substring(matcherArt.start(), matcherArt.end()).toUpperCase());
		if (tmp.size() != 2) {
			msgError("Scan barcode", String.format("Incorrect barcode: %s", val));
			msgScanSystemBarcode();
		}
		return tmp;
	}

	static String msgScanMac() {
		String val = msgInputString("Scan system mac address:");
		Matcher matcherMac = patternMac.matcher(val);
		if (!matcherMac.find()) {
			msgError("Scan mac address", String.format("Incorrect mac address: %s", val));
			msgScanMac();
		}
		return val;
	}

	static String msgChoice(String header, String content, List<String> choices){
		ChoiceDialog<String> dialog = new ChoiceDialog<>(null, choices);
		dialog.setTitle("Choise");
		dialog.setHeaderText(header);
		dialog.setContentText(content);
		Optional<String> result = dialog.showAndWait();
		return result.orElse(null);
	}

}
