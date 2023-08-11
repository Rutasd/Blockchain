/**
 This class represents a block in a blockchain.
 Each block has an index, a timestamp, data, a previous hash, a nonce, and a difficulty.
 The block can calculate its hash using SHA-256 algorithm and also perform a proof of work
 to find a hash that meets the specified difficulty level.
 The class also has methods to get and set the values of its attributes.
 Additionally, it provides a toString method to serialize the block into a JSON string.
 @author: Ruta Deshpande
 @andrew id: rutasurd
 @email id: rutasurd@andrew.cmu.edu
 @date: 16th March 2023
 */

package ds;
//imports
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//Block class with member variables index, timestamp, data, previousHash, nonce, difficulty
public class Block {
    int index;
    Timestamp timestamp;
    String data;
    String previousHash;
    BigInteger nonce;
    int difficulty;

    //Parameterized constructor for block
    public Block(int index, Timestamp timestamp, String data, int difficulty) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
        this.nonce = BigInteger.valueOf(0);
    }

    //Getter for index
    public int getIndex() {
        return index;
    }

    //setter for index
    public void setIndex(int index) {
        this.index = index;
    }

    //getter for timestamp
    public Timestamp getTimestamp() {
        return timestamp;
    }

    //setter for timestamp
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    //getter for data
    public String getData() {
        return data;
    }

    //setter for data
    public void setData(String data) {
        this.data = data;
    }

    //getter for previous hash
    public String getPreviousHash() {
        return previousHash;
    }

    //setter for previous hash
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    //getter for difficulty
    public int getDifficulty() {
        return difficulty;
    }

    //getter for nonce
    public BigInteger getNonce() {
        return this.nonce;
    }

    //setter for difficulty
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * This method calculates the SHA-256 hash value of the concatenated string
     * containing the index, timestamp, data, previous hash, nonce, and difficulty
     * of a block.
     * @return a string representing the hexadecimal hash value of the block
     * @throws RuntimeException if the SHA-256 algorithm is not available
     */
    public String calculateHash()
    {

        // Combine the various fields into a string message
        String message = index + timestamp.toString() + data + previousHash + nonce + difficulty;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            // Compute the hash of the message using the message digest instance
            byte[] hashBytes = messageDigest.digest(message.getBytes());
            // Convert the hash bytes to a hexadecimal string representation
            String hexHash = bytesToHex(hashBytes);
            // Return the hexadecimal string hash
            return hexHash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     Converts an array of bytes into its hexadecimal representation.
     @param bytes the array of bytes to convert
     @return the hexadecimal representation of the input bytes as a String
     */
    public static String bytesToHex(byte[] bytes) {
        final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     Computes the proof of work for the block using the current difficulty level.
     This method calculates a hash for the block and increments the nonce value until the
     hash starts with the required number of leading zeros determined by the difficulty level.
     @return the hash value that meets the difficulty level requirement
     */
    public String proofOfWork() {
        // Create a string of leading zeros with length equal to the difficulty
        StringBuilder targetBuilder = new StringBuilder();
        for (int i = 0; i < difficulty; i++) {
            targetBuilder.append('0');
        }
        String target = targetBuilder.toString();
        // Calculate the hash of the current block and keep incrementing the nonce until a hash is found with the required number of leading zeros
        String hash = calculateHash();
        while (!hash.substring(0, this.difficulty).equals(target)) {
            // Increment the nonce
            this.nonce = this.nonce.add(BigInteger.ONE);
            // Calculate the hash with the new nonce
            hash = calculateHash();
        }
        // Return the hash with the required number of leading zeros
        return hash;
    }

    /**
     @return a JSON string representation of the block object
     */
    @Override
    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        //Creating gso object
        Gson gson = new GsonBuilder().setDateFormat(dateFormat.format(new Date())).disableHtmlEscaping().setPrettyPrinting().create();
        String jsonString = gson.toJson(this);
        jsonString = jsonString.replaceAll("[\r\n]+", "");
        return jsonString;
    }
}
