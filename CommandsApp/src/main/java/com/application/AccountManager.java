package com.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mindrot.jbcrypt.BCrypt;
import sun.jvm.hotspot.ui.tree.SimpleTreeGroupNode;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;
import java.util.Random;

public class AccountManager {
    public static File accountRecords = new File("./src/resources/accounts.json");

    public static void CreateAccount(){
        Scanner scanner = new Scanner(System.in);
        Random rand = new Random();
        int userId = rand.nextInt(1000000);

        // Title
        System.out.println("Creating Account: \n");

        // Created Name Input
        System.out.println("Enter the name for your account: ");
        String getName = scanner.nextLine();

        // Created Password Input
        System.out.println("Enter the password for your account: ");
        String hashedpass = BCrypt.hashpw(scanner.nextLine(), BCrypt.gensalt());

        // Create file and object mapper to write to json records
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode;

        try{
            if(accountRecords.exists() && accountRecords.length() > 0){
                JsonNode root = mapper.readTree(accountRecords);
                if(root.isArray()){
                    arrayNode = (ArrayNode) root;
                }else{
                    arrayNode = mapper.createArrayNode();
                    arrayNode.add(root);
                }
            }else{
                arrayNode = mapper.createArrayNode();
            }
            ObjectNode newEntry = mapper.createObjectNode();
            newEntry.put("id", userId);
            newEntry.put("name", getName);
            newEntry.put("password", hashedpass);
            arrayNode.add(newEntry);

            mapper.writerWithDefaultPrettyPrinter().writeValue(accountRecords, arrayNode);

        }catch(IOException e){
            System.err.println(String.format("There was an IO error: %s", e));
        }

        System.out.println("Your account has been created!");
    }
    public static void Logout(User user){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Are you sure you want to delete your account?(y/n): ");
        String logoutDialog = scanner.nextLine();

        if(logoutDialog.equalsIgnoreCase("y")){
            user.username = "";
            user.password = "";
            user.age = -1;
        }else if(logoutDialog.equalsIgnoreCase("n")){
            System.out.println("Action aborted...");
        }
    }
    public static void UpdateSpecs(User user, String Dialog, String spec) {
        Scanner scanner = new Scanner(System.in);

        if(Dialog.equalsIgnoreCase("y")){
            System.out.println(String.format("Input new %s: ", spec));
            Object newSpec = scanner.nextLine();

            ObjectMapper mapper = new ObjectMapper();
            ArrayNode arrayNode;

            try{
                //Get the records as an array object
                arrayNode = (ArrayNode) mapper.readTree(accountRecords);

                // Create empty new updated record(Object)
                ObjectNode updatedRecord = mapper.createObjectNode();

                for(int i=0; i< arrayNode.size(); i++){
                    JsonNode node = arrayNode.get(i);

                    // Check if any records in the json accountRecords match the current users logged-in username
                    if(node.get("id").asInt() == user.id){

                        //Fill the updated record with updated data
                        if(spec.equals("name")){
                            updatedRecord.put("id", node.get("id"));
                            updatedRecord.put("name", (String) newSpec);
                            updatedRecord.put("password", node.get("password"));
                            arrayNode.add(updatedRecord);
                        }else if(spec.equals("password")){
                            String hashedNewPass = BCrypt.hashpw((String) newSpec, BCrypt.gensalt());
                            updatedRecord.put("id", node.get("id"));
                            updatedRecord.put("name", node.get("name"));
                            updatedRecord.put("password", hashedNewPass);
                            arrayNode.add(updatedRecord);
                        }else if(spec.equals("age")){
                            updatedRecord.put("id", node.get("id"));
                            updatedRecord.put("name", node.get("name"));
                            updatedRecord.put("password", node.get("password"));
                            updatedRecord.put("age", Integer.parseInt((String) newSpec));
                            arrayNode.add(updatedRecord);
                        }

                        //remove the old record
                        arrayNode.remove(i);

                        //write it back to the accounts json file
                        mapper.writerWithDefaultPrettyPrinter().writeValue(accountRecords, arrayNode);
                    }
                }
            }catch(IOException e) {
                System.err.println(String.format("There was an IO Error: %s", e));
            }
            // Update current user
            if(spec.equals("name")){
                user.username = (String) newSpec;
            }else if(spec.equals("password")){
                user.password = (String) newSpec;
            }else if(spec.equals("age")){
                user.age = Integer.parseInt((String) newSpec);
            }

        }else{
            System.out.println("Action aborted...");
        }
    }

    public static void UpdateAccount(User user){


        if(user.isUserNull()){

            //Is Logged In
            System.out.println("You need to log in(login acc) first...");
        }else {
            Scanner scanner = new Scanner(System.in);

            //Change username functionality
            System.out.println("Update username?(y/n)");
            String updateUsernameDialog = scanner.nextLine();

            UpdateSpecs(user, updateUsernameDialog, "name");

            //Change password functionality
            System.out.println("Update password?(y/n)");
            String updatePasswordDialog = scanner.nextLine();

            UpdateSpecs(user, updatePasswordDialog, "password");

            //Change age functionality
            System.out.println("Update age?(y/n)");
            String updateAgeDialog = scanner.nextLine();

            UpdateSpecs(user, updateAgeDialog, "age");
        }
    }

    public static void PrintAccount(User user){
        System.out.println(String.format("AccountName: %s\nAge: %s", user.username, user.age));
    }
    public static void Login(User user) {

        // Title
        System.out.print("Log In: \n");
        Scanner scanner = new Scanner(System.in);

        // Get Input Name From User
        System.out.println("Name: ");
        String getInputName = scanner.nextLine();

        // Get Input Password From User
        System.out.println("Password: ");
        String getInputPass = scanner.nextLine();


        // Create the object mapper for reading the existing accounts in the json accountRecords
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = null;

        // Read the json accountRecords and transform the content to an array to make it iterable
        try {
            arrayNode = (ArrayNode) mapper.readTree(accountRecords);
        } catch (IOException e) {
            System.err.print(String.format("There was an IO Error: %s", e));
        }

        // Check for existence of user in json records
        for (JsonNode node : arrayNode) {

            if (getInputName.equals(node.get("name").asText()) && BCrypt.checkpw(getInputPass, node.get("password").asText())) {
                user.id = node.get("id").asInt();
                user.username = node.get("name").asText();
                user.password = node.get("password").asText();
                break;
            }
        }

        // Return appropriate response to the result
        if (user.isUserNull()) {
            System.err.println("Incorrect user name or password, try again.");
        }else{
            System.out.println("Log in successful!");
        }
    }
}
