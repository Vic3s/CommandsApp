package com.application;

import java.util.Scanner;

class MainProgram{
    boolean exit = false;

    public String commandsList(){
        String listOfCommands = "simple file-manipulate - (mode), simple Search";
        return  listOfCommands;
    }

    public void Run(){

        User currUser = new User(11111111, "", "");

        while(!exit){
            Scanner inputUser = new Scanner(System.in);

            if (currUser.isUserNull()){
                System.out.println("Login or Create an account to start(Enter: create acc/login)");
            }else {
                System.out.println("Do Stuff!(Enter - !list, for more info)");
            }

            switch (inputUser.nextLine()){
                case "create acc":
                    AccountManager.CreateAccount();
                break;
                case "login":
                    AccountManager.Login(currUser);
                break;
                case "logout":
                    AccountManager.Logout(currUser);
                break;
                case "update acc":
                    AccountManager.UpdateAccount(currUser);
                break;
                case "print acc":
                    AccountManager.PrintAccount(currUser);
                break;
                case "!help":

                break;
                case "exit":
                    exit = true;
                break;
            }
        }
    }
}

public class App
{
    public static void main( String[] args )
    {
        MainProgram mainProgram = new MainProgram();
        mainProgram.Run();
    }
}
