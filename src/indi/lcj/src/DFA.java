package indi.lcj.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DFA extends FA {
    public ArrayList<Integer> finalStates;

    public int read(int left, String s) {
        int state = 0;
        int right = left;
        for (; right < s.length(); right++) {
            if (finalStates.contains(state) && !finalStates.contains(move(state, s.charAt(right)))) return right;
            state = move(state, s.charAt(right));
            if (state == -1) {
                return left;
            }
        }
        return right;
    }

    int move(int state, char c) {
        for (Trans tran : transitions) {
            if (state == tran.state_from && c == tran.trans_symbol) {
                return tran.state_to;
            }
        }
        return -1;
    }

    public DFA() {
        super();
        finalStates = new ArrayList<>();
    }

//    public void display() {
//        for (Trans t : transitions) {
//            System.out.println("(" + (char) (t.state_from + (int) 'A') + ", " + t.trans_symbol +
//                    ", " + (char) (t.state_to + (int) 'A') + ")");
//        }
//    }
}
