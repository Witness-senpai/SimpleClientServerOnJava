import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.Date;

import java.io.IOException;

public class Controller {
    public TextArea textLog;
    public TextField textField;
    public CheckBox checkBoxGetArray;
    public CheckBox checkBoxConcat;
    public CheckBox checkBoxDelClones;
    public CheckBox checkBoxClear;
    public Button buttonGet;
    public Button buttonConnect;
    public TextField textHost;
    public Button buttonSendClick;
    public TextField textPort;

    public Client client;
    public Thread clientThread;

    public double connectionTime = 0;

    @FXML
    public void initialize(){
        textLog.setEditable(false);
        client = new Client(this);
    }

    public  void writeToLog(String s){
        textLog.appendText(s);
    }

    public void buttonConnectionToServer(ActionEvent actionEvent) {
        Date date = new Date();
        if (!client.isConnection){
            try {
                if (client.findServer()) { //Если нашли сервер и подключились
                    connectionTime = date.getTime(); //Время в секундах начала подключения
                    textPort.setDisable(true);
                    textHost.setDisable(true);
                    clientThread = new Thread(client,"clientThread");
                    clientThread.start();
                    buttonConnect.setText("Отключиться");
                    client.isConnection = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                this.writeToLog("Ошибка подключения!\nПроверьте данные для подключения:\n HOST: " + textHost.getText() + ":" + textPort.getText() + "\n");
            }
        }
        else{
            connectionTime = (date.getTime() - connectionTime) / 1000; //Время соедниения в секундах
            writeToLog("\n===Вы успешно отключились от сервера!===\nВремя сессии: " + connectionTime + " сек\n");
            textPort.setDisable(false);
            textHost.setDisable(false);
            buttonConnect.setText("Подключиться");
            client.isConnection = false;
            clientThread.interrupt();
            try {
                client.finalize();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Получить что-то от сервера
    public void buttonGetInfo(ActionEvent actionEvent) {
        String action = null;
        if (client.isConnection) {
            this.writeToLog("Клиент: ");
            if (checkBoxGetArray.isSelected()) {
                action = "PRINT";
                this.writeToLog("[запрос на печать всех символов]");
                checkBoxGetArray.setSelected(false);
            } else if (checkBoxClear.isSelected()) {
                action = "CLEAR";
                this.writeToLog("[запрос на очистку всего массива]");
                checkBoxClear.setSelected(false);
            } else if (checkBoxConcat.isSelected()) {
                action = "CONCAT";
                this.writeToLog("[запрос на конкатенацию всех символов]");
                checkBoxConcat.setSelected(false);
            } else if (checkBoxDelClones.isSelected()) {
                action = "DELCLONES";
                this.writeToLog("[запрос на удаление дубликатов]");
                checkBoxDelClones.setSelected(false);
            } else {//Если ничего не выбрано, то выход
                writeToLog("return nothing");
                return;
            }
            this.writeToLog("\n");
            //Отсылаем соответствующую команду
        }
        try {
            client.getOut().writeUTF(action);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Отправить символы на сервер
    public void buttonSend(ActionEvent actionEvent) {
        if (!textField.getText().isEmpty() && client.isConnection){
            char inputChar = textField.getText().charAt(0);
            try {
                client.getOut().writeUTF("INPUT"); //Говорим серверу, что будет символ
                clientThread.join(50); //Пауза для коррекции
                client.getOut().writeChar(inputChar);//И отсылаем символ
                client.getOut().flush();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            textLog.appendText("Клиент: отправка -> " + inputChar + "\n");
            textField.clear();
        }
    }
}
