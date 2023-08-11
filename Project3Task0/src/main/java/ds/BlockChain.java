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

    //calculating total difficulty
    public int getTotalDifficulty()
    {
        int totalDifficulty = 0;
        //iterating through blockchain
        for(Block block : blockChain){
            totalDifficulty +=  block.getDifficulty();
        }
        return totalDifficulty;
    }

    public double getTotalExpectedHashes() {
        // Initialize a variable to keep track of the total expected hashes
        double totalExpectedHashes = 0;
        // Loop through all blocks in the blockchain
        for (Block block : blockChain) {
            // Get the difficulty of the block
            int difficulty = block.getDifficulty();
            // Calculate the expected number of hashes for a block with the current difficulty (16^difficulty)
            double expectedHashes = Math.pow(16, difficulty);
            // Add the expected number of hashes to the total
            totalExpectedHashes += expectedHashes;
        }
        // Return the total expected number of hashes for the entire blockchain
        return totalExpectedHashes;
    }


    public void addBlock(Block newBlock){
        //setting previous hash of new block as chainhash of old chain
        newBlock.setPreviousHash(this.chainHash);
        //adding new block
        blockChain.add(newBlock);
        //calculating proof of work
        chainHash = newBlock.proofOfWork();
    }

    public String isChainValid() {
        // If the blockchain only has one block, check that its hash is valid
        if (blockChain.size() == 1) {
            // Calculate the hash for the first block in the chain
            String hashCalculated = blockChain.get(0).calculateHash();

            // Create a target string with 'difficulty' number of zeros
            StringBuilder targetBuilder = new StringBuilder();
            for (int i = 0; i < blockChain.get(0).getDifficulty(); i++) {
                targetBuilder.append('0');
            }
            String target = targetBuilder.toString();

            // Check that the calculated hash begins with target number of zeros
            if (!hashCalculated.substring(0, blockChain.get(0).getDifficulty()).equals(target)) {
                return "Improper hash on node 1 Does not begin with " + target;
            } else if (!chainHash.equals(hashCalculated)) { // Check that the chain hash matches the hash of the only block
                return "Chain hash error";
            } else { // The chain is valid if both checks pass
                return "Chain Verification: TRUE";
            }
        } else { // If the blockchain has more than one block, check each block and the chain hash
            for (int i = 1; i < blockChain.size(); i++) {
                // Create a target string with 'difficulty' number of zeros for the current block
                StringBuilder targetBuilder = new StringBuilder();
                for (int j = 0; j < blockChain.get(i).getDifficulty(); j++) {
                    targetBuilder.append('0');
                }
                String target = targetBuilder.toString();
                // Get the hash of the previous block
                String previousBlockHash = blockChain.get(i - 1).calculateHash();
                // Get the hash pointer for the current block
                String hashPointer = blockChain.get(i).getPreviousHash();
                // Calculate the hash of the current block
                String blockHash = blockChain.get(i).calculateHash();
                // Check that the hash pointer for the current block matches the hash of the previous block
                if (!previousBlockHash.equals(hashPointer)) {
                    return "Hash pointer error for block " + (i);
                } else if (!blockHash.substring(0, blockChain.get(i).getDifficulty()).equals(target)) { // Check that the calculated hash begins with 'difficulty' number of zeros for the current block
                    return "Chain Verification: FALSE \nImproper hash on node " + (i) + " Does not begin with " + target;
                }
            }
            // Check that the chain hash matches the hash of the last block in the chain
            if (!chainHash.equals(blockChain.get(blockChain.size() - 1).calculateHash())) {
                return "Chain hash error";
            }
            // The chain is valid if all checks pass
            return "Chain Verification: TRUE";
        }
    }

    // This method repairs the blockchain by updating any incorrect previous hash values
    public void repairChain()
    {
        //declaring variables
        String hashPointer;
        String blockHash;
        //if size is 1, set previous hash as blank
        if(blockChain.size()==1) {
            blockChain.get(0).setPreviousHash("");
        }
        if(blockChain.size()>1) {
            for (int i = 1; i < blockChain.size(); i++) {
                //calculate previous hash nad proof of work
                hashPointer = blockChain.get(i).getPreviousHash();
                blockHash = blockChain.get(i-1).proofOfWork();
                //check if there are any inconsistencies
                if(!hashPointer.equals(blockHash))
                {
                    //update
                    blockChain.get(i).setPreviousHash(blockHash);
                }
            }
        }
        //update chain hash
        chainHash = blockChain.get(blockChain.size()-1).proofOfWork();
    }

    //toString method to print blockchain
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

    public static void main(String args[])
    {
        //create blockchain obj
        BlockChain chain = new BlockChain();
        //creating genesis clock
        Block genesisBlock = new Block(0, chain.getTime(), "Genesis", 2);
        //set previous hash of genesis block as null
        genesisBlock.setPreviousHash("");
        //calculate proof of work
        genesisBlock.proofOfWork();
        //add to chain
        chain.addBlock(genesisBlock);
        Scanner scanner = new Scanner(System.in);

        while(true)
        {
            System.out.println("0. View basic blockchain status.");
            System.out.println("1. Add a transaction to the blockchain.");
            System.out.println("2. Verify the blockchain.");
            System.out.println("3. View the blockchain.");
            System.out.println("4. Corrupt the chain.");
            System.out.println("5. Hide the Corruption by recomputing hashes.");
            System.out.println("6. Exit");
            int choice = scanner.nextInt();
            Timestamp startTime,endTime;
            double execTime;
            switch(choice)
            {
                case 0:
                    System.out.println("Current Size of chain: " + chain.getChainSize());
                    System.out.println("Difficulty of most recent block: " + chain.getLatestBlock().getDifficulty());
                    System.out.println("Total difficulty for all blocks: " + chain.getTotalDifficulty());
                    System.out.println("Approximate hashes per second on this machine: " + chain.getHashesPerSecond());
                    System.out.println("Expected total hashes required for the whole chain: " + chain.getTotalExpectedHashes());
                    System.out.println("Nonce for the most recent block: " + chain.getLatestBlock().getNonce());
                    System.out.println("Chain hash: " + chain.getLatestBlock().calculateHash() + "\n");
                    break;

                case 1:
                    //Adding new block to chain
                    System.out.println("Enter difficulty > 0");
                    int difficulty = scanner.nextInt();
                    System.out.println("Enter transaction");
                    scanner.nextLine();
                    String data = scanner.nextLine();
                    Block newBlock = new Block(chain.getChainSize(), chain.getTime(), data, difficulty);
                    startTime = getTime();
                    chain.addBlock(newBlock);
                    endTime = getTime();
                    execTime = endTime.getTime() - startTime.getTime();
                    System.out.println("Total execution time to add this block was " + (execTime) + " milliseconds");
                    break;

                case 2:
                    //checking is chain is valid
                    startTime = getTime();
                    System.out.println(chain.isChainValid());
                    endTime = getTime();
                    execTime = endTime.getTime() - startTime.getTime();
                    System.out.println("Total execution time required to verify the chain was " + (execTime) + " milliseconds");
                    break;

                case 3:
                    //viewing the blockchain
                    System.out.println("View the blockchain: " + chain.toString());
                    break;

                case 4:
                    //corrupting the blockchain
                    System.out.println("Corrupt the Blockchain");
                    System.out.print("Enter block ID of block to Corrupt: ");
                    int id = scanner.nextInt();
                    System.out.print("Enter new data for block " + id + ":\n");
                    scanner.nextLine();
                    String corruptData = scanner.nextLine();
                    blockChain.get(id).setData(corruptData);
                    System.out.printf("Block %d now holds %s\n",id,corruptData);
                    break;

                case 5:
                    //repairing the blockchain
                    startTime = getTime();
                    chain.repairChain();
                    endTime = getTime();
                    execTime = endTime.getTime() - startTime.getTime();
                    System.out.println("Total execution time required to repair the chain was " + (execTime) + " milliseconds");

                    break;

                case 6:
                    //exit
                    System.exit(0);

            }

        }

    }


}

/*
Analysis:
addBlock()
1.
Enter difficulty > 0
2
Enter transaction
Alice pays Billy 1 DSCoin
Total execution time to add this block was 12.0 milliseconds


2.
Enter difficulty > 0
3
Enter transaction
Sara pays Daisy 5 DSCoin
Total execution time to add this block was 62.0 milliseconds

3.
Enter difficulty > 0
4
Enter transaction
Billy pays Clara 15 DSCoin
Total execution time to add this block was 199.0 milliseconds

4.
Enter difficulty > 0
5
Enter transaction
Clara pays Daisy 16 DSCoin
Total execution time to add this block was 850.0 milliseconds


For adding a block, we can see that increasing difficulty increases the time required for adding a block to the blockchain

isChainValid()
For checking the validity of the chain, the difficulty of the block does not really affect the time taken to check the validity of the chain

Total execution time required to verify the chain was 0.0 milliseconds
Total execution time required to verify the chain was 1.0 milliseconds
Total execution time required to verify the chain was 0.0 milliseconds
Total execution time required to verify the chain was 2.0 milliseconds
Total execution time required to verify the chain was 1.0 milliseconds

We can observe that the behaviour is random and the difficulty does not really affect the time required to verify the chain

repairChain()
For repair chain,
Checking by corrupting each block with different difficulty and then repairing the chain
Difficulty  = 1
Total execution time required to repair the chain was 43.0 milliseconds
Difficulty = 2
Total execution time required to repair the chain was 59.0 milliseconds
Difficulty = 3
Total execution time required to repair the chain was 113.0 milliseconds
Difficulty = 5
Total execution time required to repair the chain was 1553.0 milliseconds

Thus for repairChain(), we can observe that the time increases as difficulty increases. This is because when repairing, the proof of work is calculated again, which uses the difficulty for calculation.


 */



