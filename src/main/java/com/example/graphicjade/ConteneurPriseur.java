package com.example.graphicjade;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
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


public class ConteneurPriseur extends Application {
    protected ComissairePriseur agentPriseur;
    protected ObservableList<String > observableListData;
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        startContainer();
        primaryStage.setTitle("ComissairePriseur");
        BorderPane BP=new BorderPane();

        observableListData=FXCollections.observableArrayList();

        Button tourner=new Button("Tourner");
        Button sceller=new Button("Sceller");

        ListView<String> listView=new ListView<>(observableListData);


        HBox hbox=new HBox();
        hbox.setSpacing(10);
        hbox.setPadding(new Insets(10));
        VBox vbox=new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));
        hbox.getChildren().addAll(tourner,sceller);
        vbox.getChildren().add(listView);

        BP.setTop(hbox);
        BP.setCenter(vbox);

        tourner.setOnAction(evt->{
            GuiEvent event= new GuiEvent(evt,1);
            agentPriseur.onGuiEvent(event);
        });
        sceller.setOnAction(evt->{
            GuiEvent event= new GuiEvent(evt,2);
            agentPriseur.onGuiEvent(event);
        });
        Scene scene=new Scene(BP,400,300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startContainer() {
        Runtime runtime = Runtime.instance();
        Profile profileImpl = new ProfileImpl();
        profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        AgentContainer container = runtime.createAgentContainer(profileImpl);
        try {
            AgentController agentController = container.createNewAgent("Priseur", "com.example.graphicjade.ComissairePriseur", new
                    Object[]{this});
            agentController.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void afficherMessages(ACLMessage aclMessage)
    {
        Platform.runLater(()->{ // Mise à jour de l'interface utilisateur
            observableListData.add(aclMessage.getSender().getName()+"=>"+aclMessage.getContent());})
        ;
    }
}
