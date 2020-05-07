package indi.lcj.src;

import java.util.ArrayList;
import java.util.HashMap;

public class DFA extends FA {

    public void display() {
        for (Trans t : transitions) {
            System.out.println("(" + (char)(t.state_from+(int)'A') + ", " + t.trans_symbol +
                    ", " +(char)(t.state_to+(int)'A') + ")");
        }
    }
}
