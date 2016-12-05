import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Created by ukevgen on 06.11.2016.
 */


public class Main {
    private static String answer = Const.ONE;

    public static void main(String[] args) {

        Client clientModel = new Client(Const.SERVER, Const.PORT);
        Scanner sc = new Scanner(System.in);
        boolean exit = false;

        display();
        while (!exit) {
            System.out.println("Select a command..");
            try {
                answer = sc.next();
            } catch (InputMismatchException e) {
                System.out.println("Incorrect type");
            }

            switch (answer) {
                case Const.ONE:
                    clientModel.start();
                    break;
                case Const.TWO:
                    clientModel.sendMessage(String.valueOf(Const.TWO));
                    System.out.println("get time and weather");
                    break;
                case Const.THREE:
                    clientModel.sendMessage(String.valueOf(Const.THREE));
                    System.out.println("get updates");
                    break;
                case Const.FOUR:
                    clientModel.alive();
                    break;
                case Const.FIVE:
                    clientModel.sendMessage(String.valueOf(Const.FIVE));
                    clientModel.disconnect();
                    break;
                case Const.SIX:
                    exit = true;
                    clientModel.disconnect();
                    break;
                default:
                    System.out.println("Not found command");
                    break;
            }
        }
        clientModel.disconnect();
    }

    public static void display() {
        System.out.println("*To start the Main in console mode use one of the number");
        System.out.println("1 - new connect");
        System.out.println("2 - get time and weather");
        System.out.println("3 - get updates");
        System.out.println("4 - ping server");
        System.out.println("5 - disconnect");
        System.out.println("6 - exit");
    }
}
