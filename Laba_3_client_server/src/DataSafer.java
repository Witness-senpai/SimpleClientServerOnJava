import java.io.*;
import java.net.Socket;
import java.util.Date;

public class DataSafer {

    public static void writeToTextLog(Socket socket,String action) throws IOException {
        Date date = new Date();
        String strOut = "[" + String.valueOf(date.toInstant()) + "]:"+ socket.getInetAddress() + ":" + socket.getPort() + " => " + action;
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
