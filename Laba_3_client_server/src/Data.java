import java.io.Serializable;
import java.util.*;

public class Data implements Serializable {
    private ArrayList<Character> array;
    private String arrayInStr;

    public Data(){
        array = new ArrayList<Character>();
        arrayInStr = null;
    }

    public void clear(){
        array.clear();
    }

    public void delClones(){
        array = new ArrayList<Character>(new HashSet<Character>(array));
    }

    public void concat(){
        arrayInStr = "";
        for (int i = 0; i < array.size(); i++){
            arrayInStr += array.get(i);
        }
    }

    public int getArraySize(){
        return array.size();
    }

    public char getArrayElement(int i){
        return array.get(i);
    }

    public void addArrayElement(char c){
        array.add(c);
    }

    public String getArrayInStr() {
        return arrayInStr;
    }
}
