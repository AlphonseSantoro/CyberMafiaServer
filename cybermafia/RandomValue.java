package cybermafia;

import java.util.Random;

public class RandomValue {

    Random rand;

    public RandomValue(){
        rand = new Random();
    }

    public int getRandomValue(int min, int max){
        return min + rand.nextInt(max);
    }

    public char getRandomChar(char min, int max){
        return (char)(rand.nextInt(max) + min);
    }
}
