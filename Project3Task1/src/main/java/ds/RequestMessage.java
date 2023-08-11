/**
 * This class is used to send requests from client to server
 * This class has various parameterized constructors to cater to various cases
 * The response field has a getter and setter used for getting and setting the responses for particular cases
 * @author: Ruta Deshpande
 * @andrew id: rutasurd
 * @email id: rutasurd@andrew.cmu.edu
 * @date: 16th March 2023
 * Project 3
 */
//imports
package ds;
import com.google.gson.Gson;

public class RequestMessage {
    //member variables
    int selection;
    int difficulty;
    String data;
    int corruptId;

    //case 0,2,3,5
    public RequestMessage(int selection)
    {
     this.selection = selection;
    }

    //case 1
    public RequestMessage(int selection,int difficulty, String data)
    {
        this.selection = selection;
        this.difficulty = difficulty;
        this.data = data;
    }

    //case 4
    public RequestMessage(int selection,String data, int corruptId)
    {
        this.selection = selection;
        this.corruptId = corruptId;
        this.data = data;
    }


    public String toString(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
