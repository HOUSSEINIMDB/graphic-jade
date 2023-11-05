package com.example.graphicjade;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Acheteur4 extends GuiAgent {
    protected ConteneurAcheteur4 conteneurAcheteur4;

    @Override
    protected void setup() {
        conteneurAcheteur4=(ConteneurAcheteur4)getArguments()[0];
        conteneurAcheteur4.acheteur=this;
        //Filtre des acheteurs aux enchères
        MessageTemplate mt =  MessageTemplate.or(
                MessageTemplate.MatchPerformative(ACLMessage.CFP),
                MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL)
        );
        addBehaviour(new OneShotBehaviour() {

            @Override
            public void action() {
                DFAgentDescription dfd=new DFAgentDescription();
                dfd.setName(getAID());

                //Declarer le produit qu'il peut vouloir acheter
                ServiceDescription sd=new ServiceDescription();
                sd.setName("encheres");
                sd.setType("Antiquite");
                dfd.addServices(sd);

                try {
                    DFService.register(myAgent,dfd);
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }
        });
        addBehaviour(new CyclicBehaviour() {

            @Override
            public void action() {


                ACLMessage msg=receive();
                if (msg!=null) {
                    if (msg.getPerformative() == ACLMessage.CFP) {
                        conteneurAcheteur4.afficherMessage(msg);
                        System.out.println("le prix à vaincre est: "+msg.getContent());
                        // pour repondre au message
                        ACLMessage rp=new ACLMessage(ACLMessage.PROPOSE);
                        rp.addReceiver(msg.getSender());
                        rp.setContent(String.valueOf((Double.parseDouble((msg.getContent())))+18));
                        send(rp);
                    }
                    if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                        // pour repondre au message
                        conteneurAcheteur4.afficherMessageConclu(msg);
                        ACLMessage rp=new ACLMessage(ACLMessage.AGREE);
                        rp.setContent("C'est d'accord Comissaire");
                        rp.addReceiver(msg.getSender());
                        send(rp);
                    }
                }
                else block();
            }
        });
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }
    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

}