package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import jess.Fact;
import jess.JessException;
import jess.RU;
import jess.Rete;
import jess.Value;
import jess.swing.JTextAreaWriter;
import weka.core.Attribute;

/**
 * @author tii
 * 
 *         This class will handle all events in the rules window
 *
 */

public class FactsController extends VBox implements Initializable {
	Rete engine = Begin.getReteInstance();
	// @FXML
	// private Text txtStat1;
	// @FXML
	// private Text txtStat2;
	// @FXML
	// private Text txtStat3;
	// @FXML
	// private Text txtStat4;

	@FXML
	private TextArea txtRConsole;
	private JTextArea txtConsole = new JTextArea();
	JTextAreaWriter taw = new JTextAreaWriter(txtConsole);

	@FXML
	private Button btnSend;
	@FXML
	private TabPane tpAttrib;
	private ArrayList<ChoiceBox<String>> boxes;

	public void initialize(URL arg0, ResourceBundle arg1) {

		txtConsole.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				txtRConsole.setText(txtConsole.getText());
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				txtRConsole.setText(txtConsole.getText());
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				txtRConsole.setText(txtConsole.getText());
			}
		});

		try {
			
			
			System.out.println(	);
			engine.addOutputRouter("t", taw);
			engine.addOutputRouter("WSTDOUT", taw);
			engine.addOutputRouter("WSTERR", taw);
			engine.run();
			engine.watchAll();
		} catch (JessException e) {
			e.printStackTrace();
		}

		if (DataController.getRules() != null && DataController.getRules().size() > 0) {
			// step 1: get the number of attributes
			Enumeration<Attribute> atts = DataController.data.enumerateAttributes();
			int size = 0;
			while (atts.hasMoreElements()) {
				atts.nextElement();
				size++;
			}
			// step 2: create an arraylist of choiceboxes
			boxes = new ArrayList<ChoiceBox<String>>();
			// step 3: get enumeration of attributes
			atts = DataController.data.enumerateAttributes();

			int columnCount = 0;
			int rowCount = 0;
			if (size > 20) {
				System.out.println("Data contains more than 20 attributes");
			}
			int max_num_attributes = 20;
			int i = 0;
			int tpCount = 0;
			GridPane row = new GridPane();
			row.setHgap(10);
			row.setVgap(10);
			row.setPadding(new Insets(10,10,10,10));
			while (atts.hasMoreElements() && i < max_num_attributes) {
			//	System.out.println("tpCount: " + tpCount);
			//	System.out.println("row_count: " + rowCount);
				Attribute theatt = atts.nextElement();
				// create label with its name
				Label ltemp = new Label();
				ltemp.setText(theatt.name().replaceAll("[^\\w\\s]", "").trim());
				// add it to the gridpane
				row.add(ltemp, columnCount, rowCount, 1, 1);
				// create a drop down box
				ChoiceBox<String> ctemp = new ChoiceBox<String>();
				// give class for choicebox
				ctemp.setId("box");
				// add it to the gridpane
				row.add(ctemp, columnCount + 1, rowCount, 1, 1);

				ctemp.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>(){

					@Override
					public void changed(ObservableValue<? extends Number> obv, Number number, Number number2) {
						if(number2.intValue() != -1)
						{
							ctemp.setDisable(true);
						}
					}
				
					
					
				});
				
				
				// add possible items to the choice box
				Enumeration<Object> vals = theatt.enumerateValues();
				while (vals.hasMoreElements()) {
					ctemp.getItems().add(vals.nextElement().toString());
				}
				// add the box to the arraylist
				boxes.add(ctemp);
				//
				if (rowCount == 4 || i == size-1) {
					rowCount = 0;
					tpAttrib.getTabs().get(tpCount).setContent(row);
					tpCount++;
					row = new GridPane();
					row.setHgap(10);
					row.setVgap(10);
					row.setPadding(new Insets(10,10,10,10));


				} else {
					rowCount++;
				}
				i++;
			}

		} else {
			System.out.println("");
		}
	}

	public void sendFacts(ActionEvent event) throws Exception {
		Rete engine = Begin.getReteInstance();
		engine.watchAll();

		Iterator<ChoiceBox<String>> boxIter = boxes.listIterator();
		final Enumeration<Attribute> newatts = DataController.data.enumerateAttributes();


		while (boxIter.hasNext()) {
			ChoiceBox<String> tempBox = boxIter.next();
			Attribute tempAtt = newatts.nextElement();
			if (tempBox.getSelectionModel().getSelectedIndex() != -1) {
				String selItem = tempBox.getSelectionModel().getSelectedItem().toString();
				// very helpful to cut out all non alphanumeric
				// characters
				selItem = selItem.replaceAll("[^\\w\\s]", "").trim();
				String currAtt = tempAtt.name();
				currAtt = currAtt.replaceAll("[^\\w\\s]", "").trim();

				if (!selItem.isEmpty()) {

					try {
						Fact theFact = new Fact(currAtt, engine);
						theFact.setSlotValue("__data", new Value(selItem, RU.SYMBOL));
						engine.assertFact(theFact);
						engine.run();
					} catch (JessException e) {
						e.printStackTrace();
						// tt.setFill(Color.RED);
						// tt.setText("Error");

					}
				}
			}
		}

	}

}