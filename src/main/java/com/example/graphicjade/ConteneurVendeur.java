package com.example.graphicjade;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class ConteneurVendeur extends Application {
    protected Vendeur Vendeur;
    protected ObservableList<String> observableListData;
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        startContainer();
        primaryStage.setTitle("Vendeur");
        BorderPane borderPane=new BorderPane();
        HBox hbox1=new HBox();
        hbox1.setPadding(new Insets(10));
        hbox1.setSpacing(10);
        Label label=new Label("Product name");
        TextField productName=new TextField();
        Button buttonOk=new Button("OK");
        hbox1.getChildren().addAll(label,productName,buttonOk);
        borderPane.setTop(hbox1);
        observableListData=FXCollections.observableArrayList();
        ListView<String> listView=new ListView<String>(observableListData);
        VBox vbox2=new VBox();
        vbox2.setPadding(new Insets(10));
        vbox2.setSpacing(10);
        vbox2.getChildren().addAll(listView);
        borderPane.setCenter(vbox2);
        Scene scene=new Scene(borderPane,400,300);
        primaryStage.setScene(scene);
        buttonOk.setOnAction(evt->{
            String prdName=productName.getText();
            GuiEvent guiEvent=new GuiEvent(this, 1);
            guiEvent.addParameter(prdName);
            Vendeur.onGuiEvent(guiEvent);
        });
        primaryStage.show();
    }

    private void startContainer() {
        Runtime runtime = Runtime.instance();
        Profile profileImpl = new ProfileImpl();
        profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        AgentContainer container = runtime.createAgentContainer(profileImpl);
        try {
            AgentController agentController = container.createNewAgent("Vendeur", "com.example.graphicjade.Vendeur", new
                    Object[]{this});
            agentController.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void afficherMessages(ACLMessage aclMessage)
    {Platform.runLater(()->{observableListData.add(aclMessage.getContent()+" reçu de la part de "+aclMessage.getSender().getName());});
    }
}