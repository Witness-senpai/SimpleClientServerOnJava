import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Server implements Runnable{
    private ServerSocket serverSocket;
    private Socket socket;
    private int port;
    private Data serverData;
    private DataInputStream in;
    private DataOutputStream out;

    public Server() {
        this.serverData = null;
        this.socket = null;
        this.port = 1234;
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("Cервер создан на: " + InetAddress.getByName("localhost") + ":" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadData(Data data){
        this.serverData = data;
    }

    public void run() {
        try {
            this.socket = serverSocket.accept(); //ждём, пока подключится клиент
            System.out.println("Соединение устанвлено! Client: " + socket.getInetAddress() + ":" + socket.getPort());
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            loadData(DataSafer.loadDataFromByte());
            //Если после загрузки данных данные не нулевые, значит произошла их загрузка
            if(serverData.getArraySize() != 0)
                out.writeUTF("данные с прошлого сеанса успешно загружены.\n");
            else
                out.writeUTF("test");

            while (true) {
                String inputInfo = in.readUTF();
                Thread.sleep(500);
                switch (inputInfo){
                    case "INPUT"://Получение нового символа
                        char inpurChar = in.readChar();
                        serverData.addArrayElement(inpurChar);
                        DataSafer.writeToTextLog(socket,"запись символа '" + String.valueOf(inpurChar) + "' в массив");
                        break;
                    case "CLEAR"://Очистить массив
                        serverData.clear();
                        out.writeUTF("CLEAR");
                        out.writeUTF("все данные были очищены.");
                        out.flush();
                        DataSafer.writeToTextLog(socket,"очищение всех данных");
                        break;
                    case "CONCAT"://Вернуть массив в виде строки
                        serverData.concat();
                        out.writeUTF("CONCAT");
                        out.writeUTF("все символы в массиве были конкатированы: \n" + serverData.getArrayInStr());
                        out.flush();
                        DataSafer.writeToTextLog(socket,"Конкатекация всех элементов массива в строку: " + serverData.getArrayInStr());
                        break;
                    case "PRINT"://Вернуть весь массив
                        out.writeUTF("PRINT");//Оповещение о печати
                        out.writeInt(serverData.getArraySize());//Сообщение о количестве символов
                        for (int i = 0; i < serverData.getArraySize(); i++) { //Отправка всех элементов клиенту
                            out.writeChar(serverData.getArrayElement(i));
                        }
                        out.flush();
                        DataSafer.writeToTextLog(socket,"запрос на печать всех символов в массиве");
                        break;
                    case "DELCLONES"://Удаление клонов
                        serverData.delClones();
                        out.writeUTF("DELCLONES");//Сообщение о команде
                        out.writeUTF("все дубликаты в массиве символов были удалены");//Сообщение о действии
                        DataSafer.writeToTextLog(socket,"удаление всех дубликатов в массиве символов");
                        break;
                    default:
                        System.out.println("Шок-событие");
                }
                DataSafer.saveDataInByte(serverData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}