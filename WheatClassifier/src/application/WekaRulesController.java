package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import jess.JessException;
import jess.Rete;
import jess.swing.JTextAreaWriter;
import weka.classifiers.rules.JRip.Antd;
import weka.classifiers.rules.JRip.RipperRule;

/**
 * @author tii
 * 
 *         This class will handle all events in the rules window
 *
 */

public class WekaRulesController extends VBox implements Initializable {
	Rete engine = Begin.getReteInstance();
	@FXML
	private Text txtStat1;
	@FXML
	private Text txtStat2;
	@FXML
	private Text txtStat3;
	@FXML
	private Text txtStat4;
	public static ArrayList<Integer> selected = new ArrayList<Integer>();
	@FXML
	private ListView<String> lvRules;
	@FXML
	private TextArea txtNewRule;

	@FXML
	private TextArea txtRConsole;
	private JTextArea txtConsole = new JTextArea();
	JTextAreaWriter taw = new JTextAreaWriter(txtConsole);	

	
	
	@FXML
	private Button btnSend;
	@FXML
	private TextField bulkURL;
	private File file;

	
	public ListView<String> getLvRules() {
		return lvRules;
	}

	public void setLvRules(ListView<String> lvRules) {
		this.lvRules = lvRules;
	}
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		txtConsole.getDocument().addDocumentListener(new DocumentListener(){
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
			engine.addOutputRouter("t", taw);
			engine.addOutputRouter("WSTDOUT", taw);
			engine.addOutputRouter("WSTERR", taw);
			engine.run();
			engine.watchAll();
		} catch (JessException e) {
			e.printStackTrace();
		}
		lvRules.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		lvRules.getItems().removeAll(lvRules.getItems());
		if (DataController.getRules() != null && DataController.getRules().size() > 0) {
			for (RipperRule r : DataController.getRules()) {
				lvRules.getItems().add(r.getAntds().toString() + " => " + DataController.data.classAttribute().name()
						+ " = " + DataController.data.classAttribute().value((int) r.getConsequent()));
			}
		} else {
			System.out.println("JRIP/Apriori rules not found");
		}
	}

	public void sendRules(ActionEvent event) throws Exception {
		selected.clear();
		Rete engine = Begin.getReteInstance();
		engine.watchAll();
		// get selected rules
		Iterator<Integer> iter = lvRules.getSelectionModel().getSelectedIndices().listIterator();
		int index;
		while (iter.hasNext()) {
			index = Integer.parseInt(iter.next().toString());
			selected.add(index);
		}
		if (DataController.getRules() != null && DataController.getRules().size() > 0) {
			int ruleCounter = 0;
			for (RipperRule r : DataController.getRules()) {
				String ruleString = "(defrule  rule" + ruleCounter + " ";
				if (r.hasAntds() == true) {
					Iterator<?> antIterator = r.getAntds().listIterator();
					while (antIterator.hasNext()) {
						Antd ant = (Antd) antIterator.next();
						String name = ant.getAttr().name();
						String value = DataController.data.attribute(name).value((int) ant.getAttrValue());
						ruleString += "(" + name.replaceAll("[^\\w\\s]", "").trim() + " "
								+ value.replaceAll("[^\\w\\s]", "").trim() + ") ";
					}
				}
				ruleString += " => ";
				ruleString += "(assert (DiseaseType ";
				// trim the disease type because some contain illegal characters
				String trimmed = DataController.data.classAttribute().value((int) r.getConsequent())
						.replaceAll("[^\\w\\s]", "").trim();
				ruleString += trimmed;
				ruleString += ")))";
				if (selected.contains(ruleCounter)) {
					engine.executeCommand(ruleString);
					// System.out.println(ruleString);
				}
				ruleCounter++;
			}

		}
		//txtStat1.setText("Successfully sent");

	}

	public void executeSingleRule(ActionEvent event) throws Exception {

		engine.executeCommand(txtNewRule.getText());

	}

	public void browseClick(ActionEvent event) throws Exception {
		try {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open JESS script");
		//	FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("JESS Script (*.clp) or (*.CLP)",
			//		"(*.clp)", "(*.CLP)");
			// fileChooser.getExtensionFilters().add(filter);
			file = fileChooser.showOpenDialog(Begin.theStage);
			bulkURL.setText(file.getAbsolutePath().toString());
		} catch (Exception e) {
			System.out.println("File operation error");
		}

	}

	public void executeBulkRules(ActionEvent event) throws Exception {

		engine.executeCommand(txtNewRule.getText());
		System.out.println(file.toURI().getPath());
		engine.batch(file.toURI().getPath());

	}
}