package indi.lcj.src;

import java.util.ArrayList;

public class DFA extends FA {
    public ArrayList<Integer> finalStates;

    public int read(int left, String s) {
        int state = 0;
        int right = left;
        int res = right;
        if (finalStates.contains(state)) res = right;
        for (; right < s.length(); right++) {
            state = move(state, s.charAt(right));
            if (state == -1) {
                break;
            }
            if (finalStates.contains(state)) res = right+1;

        }
        return res;
    }

    private int move(int state, char c) {
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

    public void clear() {
        ArrayList<Trans> newTrans = new ArrayList<>();
        for (Trans tran : transitions) {
            boolean flag = false;
            for (Trans newTran : newTrans) {
                if (tran.equals(newTran)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) newTrans.add(tran);
        }
        this.transitions = newTrans;
    }

//    public void display() {
//        for (Trans t : transitions) {
//            System.out.println("(" + (char) (t.state_from + (int) 'A') + ", " + t.trans_symbol +
//                    ", " + (char) (t.state_to + (int) 'A') + ")");
//        }
//    }
}
