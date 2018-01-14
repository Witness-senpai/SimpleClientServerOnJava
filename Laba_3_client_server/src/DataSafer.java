import java.io.*;
import java.net.Socket;
import java.util.Date;

public class DataSafer {

    private static double connectionTime = 0;
    
    public static void writeToTextLog(Socket socket,String action) throws IOException {
        Date date = new Date();
        String strOut = " ";
        if (action.equals("===Соединение сервер-клиент установлено==="))
            connectionTime = date.getTime(); //Время подлючения в секундах UNIX-времени
        else if (action.equals("===Соединение сервер-клиент прервано===")) {
            connectionTime = (date.getTime() - connectionTime) / (double) 1000; //Время подключения в секундах
            strOut = "[" + String.valueOf(date.toInstant()) + "]:"+ socket.getInetAddress() + ":" + socket.getPort() + " => " +
                    action + "[Время подключения: " + connectionTime + " сек]";
        }
        else
            strOut = "[" + String.valueOf(date.toInstant()) + "]:"+ socket.getInetAddress() + ":" + socket.getPort() + " => " + action;
        FileWriter writer = new FileWriter("serverLog.txt", true);
        try {
            writer.write(strOut);
            writer.append("\r\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveDataInByte(Data currentData){
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream("savedServerData.out");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(currentData);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Data loadDataFromByte() {
        Data inputData = new Data();
        try {
            FileInputStream fis = new FileInputStream("savedServerData.out");
            ObjectInputStream oin = new ObjectInputStream(fis);
            inputData = (Data)oin.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return inputData;
    }
}
