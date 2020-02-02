package net.ddns.dimag.cobhamrunning.utils;

import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import net.ddns.dimag.cobhamrunning.MainApp;

public class HtmlParser implements MsgBox{
	private WebClient client;
	private MainApp mainApp;
	
	private String addr = "http://192.168.1.253";
	
	public HtmlParser() {
		this.client = new WebClient();
		
		client.getOptions().setCssEnabled(true);
		client.getOptions().setJavaScriptEnabled(false);
	}
	
	public boolean login(){
		try {
			HtmlPage page = (HtmlPage) client.getPage(addr); 
            HtmlForm form = page.getHtmlElementById("loginForm"); 
            form.getInputByName("userid").setValueAttribute("deko"); 
            form.getInputByName("password").setValueAttribute("deko10"); //does not work 

            page = form.getInputByName("submit").click(); //works fine
            System.out.println(page.asText());
            startWeb(page);
            return true;
		} catch (Exception e) {
			MsgBox.msgException(e);
			return false;
		}
	}
	
    public void startWeb(HtmlPage page) {
    	Stage stage = new Stage();
        // Create the WebView
        WebView webView = new WebView();
         
        // Create the WebEngine
        final WebEngine webEngine = webView.getEngine();
 
        // LOad the Start-Page
//        webEngine.load(page.asText());
        webEngine.loadContent(page.getWebResponse().getContentAsString());
//         
//        // Update the stage title when a new web page title is available
//        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() 
//        {
//            public void changed(ObservableValue<? extends State> ov, State oldState, State newState) 
//            {
//                if (newState == State.SUCCEEDED) 
//                {
//                    //stage.setTitle(webEngine.getLocation());
//                    stage.setTitle(webEngine.getTitle());
//                }
//            }
//        });
 
        // Create the VBox
        VBox root = new VBox();
        // Add the WebView to the VBox
        root.getChildren().add(webView);
 
        // Set the Style-properties of the VBox
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");
 
        // Create the Scene
        Scene scene = new Scene(root);
        // Add  the Scene to the Stage
        stage.setScene(scene);
        // Display the Stage
        stage.show();
    }
	
	public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
