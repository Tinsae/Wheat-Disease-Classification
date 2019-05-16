package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Scanner;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import jess.JessException;
import jess.QueryResult;
import jess.Rete;
import jess.ValueVector;
import jess.swing.JTextAreaWriter;

/**
 * @author tii
 * 
 *         This class will handle all events in the rules window
 *
 */

public class ResultsController extends VBox implements Initializable {
	Rete engine = Begin.getReteInstance();
	
	File descFile;
	
	@FXML
	private TextArea txtRConsole;
	private JTextArea txtConsole = new JTextArea();
	JTextAreaWriter taw = new JTextAreaWriter(txtConsole);

	@FXML
	private TabPane tpResults;

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
			engine.addOutputRouter("t", taw);
			engine.addOutputRouter("WSTDOUT", taw);
			engine.addOutputRouter("WSTERR", taw);
			engine.run();
			engine.watchAll();
			// engine.reset();
		} catch (JessException e) {
			e.printStackTrace();
		}

		descFile= new File("C:/Description.txt");
		Scanner s=null;
		try {
			 s = new Scanner(descFile);
		} catch (FileNotFoundException e1) {
			System.out.println("put description file on C: drive");
			e1.printStackTrace();
		}
	
		
		
		
		if (DataController.getRules() != null && DataController.data != null) {
			Enumeration<Object> cvals = DataController.data.classAttribute().enumerateValues();
			String foo = "(defquery searchDisease (declare (variables ?dn ))";
			foo += " (DiseaseType ?dn)) ";
			try {
				engine.executeCommand(foo);
				int valueCounter=0;
				while (cvals.hasMoreElements()) {
					String val = cvals.nextElement().toString().replaceAll("[^\\w\\s]", "").trim();
					QueryResult result;
					result = engine.runQueryStar("searchDisease", new ValueVector().add(val));
					Integer i = 0;
					while (result.next()) {
						i++;
					}
					if (i > 0)
					{
						VBox v = new VBox();
						TextArea txt = new TextArea();
						txt.setFont(new Font("Garamond", 14));
						txt.setWrapText(true);
						switch(valueCounter)
						{
							case 0: 
							case 1:
							case 2:
							case 3:
							case 4:
							case 5: txt.setText(s.nextLine()); break;
							default: txt.setText("App supports up to 6 classes"); break;
							
						}
						
						if(valueCounter==0)
							txt.setText(s.nextLine());
						else if (valueCounter==1)
							txt.setText(s.nextLine());
						else if(valueCounter==2)
							txt.setText(s.nextLine());
						else if(valueCounter==3)
						{
							txt.setText(s.nextLine());
						}
						else
						{
							txt.setText("Unknown");
						}
						v.getChildren().add(txt);
						Tab pane = new Tab();
						pane.setContent(v);
						pane.setText(val);
						tpResults.getTabs().add(pane);
					
					}
				}
				
			} catch (JessException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("input data, generate rules and add rules to jess");
		}
		
		
	}

	

}