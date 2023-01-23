package com.example.zenussdbanking;

import com.example.zenussdbanking.CrudRepository.PersonalOperationRepository;
import com.example.zenussdbanking.CrudRepository.TransfertRepository;
import com.example.zenussdbanking.CrudRepository.UserRepository;
import com.example.zenussdbanking.models.Transfert;
import com.example.zenussdbanking.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import static org.aspectj.runtime.internal.Conversions.intValue;

@RestController
public class UssdMessage {

    private ConcurrentMap<String, Object> map = new ConcurrentReferenceHashMap<String, Object>();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PersonalOperationRepository personalOperationRepository;
    @Autowired
    private TransfertRepository transfertRepository;

    private final String[] menus = {"Bienvenue à AMH Bank\n\n1.Consulter votre solde\n2.Historique des 5 dernières transactions\n3.Transferer de l'argent\n------\n10.Exit",
                                    "Entrer votre code pin",
                                    "Numero de compte du benificaire",
                                    "Entrer le montant",
                                    "Raison du transfert",
                                    "Votre requete à été prise en compte",
                                    "entrée incorrecte\n------\n9.Home"};
    @RequestMapping(path="/ussdBanking")
    public Object onMessage(@RequestBody Map<String, String> ussdmessage) throws ParseException, JsonProcessingException {
        //the response
        Map<String, Object> answer = new HashMap<String, Object>();
        String address = null;
        String inputMessage = ussdmessage.get("message");
        Map<String, Object> tempMap = (Map<String, Object>) map.get("address");

        if (map.containsKey("address")){
            int selected =  (Integer) tempMap.get("selected");
            int level =  (Integer) tempMap.get("level");

            if ("1".equals(inputMessage)){
                tempMap.put("selected", 1);
                tempMap.put("level", 1);
                answer.put("menu", menus[1]);
                answer.put("end", "0");
                return answer;
            } else if(selected == 1 && level == 1){
                if(userRepository.findByPin(inputMessage) == null){
                    answer.put("menu", "pin incorrect");;
                } else {
                    String solde = "Votre solde est de " +intValue((userRepository.findByPin(inputMessage))) + " FCFA";
                    answer.put("menu", solde);
                }
                answer.put("end", "1");
                tempMap.remove("address");
                map.remove("address");
                return answer;
            } else if ("2".equals(inputMessage) && level == 0){
                tempMap.put("selected", 2);
                tempMap.put("level", 1);
                answer.put("menu", menus[1]);
                answer.put("end", "0");
                return answer;
            } else if ( selected == 2 && level == 1){
                if(personalOperationRepository.findByPin(inputMessage) == null){
                    answer.put("menu", "pin incorrect");;
                } else {
                    ObjectMapper mapper = new ObjectMapper();
                    // convert user object to `JsonNode`
                    JsonNode node = mapper.convertValue(personalOperationRepository.findByPin(inputMessage), JsonNode.class);
                    String response ="";
                    for (int i=0; i<node.size(); i++){
                        JsonNode subNode = node.path(i);
                        String res= "";
                        for (int j=0; j<subNode.size(); j++){
                            if (j == 0) {
                                res = res + " " + subNode.path(j).intValue()+ " FCFA";
                            } else if (j == subNode.size()-1) {
                                String sub = String.valueOf(subNode.path(j));
                                res = res + " le " +  sub.substring(1, sub.length()-1);
                            } else {
                                String subDate = String.valueOf(subNode.path(j));
                                res = res + " " +  subDate.substring(1, subDate.length()-1);
                            }
                        }
                        response = response + "\n" + res;
                    }
                    answer.put("menu",  response);
                }
                answer.put("end", "1");
                tempMap.remove("address");
                map.remove("address");
                return answer;
            } else if("3".equals(inputMessage) && level == 0){ //transfert start
                tempMap.put("selected", 3);
                tempMap.put("level", 1);
                answer.put("menu", menus[2]);
                answer.put("end", "0");
                return answer;
            } else if(selected == 3 && level == 1){ //transfert account_to
                if (userRepository.findIdAndSoldeByAccount_number(inputMessage) == null){
                    answer.put("menu", "compte non-existant, essayer encore SVP");
                    tempMap.put("level", 1);
                } else {
                    tempMap.put("level", 2);
                    tempMap.put("account_no", inputMessage);
                    answer.put("menu", menus[3]);
                }
                tempMap.put("selected", 3);
                answer.put("end", "0");
                return answer;
            } else if(selected == 3 && level == 2){ //transfert amount
                try{
                    Double val = Double.parseDouble(inputMessage);
                    tempMap.put("selected", 3);
                    answer.put("end", "0");
                    if (val > 0){
                        tempMap.put("level", 3);
                        tempMap.put("amount", val);
                        answer.put("menu", menus[4]);
                    }else {
                        answer.put("menu", "Le montant doit etre strictement superieur à zéro, essayer encore SVP");
                        tempMap.put("level", 2);
                    }
                }catch (NumberFormatException ex){
                    answer.put("menu", "Type de valeur incorrect");
                    answer.put("end", "1");
                    tempMap.remove("address");
                    map.remove("address");
                }
                return answer;
            } else if(selected == 3 && level == 3) {//transfert reason
                tempMap.put("selected", 3);
                tempMap.put("level", 4);
                tempMap.put("reason", inputMessage);
                answer.put("menu", menus[1]);
                answer.put("end", "0");
                return answer;
            } else if (selected == 3 && level == 4) { //inserting a new Transfert, updating user sender and send_to
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node1 = mapper.convertValue(userRepository.findIdAndSoldeByPin(inputMessage),JsonNode.class);
                JsonNode node2 = mapper.convertValue( userRepository.findIdAndSoldeByAccount_number(tempMap.get("account_no").toString()), JsonNode.class);
                answer.put("end", "1");
                answer.put("menu", "Votre requete à étè traitée avec success");
                Transfert transfert = new Transfert(); //New instance of Transfert to insert
                    transfert.setMontant(Double.parseDouble(tempMap.get("amount").toString()));
                    transfert.setAccountTo(tempMap.get("account_no").toString());
                    transfert.setReason(tempMap.get("reason").toString());
                    transfert.setUserAccount_id(node1.path(0).intValue());
                    transfert.setCreatedAt(new Timestamp(Long.parseLong((Instant.now()).toString())));
                    transfert.setUpdatedAt(transfert.getCreatedAt());
                transfertRepository.save(transfert); //Inserting a new row

                User userSender = new User(); //updating sender amount
                int id = node1.path(0).intValue();
                double solde = ((double) (node1.path(1).intValue())) - (Double.parseDouble(tempMap.get("amount").toString()));
                userRepository.updateSender(id, solde);//end updating


                User userReceiver = new User();//updating receiver amount
                int idR = node2.path(0).intValue();
                double soldeR = ((double)(node2.path(1).intValue())) + (Double.parseDouble(tempMap.get("amount").toString()));
                userRepository.updateReceiver(idR, soldeR);

                tempMap.remove("address");
                map.remove("address");
                return answer;
            } else if ("10".equals(inputMessage)) {
                answer.put("menu", menus[5]);
                answer.put("end", "1");
                tempMap.remove("address");
                map.remove("address");
                return answer;
            } else if ("9".equals(inputMessage)) {
                answer.put("menu", menus[0]);
                answer.put("end", "0");
                map.put("address", new HashMap(){{
                    put("selected", 0);
                    put("level", 0);
                    put("end", "0");
                }});
                return answer;
            } else {
                answer.put("menu", menus[6]);
                answer.put("end", "0");
                return answer;
            }
        } else {
            map.put("address", new HashMap(){{
                put("selected", 0);
                put("level", 0);
                put("end", "0");
            }});
            answer.put("menu", menus[0]);
            answer.put("end", "0");
            return answer;
        }
    }
}
