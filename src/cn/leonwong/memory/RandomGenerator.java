package cn.leonwong.memory;

import java.util.Random;
import java.util.HashSet;
import java.util.ArrayList;

/**
 * Generate a list of numbers to simulate running
 */
class RandomGenerator {
    /**
     * Used to enum the states of the next instruction counter
     */
    private static class states{
        static final int choosePriorOnes = 0;
        static final int afterChoosePriorOnes = 1;
        static final int chooseInferiorOnes = 2;
        static final int afterChooseInferiorOnes = 3;
    }
    /// The first instruction to execute
    private int startIndex;
    /// The list of instructions
    public ArrayList<Integer> instructions;

    /**
     * Generate a random list generator
     * @param start the first instruction to execute
     */
    public RandomGenerator(int start){
        if (start >= 320){
            System.out.println("Error: Starting index larger than 320! Calculating remainder!");
        }
        this.startIndex = start % 320;
        this.generate();
    }

    /**
     * Generate the list of instructions.
     * This function will automatically be called after construction
     */
    private void generate(){
        HashSet<Integer> hashSet = new HashSet<>();
        int count;
        Random rd = new Random();
        rd.setSeed(System.currentTimeMillis());
        this.instructions = new ArrayList<>();

        // first put in first two instructions
        count = 2;
        this.instructions.add(this.startIndex);
        this.instructions.add((this.startIndex + 1) % 320);
        hashSet.add(this.startIndex);
        hashSet.add((this.startIndex + 1) % 320);

        int state = states.choosePriorOnes;

        Integer nextInstruction;
        Integer lastInstruction = (this.startIndex + 1) % 320;

        while (hashSet.size() < 320 // Stop if all the instructions have been executed...
                && count < 1280 // or there are 1280 (upper bound) instructions executed
                ){
            ++count;
            if (count % 128 == 0){
                // Change the seed in make the list more random
                rd.setSeed(System.currentTimeMillis() * (count / 128));
            }
            // Generate next instruction...
            if (state == states.choosePriorOnes){
                if (lastInstruction.equals(0)){
                    nextInstruction = Math.abs(rd.nextInt(320));
                }
                else {
                    nextInstruction = Math.abs(rd.nextInt() % lastInstruction);
                }
                state = states.afterChoosePriorOnes;
            }
            else if (state == states.afterChoosePriorOnes) {
                nextInstruction = (lastInstruction + 1) % 320;
                state = states.chooseInferiorOnes;
            }
            else if (state == states.chooseInferiorOnes){
                if (lastInstruction.equals(320)){
                    nextInstruction = Math.abs(rd.nextInt(320));
                }
                else {
                    int tmp = Math.abs(rd.nextInt(320 - lastInstruction));
                    nextInstruction = lastInstruction + tmp;
                    state = states.afterChooseInferiorOnes;
                }
            }
            else {
                nextInstruction = (lastInstruction + 1) % 320;
                state = states.choosePriorOnes;
            }
            lastInstruction = nextInstruction;
            // add it to the list...
            this.instructions.add(nextInstruction);
            // also add it to the set
            hashSet.add(nextInstruction);
        }
    }

    /**
     * Getter for the list of instructions
     * @return the list of instructions
     */
    public ArrayList<Integer> getInstructions(){
        return this.instructions;
    }
}
