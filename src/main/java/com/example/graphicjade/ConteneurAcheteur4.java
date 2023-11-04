package com.example.graphicjade;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConteneurAcheteur4 extends Application {
    protected Acheteur4 acheteur;
    protected ObservableList<String> observableList;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)  {
        startcontainer2();
        primaryStage.setTitle("Acheteur4");
        BorderPane BP=new BorderPane();
        Scene scene=new Scene(BP,400,300);
        observableList=FXCollections.observableArrayList();
        ListView<String> listView=new ListView<>(observableList);

        VBox vBox=new VBox();
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(10);
        vBox.getChildren().add(listView);
        BP.setCenter(vBox);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void startcontainer2() {
        try{

            Runtime runtime=Runtime.instance();
            ProfileImpl profileImpl=new ProfileImpl(false);
            profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
            AgentContainer agentContainer1=runtime.createAgentContainer(profileImpl);
            AgentController agentController1=agentContainer1.createNewAgent("Acheteur4","com.example.graphicjade.Acheteur4", new Object[]{this});
            agentController1.start();
        }catch(Exception e)
        {e.printStackTrace(); }
    }
    public void afficherMessage(ACLMessage mesg)
    {
        Platform.runLater(()->{
            observableList.add(mesg.getSender().getName()+" dit que "+mesg.getContent()+" est le prix à vaincre");
        });


    }
    public void afficherMessageConclu(ACLMessage mesg)
    {
        Platform.runLater(()->{
            observableList.add(mesg.getSender().getName()+" dit que "+mesg.getContent());
        });


    }
}