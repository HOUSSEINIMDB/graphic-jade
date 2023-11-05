package com.example.graphicjade;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.List;

public class ComissairePriseur extends GuiAgent {
    protected ConteneurPriseur conteneurPriseur;//le conteneur de l'agent
    protected List<AID> acheteursAgents = new ArrayList<>();//les acheteurs aux enchères
    protected List<ACLMessage> receivedPrices = new ArrayList<>();//les prix reçus
    protected double currentmaxPrice = 0.0;//variable pour le prix du produit qui va monter

    private void setCurrentmaxPrice(double currentmaxPrice) {
        this.currentmaxPrice = currentmaxPrice;
    }

    protected String produitCherche;
    protected boolean aUnTour = true;// Ces 2 variables suffisent pour gerer les enchères à 1 tour
    int nbtour = 0;
    protected boolean finalisable = false;// Cette variable s'assure que le gagnat à dit AGREE


    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {
        if (guiEvent.getType() == 1) {
            // A chaque fois qu'on fait tourner on obtient un gagnant mais on ne
            // dit rien au vendeur d'enchère sauf si c'est un seul tour
            if(aUnTour && nbtour==0) {
                askPricesToSellers(currentmaxPrice);
                findMaxPriceandAcceptProposal();
                nbtour++;//on ne reéagit plus au clic 'tourner' // dans le cas des enchères à un seul tour
            }
            else if(!aUnTour) {
                askPricesToSellers(currentmaxPrice);
                findMaxPriceandAcceptProposal();
            }

        }
        if (guiEvent.getType() == 2) {
            // ici on scelle une enchère à plusieurs tours
            if(finalisable)
                sendMaxPriceToVendeur(currentmaxPrice);
        }
    }

    private void askPricesToSellers(double prixAVaincre) {
        ACLMessage request = new ACLMessage(ACLMessage.CFP);
        //Tour a tour on propose et les agents acheteurs aux enchères repondent chacun son tour
        for (AID sellerAgent : acheteursAgents) {
            request.addReceiver(sellerAgent);
            request.setContent(String.valueOf(prixAVaincre));
            send(request);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            request.clearAllReceiver();
        }
    }

    private double findMaxPriceandAcceptProposal() {
        double price ;
        ACLMessage maxPriceMessage = receivedPrices.get(0);
        System.out.println(maxPriceMessage.getContent() + "Is the content" + maxPriceMessage.getSender());
        for (ACLMessage message : receivedPrices) {
            price = Double.parseDouble(message.getContent());
            if (price > currentmaxPrice) {
                setCurrentmaxPrice(price);
                maxPriceMessage = message;
            }
        }
        //accept proposal du meilleur acheteur
        ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
        msg.addReceiver(maxPriceMessage.getSender());
        msg.setContent("Ton prix était choisi");
        send(msg);
        return currentmaxPrice;
    }

    private void sendMaxPriceToVendeur(double maxPrice) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("Vendeur", AID.ISLOCALNAME));
        msg.setContent(Double.toString(maxPrice));
        send(msg);
    }

    @Override
    protected void setup() {
        //Filtre pour le Commissaire priseur
        MessageTemplate mt = MessageTemplate.or(
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                MessageTemplate.or(
                        MessageTemplate.MatchPerformative(ACLMessage.AGREE),
                        MessageTemplate.MatchPerformative(ACLMessage.PROPOSE)
                )
        );
        conteneurPriseur = (ConteneurPriseur) getArguments()[0];
        conteneurPriseur.agentPriseur = this;

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive(mt);
                if (msg != null) {
                    conteneurPriseur.afficherMessages(msg);
                    //Message d'un Acheteur aux enchères
                    if (msg.getPerformative() == ACLMessage.PROPOSE) {
                        receivedPrices.add(msg);
                    }
                    //Message du chercher(Vendeur aux enchères)
                    if (msg.getPerformative() == ACLMessage.REQUEST) {
                        produitCherche = msg.getContent();
                    }
                    //Marché conclu
                    if (msg.getPerformative() == ACLMessage.AGREE) {
                        finalisable = true;
                    }
                } else {
                    block();
                }
            }
        });

        addBehaviour(new TickerBehaviour(this, 3000) {
            protected void onTick() {
                //Chercher et acceuillir d'autres acheteurs aux enchères - utile dans le
                //cas des enchères à plusieurs tours
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType(produitCherche);
                template.addServices(sd);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    //On clear car peut etre d'autres agents sont sortis du DF et sont
                    //toujours dans notre liste
                    acheteursAgents.clear();
                    for (DFAgentDescription sellerDesc : result) {
                        acheteursAgents.add(sellerDesc.getName());
                    }
                } catch (FIPAException fe) {
                    fe.printStackTrace();
                }
            }
        });
    }

}
