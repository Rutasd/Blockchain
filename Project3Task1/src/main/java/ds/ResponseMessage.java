/**
 * This class is used to send responses from server to client
 * This class has various parameterized constructors to cater to various cases
 * @author: Ruta Deshpande
 * @andrew id: rutasurd
 * @email id: rutasurd@andrew.cmu.edu
 * @date: 16th March 2023
 * Project 3
 */

//imports
package ds;
import com.google.gson.Gson;
import java.math.BigInteger;

public class ResponseMessage {
    //member variables
    Integer operation;
    Integer size;
    Integer difficulty;
    Integer totalDifficulty;
    Integer hashesPerSecond;
    Double totalExpectedHashes;
    BigInteger nonce;
    String hash;
    Double elapsedTime;
    String response;

    //constructor for case 0, where all fields are required
    public ResponseMessage(Integer operation, Integer size, Integer difficulty, Integer totalDifficulty, Integer hashesPerSecond, Double totalExpectedHashes, BigInteger nonce, String hash) {
        this.operation = operation;
        this.size = size;
        this.difficulty = difficulty;
        this.totalDifficulty = totalDifficulty;
        this.hashesPerSecond = hashesPerSecond;
        this.totalExpectedHashes = totalExpectedHashes;
        this.nonce = nonce;
        this.hash = hash;
    }

    //Constructor for cases where only selection and time of execution is necessary
    public ResponseMessage(Integer operation, Double elapsedTime)
    {
        this.operation = operation;
        this.elapsedTime = elapsedTime;
    }

    //Constructor for cases where only selection is necessary
    public ResponseMessage(Integer operation)
    {
        this.operation = operation;
    }

    //tostring method to convert class into json object
    public String toString(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    //setter method to set the response field in the class
    public void setResponse(String s)
    {
        this.response = s;
    }
}
