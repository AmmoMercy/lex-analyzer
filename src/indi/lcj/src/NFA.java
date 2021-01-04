package indi.lcj.src;

import java.util.ArrayList;
import java.util.HashSet;

public class NFA extends FA {

    public NFA() {
        super();
    }

    public NFA(int size) {
        this.states = new ArrayList<Integer>();
        this.transitions = new ArrayList<Trans>();
        this.final_state = 0;
        this.setStateSize(size);
    }

    public NFA(char c) {
        this.states = new ArrayList<Integer>();
        this.transitions = new ArrayList<Trans>();
        this.setStateSize(2);
        this.final_state = 1;
        this.transitions.add(new Trans(0, 1, c));
    }

    public void setStateSize(int size) {
        for (int i = 0; i < size; i++)
            this.states.add(i);
    }


    public static NFA kleene(NFA n) {
        NFA result = new NFA(n.states.size() + 2);
        result.transitions.add(new Trans(0, 1, 'ε')); // new trans for q0

        for (Trans t : n.transitions) {
            result.transitions.add(new Trans(t.state_from + 1,
                    t.state_to + 1, t.trans_symbol));
        }

        result.transitions.add(new Trans(n.states.size(),
                n.states.size() + 1, 'ε'));

        result.transitions.add(new Trans(n.states.size(), 1, 'ε'));

        result.transitions.add(new Trans(0, n.states.size() + 1, 'ε'));

        result.final_state = n.states.size() + 1;
        return result;
    }

    public static NFA concat(NFA nfa1, NFA nfa2) {
        nfa2.states.remove(0); // delete nfa2's initial state
        int base = nfa1.states.size();
        for (Trans t : nfa2.transitions) {
            nfa1.transitions.add(new Trans(t.state_from + base - 1,
                    t.state_to + base - 1, t.trans_symbol));
        }

        for (Integer s : nfa2.states) {
            nfa1.states.add(s + base + 1);
        }

        nfa1.final_state = base + nfa2.states.size() - 2;
        return nfa1;

    }

    public static NFA union(NFA nfa1, NFA nfa2) {
        NFA result = new NFA(nfa1.states.size() + nfa2.states.size() + 2);

        result.transitions.add(new Trans(0, 1, 'ε'));

        for (Trans t : nfa1.transitions) {
            result.transitions.add(new Trans(t.state_from + 1,
                    t.state_to + 1, t.trans_symbol));
        }

        result.transitions.add(new Trans(nfa1.states.size(),
                nfa1.states.size() + nfa2.states.size() + 1, 'ε'));

        result.transitions.add(new Trans(0, nfa1.states.size() + 1, 'ε'));

        for (Trans t : nfa2.transitions) {
            result.transitions.add(new Trans(t.state_from + nfa1.states.size()
                    + 1, t.state_to + nfa1.states.size() + 1, t.trans_symbol));
        }

        result.transitions.add(new Trans(nfa2.states.size() + nfa1.states.size(),
                nfa1.states.size() + nfa2.states.size() + 1, 'ε'));

        result.final_state = nfa1.states.size() + nfa2.states.size() + 1;
        return result;
    }
}

