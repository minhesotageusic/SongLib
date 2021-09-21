/*
 * Creator: Yunhao Xu, yx245
 * 			Minhesota Geusic, mtg110 
 * */

package SongLib.app;

import SongLib.view.SongLibController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SongLib extends Application {
	
	private SongLibController controller;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/SongLib/view/SongLib.fxml"));
		Pane root = (Pane) loader.load();
		
		controller = loader.getController();
		controller.start(primaryStage);
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);		
		primaryStage.show();
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

	        @Override public void handle(final WindowEvent arg0) {
	            controller.stop();
	        }
	    });
	}

	public static void main(String[] args) {
		launch(args);
	}

}
