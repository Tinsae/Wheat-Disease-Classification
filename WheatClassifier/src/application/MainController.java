package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import jess.Rete;

/**
 * @author tii
 * 
 *         This class will handle all events in the Main window
 *
 */

public class MainController extends VBox implements Initializable {
	
	@FXML
	private Button data;
	@FXML
	private Button rules;
	@FXML
	private Button facts;
	@FXML
	private Button result;
		
	
	Rete engine = Begin.getReteInstance();
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		

		
	}	
	
	public void dataClick(ActionEvent event) throws IOException {
		// change the scene
		Parent root = FXMLLoader.load(getClass().getResource("Data.fxml"));
		Scene scene = new Scene(root);
		//scene.getStylesheets().add(getClass().getResource("Data.css").toExternalForm());
		Begin.theStage.setScene(scene);

	}

	public void wekaRulesClick(ActionEvent event) throws IOException {
		// change the scene
		Parent root = FXMLLoader.load(getClass().getResource("WekaRules.fxml"));
		Scene scene = new Scene(root);
	//	scene.getStylesheets().add(getClass().getResource("wekarules.css").toExternalForm());
		Begin.theStage.setScene(scene);
	}

	public void factsClick(ActionEvent event) throws IOException {
		
		// change the scene
				Parent root = FXMLLoader.load(getClass().getResource("Facts.fxml"));
				Scene scene = new Scene(root);
			//	scene.getStylesheets().add(getClass().getResource("facts.css").toExternalForm());
				Begin.theStage.setScene(scene);

	}

	public void resultClick(ActionEvent event) throws IOException {
		// change the scene
		Parent root = FXMLLoader.load(getClass().getResource("Results.fxml"));
		Scene scene = new Scene(root);
		// scene.getStylesheets().add(getClass().getResource("result.css").toExternalForm());
		Begin.theStage.setScene(scene);
	}

	public void MouseEnter(MouseEvent event) throws IOException {
		Begin.theStage.getScene().setCursor(Cursor.HAND);
	}

	public void MouseExit(MouseEvent event) throws IOException {
		Begin.theStage.getScene().setCursor(Cursor.DEFAULT);
		((Button) event.getSource()).setOpacity(0.9);
		// ((Button) event.getSource()).setBlendMode(arg0);
	}

	

}
