package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Scanner;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import jess.JessException;
import jess.Rete;
import jess.swing.JTextAreaWriter;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.JRip.RipperRule;
import weka.classifiers.rules.PART;
import weka.classifiers.rules.Rule;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;

/**
 * @author tii
 * 
 *         This class will handle all events in the Data Window
 *
 *
 *
 */

public class DataController extends VBox implements Initializable {
	Rete engine = Begin.getReteInstance();

	@FXML
	private Window beginstage;
	@FXML
	private MainController begincontroller;
	@FXML
	private TextField txtURL;
	@FXML
	private ComboBox<String> cmbAlg;
	@FXML
	private ImageView imgIndicator;
	@FXML
	private TitledPane titledStat;
	File file;
	File pfile;
	int whichAlg = -1;
	@FXML
	private VBox vboxBottom;
	@FXML
	private Text txtStat1;
	@FXML
	private Text txtStat2;
	@FXML
	private Text txtStat3;
	@FXML
	private Text txtStat4;

	@FXML
	private Spinner<Integer> spDisc;

	@FXML
	private Label lblInterval;
	@FXML
	private CheckBox chkNumeric;
	@FXML
	private ProgressIndicator progIndicator;
	
	@FXML
	private TextField txtpURL;
	
	@FXML
	private Button btnpBrowse;
	
	@FXML
	private TextArea txtRConsole;
	// This box will never be added
	private JTextArea txtConsole = new JTextArea();
	JTextAreaWriter taw = new JTextAreaWriter(txtConsole);	

	
	static Instances data;
	private static ArrayList<RipperRule> rules = new ArrayList<RipperRule>();

	public static ArrayList<RipperRule> getRules() {
		return rules;
	}

	public static void setRules(ArrayList<RipperRule> rules) {
		DataController.rules = rules;
	}

	@Override
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
			//engine.reset();
		} catch (JessException e) {
			e.printStackTrace();
		}
		 txtpURL.setVisible(false);
		 btnpBrowse.setVisible(false);
		
		// Initializing the combo box because it is not possible
		// to do it in the scene builder it self.
		cmbAlg.getItems().removeAll(cmbAlg.getItems());
		cmbAlg.getItems().addAll("...", "PART", "JRIP", "APRIORI");
		SpinnerValueFactory<Integer> valueFactory = //
				new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 4);
		spDisc.setValueFactory(valueFactory);
		progIndicator.setProgress(0);
		
		//Add event handler for txtURL
		txtURL.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
				if (file != null) {
				
					try {
						String address = file.getAbsolutePath().toString();
						DataSource source = new DataSource(address);
						data = source.getDataSet();
						// last attribute is the class index
						data.setClassIndex(data.numAttributes() - 1);
						txtStat1.setText("No of Instances: " + data.size());
						txtStat2.setText("No of Attributes: " + data.numAttributes());
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("File is not given !!");
				}	
			}
		});

		//Add event handler for chkAlg
		cmbAlg.valueProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {				
				if(cmbAlg.getSelectionModel().getSelectedItem().toString() == "PART")
				{
					txtpURL.setVisible(true);
					btnpBrowse.setVisible(true);
				}
				else
				{
					txtpURL.setVisible(false);
					btnpBrowse.setVisible(false);
				}
			}
			
		});

		
	}

	
	public void discretizeAttributes(ActionEvent event) throws IOException {
		if (chkNumeric.isSelected()) {
			if (file == null && data == null) {
				System.out.println("File is not given" + (file==null) + (data==null));

			} else {
				// the number of bins to use for discretization
				String bins = spDisc.getValue().toString();
				Enumeration<Attribute> atts = data.enumerateAttributes();
				int progress = 0;
				while (atts.hasMoreElements()) {
					Attribute a = atts.nextElement();
					if (a.isNumeric()) {
						// discretize
						// set options
						String[] options = new String[5];
						// choose the number of intervals, e.g. 2 :
						options[0] = "-B";
						options[1] = bins;
						// choose the range of attributes on which to apply the
						// filter:
						options[2] = "-R";
						// 0 is refered as first
						if (a.index() == 0) {
							options[3] = "first";
						}
						// last attribute leaving out the class index
						// is refereed as last
						else if (a.index() == data.numAttributes() - 1) {
							options[3] = "last";
						} else {
							options[3] = ((Integer) a.index()).toString();
						}
						options[4] = "-V";
						try {
							// Apply discretization:
							Discretize discretize = new Discretize();
							discretize.setOptions(options);
							discretize.setInputFormat(data);
							data = Filter.useFilter(data, discretize);
							System.out.println(a.name().replaceAll("[^\\w\\s]","").trim() + " is discretized to " + bins + " bins");
							txtStat3.setText("numeric attributes => discertized to " + bins + " bins");
						} catch (Exception e) {
							System.out.println("some problem occurred");
							e.printStackTrace();
						}
					}
					progress++;
					progIndicator.setProgress(progress * (1. / (data.numAttributes()-1)));
				}

			}
		}

	}

	public void btnpBrowse(ActionEvent event)
	{
		try {
		Stage dataStage = Begin.theStage;
		// Stage dataStage=new Stage();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open JESS Script");
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("JESS script (*.clp)",
				"*.clp");
		fileChooser.getExtensionFilters().add(filter);
		pfile = fileChooser.showOpenDialog(dataStage);
		txtpURL.setText(pfile.getName());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void dataClick(ActionEvent event) throws IOException {
		// change the scene

		Stage dataStage = Begin.theStage;
		// Stage dataStage=new Stage();
		dataStage.getIcons().add(new Image(getClass().getResourceAsStream("wheat.png")));
		Parent root = FXMLLoader.load(getClass().getResource("Data.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("Data.css").toExternalForm());
		dataStage.setScene(scene);
		if (file == null) {
			System.out.println("file is null");
		}
	}

	@FXML
	public void browseClick(ActionEvent event) {
		try {
			Stage dataStage = Begin.theStage;
			// Stage dataStage=new Stage();
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Data File");
			FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("DATA Files (*.csv), (*.arff)",
					"*.csv", "*.arff");
			fileChooser.getExtensionFilters().add(filter);
			file = fileChooser.showOpenDialog(dataStage);
			txtURL.setText(file.getAbsolutePath().toString());
			// txtURL.setText("C:\\Users\\tii\\Desktop\\other proj\\Wheat
			// 2014-16.csv.arff");
			if (file != null) {
				System.out.println("file is given");
			}
		} catch (Exception e) {
			System.out.println("file is null");

		}

	}

	@SuppressWarnings("deprecation")
	@FXML
	public void generateRules(ActionEvent event) {
		try {
			if (file != null) {
				// the PART algorithm
				if (cmbAlg.getSelectionModel().getSelectedIndex() == 1 && pfile!=null) {
					
					whichAlg=1;
					PART model = new PART();
					model.buildClassifier(data);
					System.out.println("Fixed Rules added to the working memory");
					//pfile = new File("src/application/PART_Rules.clp");
					engine.reset();
					engine.batch(pfile.toURL().getFile());
					engine.watchAll();
					// display statistics
					String alg = cmbAlg.getSelectionModel().getSelectedItem().toString();
					//count no of defrule occurances
					int drCount=0;
					Scanner input = new Scanner(pfile);
					while(input.hasNextLine())
					{
							if(input.nextLine().contains("defrule"))
							{
								drCount ++;
							}
						
					}
					input.close();
					
					txtStat4.setText(alg + " generated " + drCount + " rules ");

				}
				// the JRip algorithm
				else if (cmbAlg.getSelectionModel().getSelectedIndex() == 2) {
					whichAlg=2;
					JRip model = new JRip();
					model.buildClassifier(data);

					rules = new ArrayList<RipperRule>();
					for (Rule r : model.getRuleset()) {
						rules.add((RipperRule) r.copy());
					}

					// display statistics
					txtStat1.setText("No of Instances: " + data.size());
					txtStat2.setText("No of Attributes: " + data.numAttributes());
					String alg = cmbAlg.getSelectionModel().getSelectedItem().toString();
					txtStat4.setText(alg + " generated " + rules.size() + " rules ");

				} 
				else if (cmbAlg.getSelectionModel().getSelectedIndex() == 3){
					whichAlg=3;

				}
				else {
					System.out.println("please select an algorithm");
				}

			} else {
				System.out.println("file is empty");

			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

}
