package test;

import indi.lcj.src.DFA;
import indi.lcj.src.Lex;
import indi.lcj.src.NFA;
import indi.lcj.src.RegExp;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class NFATest {
    @Test
    public void nfa() {

        NFA nfa = Lex.thompson("(a|b)*abb");
        nfa.display();
        HashSet<Character> set = nfa.getChars();
        System.out.println(nfa.final_state);

    }

    @Test
    public void SetEqual() {
        Set<Integer> set1 = new HashSet<>();
        set1.add(1);
        set1.add(2);
        Set<Integer> set2 = new HashSet<>();
        set2.add(1);
        System.out.println(set1.equals(set2));
    }

    @Test
    public void determine() {
        NFA nfa = Lex.thompson("(a|b)*abb");
        DFA dfa = Lex.determine(nfa);
        dfa.display();
        System.out.println(dfa.finalStates);
    }

    @Test
    public void minimize() {
        NFA nfa = Lex.thompson("(a|b)*abb*");
        nfa.display();
        DFA dfa = Lex.determine(nfa);
        System.out.println("\ndfa ");
        dfa.display();
        System.out.println(dfa.finalStates);
        dfa = Lex.minimize(dfa);
        System.out.println("\ndfa mimi");
        dfa.display();
        System.out.println(dfa.finalStates);
    }

    @Test
    public void read() {
        NFA nfa = Lex.thompson("0*(.|ε)00*");
        nfa.display();
        DFA dfa = Lex.determine(nfa);
        System.out.println(nfa.final_state);
        Lex.minimize(dfa);
        dfa.display();
        System.out.println(dfa.finalStates);

        System.out.println(dfa.read(0, "0.0"));
    }

    @Test
    public void readAgain() {
        NFA nfa = Lex.thompson("{|}|\\(|\\)|,|;");
        nfa.display();
        DFA dfa = Lex.determine(nfa);
        System.out.println(nfa.final_state);
        dfa.display();
        System.out.println(dfa.finalStates);
        System.out.println(dfa.read(0, "("));
    }
}
