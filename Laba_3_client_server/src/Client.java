import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable{
    private InetAddress host;
    private int port;
    private Socket socket;
    private Controller controller;
    public boolean isConnection;
    private DataInputStream in;
    private DataOutputStream out;

    public Client(Controller controller){
        this.isConnection = false;
        this.controller = controller;
        this.in = null;
        this.out = null;
        this.port = Integer.parseInt(controller.textPort.getText());
        try {
            this.host = InetAddress.getByName(controller.textHost.getText());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public boolean findServer() throws IOException{
        this.host = InetAddress.getByName(controller.textField.getText());
        this.port = Integer.parseInt(controller.textPort.getText());
        this.socket = new Socket(host, port);
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        if (this.socket.isConnected()) {
            controller.writeToLog("Клиент законнектился с сервером!\nHOST -> " + host + ":" + port + "\n");
            this.isConnection = true;
            return true;
        }
        else
            controller.writeToLog("Ошибка подключения!\nHOST -> " + host + ":" + port);
        return false;
    }

    public void run() {
        String inputStr1 = null;//Получение заднных о загрузке старых данных с сервера
        try {
            inputStr1 = in.readUTF();
            if (!inputStr1.equals("test"))
                controller.writeToLog("Cервер: " + inputStr1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true){
            String inputStr = null;
            try {
                inputStr = in.readUTF();//Получение информации, что за сигнла-ответ
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(!inputStr.equals(null)) {
                controller.writeToLog("Сервер: ");
                switch (inputStr) {
                    case "CLEAR":
                    case "CONCAT":
                    case "DELCLONES":
                        try {
                            controller.writeToLog(in.readUTF() + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "PRINT":
                        try {
                            int arraySize = in.readInt();
                            if(arraySize != 0) {
                                controller.writeToLog("[");
                                for (int i = 0; i < arraySize; i++) {
                                    controller.writeToLog(in.readChar() + ((i != arraySize - 1) ? ", ":"]\n"));
                                }
                            }
                            else
                                controller.writeToLog("Массив символов пуст!\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        controller.writeToLog("\nШОК-КОМАНДА!");
                }
            }
            else
                controller.writeToLog("Не удалось считать данные! Ошибка сети.\n");
        }
    }

    public DataOutputStream getOut() {
        return out;
    }

    public DataInputStream getIn() {
        return in;
    }

    public void finalize() throws IOException {
        socket.close();
    }
}