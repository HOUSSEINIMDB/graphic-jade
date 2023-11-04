package com.example.graphicjade;



import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class Vendeur extends GuiAgent{
    protected ConteneurVendeur conteneurVendeur;

    MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
    @Override
    protected void setup() {
        conteneurVendeur=(ConteneurVendeur)getArguments()[0];
        conteneurVendeur.Vendeur=this;
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage reply1;
                reply1=receive(mt);
                if (reply1!=null) {
                    conteneurVendeur.afficherMessages(reply1);
                }
                else
                    block();
            }
        });
    }
    @Override
    protected void onGuiEvent(GuiEvent evt) {
        if (evt.getType()==1)
        {
            String productName=(String) evt.getParameter(0);
            ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
            message.addReceiver(new AID("Priseur",AID.ISLOCALNAME));
            message.setContent(productName);
            send(message);
        }
    }
}
