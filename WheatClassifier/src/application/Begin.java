package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jess.JessException;
import jess.Rete;

public class Begin extends Application {
	// make the stage global because we need to change scenes in the controller
	public static Stage theStage;
	
	private static Rete engine = null;

	public static Rete getReteInstance()  {
		if (engine == null)
		{
			engine = new Rete();
			try {
				engine.reset();
			} catch (JessException e1) {
				e1.printStackTrace();
			}
			try {
				engine.run();
			} catch (JessException e) {
				e.printStackTrace();
			}
		}

		return engine;
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			theStage = primaryStage;
			primaryStage.getIcons().add(new Image(getClass().getResource("wheat.png").toExternalForm()));
			Parent root=FXMLLoader.load(getClass().getResource("Main.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("WDC");
			primaryStage.setMaxWidth(1200);
			primaryStage.setMaxHeight(600);

			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
