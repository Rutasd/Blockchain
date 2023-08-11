/**
 * The BlockChain class represents a blockchain
 * that contains a list of blocks. Each block in the blockchain
 * is connected to the previous block through a hash pointer.
 * This class provides methods to add new blocks to the blockchain,
 * validate the chain, and repair the chain if needed. It also provides
 * methods to compute the hashes per second, total difficulty, and
 * total expected hashes for the blockchain.
 *  @author: Ruta Deshpande
 *  @andrew id: rutasurd
 *  @email id: rutasurd@andrew.cmu.edu
 *  @date: 16th March 2023
 */

package ds;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;

//Creating a Blockchain class with member variables blockchain, chainhash, hashesPerSecond
public class BlockChain {
    static ArrayList<Block> blockChain;
    static String chainHash;
    static int hashesPerSecond;

    //Constructor for blockchain which initializes chainhash as empty string and hashespersecond as 0
    public BlockChain() {
        this.blockChain = new ArrayList<>();
        chainHash = "";
        hashesPerSecond =0;
    }

    //getter for chainhash
    public String getChainHash() {
        return chainHash;
    }

    //getter method for time
    public static Timestamp getTime()
    {
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        return  currentTimestamp;
    }

    //getter method for getting latest  block
    public Block getLastestBlock()
    {
        return blockChain.get(blockChain.size()-1);
    }

    //getter method for getting chainsize
    public int getChainSize()
    {
        return blockChain.size();
    }

    //
    public static void computeHashesPerSecond()  {
        int numHashes = 2000000;
        Timestamp startTime = getTime();
        MessageDigest messageDigest = null;
        String message = "000000000000000";
        double difference;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        for(int i = 0; i < numHashes; i++){
            byte[] hash = messageDigest.digest(message.getBytes());
        }
        Timestamp endTime = getTime();
        difference = endTime.getTime() - startTime.getTime();
        hashesPerSecond = (int) ( (double)numHashes / (difference) * 1000);

    }

    //getter method for getting the latest block in the blockchain
    public Block getLatestBlock()
    {
        return blockChain.get(blockChain.size()-1);
    }

    //method to get approximate hashesPerSecond
    public int getHashesPerSecond()
    {
        //computeHashesPerSecond() method is called which sets the field hashesPerSecond
        computeHashesPerSecond();
        return hashesPerSecond;
    }

    //
    public Block getBlock(int i)
    {
        return blockChain.get(i);
    }

    public int getTotalDifficulty()
    {
        int totalDifficulty = 0;
        for(Block block : blockChain){
            totalDifficulty +=  block.getDifficulty();
        }
        return totalDifficulty;
    }

    public double getTotalExpectedHashes()
    {
        double totalExpectedHashes = 0;
        for (Block block : blockChain) {
            int difficulty = block.getDifficulty();
            double expectedHashes = Math.pow(16, difficulty);
            totalExpectedHashes += expectedHashes;
        }
        return totalExpectedHashes;
    }

    public void addBlock(Block newBlock){
        newBlock.setPreviousHash(this.chainHash);
        blockChain.add(newBlock);
        chainHash = newBlock.proofOfWork();
    }

    public String isChainValid()
    {
        if(blockChain.size()==1) {
            String hashCalculated = blockChain.get(0).calculateHash();
            StringBuilder targetBuilder = new StringBuilder();
            for (int i = 0; i < blockChain.get(0).getDifficulty(); i++) {
                targetBuilder.append('0');
            }
            String target = targetBuilder.toString();
            if(!hashCalculated.substring( 0, blockChain.get(0).getDifficulty()).equals(target))
            {
                return "Improper hash on node 0 Does not begin with " +target;
            }
            else if(!chainHash.equals(hashCalculated)){
                return "Chain hash error";
            }else{
                return "Chain Verification: TRUE";
            }
        }
        else {
            for(int i=1;i<blockChain.size();i++)
            {
                StringBuilder targetBuilder = new StringBuilder();
                for (int j = 0; j <blockChain.get(i).getDifficulty(); j++) {
                    targetBuilder.append('0');
                }
                String target = targetBuilder.toString();

                String previousBlockHash = blockChain.get(i-1).calculateHash();
                String hashPointer = blockChain.get(i).getPreviousHash();
                String blockHash = blockChain.get(i).calculateHash();

                if(!previousBlockHash.equals(hashPointer))
                {
                    return "Hash pointer error for block "+(i);
                }
                else if(!blockHash.substring( 0, blockChain.get(i).getDifficulty()).equals(target))
                {
                    return "Chain Verification: FALSE \nImproper hash on node "+(i)+ " Does not begin with " +target;
                }
            }
            if(!chainHash.equals(blockChain.get(blockChain.size()-1).calculateHash())){
                return "Chain hash error";
            }
            return "Chain Verification: TRUE";
        }
    }

    public void repairChain()
    {
        String hashPointer;
        String blockHash;
        if(blockChain.size()==1) {
            blockChain.get(0).setPreviousHash("");
        }
        if(blockChain.size()>1) {
            for (int i = 1; i < blockChain.size(); i++) {
                hashPointer = blockChain.get(i).getPreviousHash();
                blockHash = blockChain.get(i-1).proofOfWork();
                if(!hashPointer.equals(blockHash))
                {
                    blockChain.get(i).setPreviousHash(blockHash);
                }
            }
        }
        chainHash = blockChain.get(blockChain.size()-1).proofOfWork();
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("{\"ds_chain\" : [");
        for (int i = 0; i < this.blockChain.size(); i++) {
            sb.append(this.blockChain.get(i).toString());
            if (i != this.blockChain.size()-1) {
                sb.append(",\n");
            } else {
                sb.append("\n");
            }
        }
        sb.append("], \"chainHash\":\"");
        sb.append(this.chainHash);
        sb.append("\"}");

        String jsonString = sb.toString();
        return jsonString;

    }

//    public static void main(String args[])
//    {
//        BlockChain chain = new BlockChain();
//        Block genesisBlock = new Block(0, chain.getTime(), "Genesis", 2);
//        genesisBlock.setPreviousHash("");
//        genesisBlock.proofOfWork();
//        chain.addBlock(genesisBlock);
//        Scanner scanner = new Scanner(System.in);
//
//        while(true)
//        {
//            System.out.println("0. View basic blockchain status.");
//            System.out.println("1. Add a transaction to the blockchain.");
//            System.out.println("2. Verify the blockchain.");
//            System.out.println("3. View the blockchain.");
//            System.out.println("4. Corrupt the chain.");
//            System.out.println("5. Hide the Corruption by recomputing hashes.");
//            System.out.println("6. Exit");
//            int choice = scanner.nextInt();
//            Timestamp startTime,endTime;
//            double execTime;
//            switch(choice)
//            {
//                case 0:
//                    System.out.println("Current Size of chain: " + chain.getChainSize());
//                    System.out.println("Difficulty of most recent block: " + chain.getLatestBlock().getDifficulty());
//                    System.out.println("Total difficulty for all blocks: " + chain.getTotalDifficulty());
//                    System.out.println("Approximate hashes per second on this machine: " + chain.getHashesPerSecond());
//                    System.out.println("Expected total hashes required for the whole chain: " + chain.getTotalExpectedHashes());
//                    System.out.println("Nonce for the most recent block: " + chain.getLatestBlock().getNonce());
//                    System.out.println("Chain hash: " + chain.getLatestBlock().calculateHash() + "\n");
//                    break;
//
//                case 1:
//                    System.out.println("Enter difficulty > 0");
//                    int difficulty = scanner.nextInt();
//                    System.out.println("Enter transaction");
//                    scanner.nextLine();
//                    String data = scanner.nextLine();
//                    Block newBlock = new Block(chain.getChainSize(), chain.getTime(), data, difficulty);
//                    startTime = getTime();
//                    chain.addBlock(newBlock);
//                    endTime = getTime();
//                    execTime = endTime.getTime() - startTime.getTime();
//                    System.out.println("Total execution time to add this block was " + (execTime) + " milliseconds");
//                    System.out.println(chain.getBlock(0).toString());
//                    break;
//
//                case 2:
//                    System.out.println("Verifying entire chain");
//                    startTime = getTime();
//                    System.out.println("Chain verification: " + chain.isChainValid());
//                    endTime = getTime();
//                    execTime = endTime.getTime() - startTime.getTime();
//                    System.out.println("Total execution time required to verify the chain was " + (execTime) + " milliseconds");
//                    break;
//
//                case 3:
//                    System.out.println("View the blockchain: " + chain.toString());
//                    break;
//
//                case 4:
//                    System.out.println("Corrupt the Blockchain");
//                    System.out.print("Enter block ID of block to Corrupt: ");
//                    int id = scanner.nextInt();
//                    System.out.print("Enter new data for block " + id + ":\n");
//                    scanner.nextLine();
//                    String corruptData = scanner.nextLine();
//                    blockChain.get(id-1).setData(corruptData);
//                    System.out.printf("Block %d now holds %s\n",id,corruptData);
//                    break;
//
//                case 5:
//                    chain.repairChain();
//                    break;
//
//                case 6:
//                    System.exit(0);
//
//            }
//
//        }
//
//    }


}



