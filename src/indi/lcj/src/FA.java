package indi.lcj.src;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class FA {


    public ArrayList<Integer> states;
    public ArrayList<Trans> transitions;
    public int final_state;

    public FA() {
        this.states = new ArrayList<Integer>();
        this.transitions = new ArrayList<Trans>();
        this.final_state = 0;
    }

    public void display() {
        for (Trans t : transitions) {
            System.out.println("(" + t.state_from + ", " + t.trans_symbol +
                    ", " + t.state_to + ")");
        }
    }

    public HashSet<Character> getChars() {
        HashSet<Character> set = new HashSet<>();
        for (Trans c : this.transitions) {
            if (c.trans_symbol != 'ε') set.add(c.trans_symbol);
        }
        return set;
    }

    public void calculateStates(){
        states.clear();
        for(Trans tran:transitions){
            if(!states.contains(tran.state_from))states.add(tran.state_from);
            if(!states.contains(tran.state_to))states.add(tran.state_to);
        }
    }


    static class Trans {
        public int state_from, state_to;
        public char trans_symbol;

        public Trans(int v1, int v2, char sym) {
            this.state_from = v1;
            this.state_to = v2;
            this.trans_symbol = sym;
        }
    }



}



