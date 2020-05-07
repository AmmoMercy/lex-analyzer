package test;

import indi.lcj.src.DFA;
import indi.lcj.src.Lex;
import indi.lcj.src.NFA;
import indi.lcj.src.RegExp;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class NFATest {
    @Test
    public void nfa() {

        NFA nfa = Lex.thompson("(a|b)*abb");
        nfa.display();
        HashSet<Character>set=nfa.getChars();
        System.out.println(nfa.final_state);

    }
    @Test
    public  void SetEqual(){
        Set<Integer> set1=new HashSet<>();
        set1.add(1);
        set1.add(2);
        Set<Integer> set2=new HashSet<>();
        set2.add(1);
        System.out.println(set1.equals(set2));
    }
    @Test
    public  void determine(){
        NFA nfa = Lex.thompson("(a|b)*abb");
        DFA dfa=Lex.determine(nfa);
        System.out.println(nfa.final_state);
        dfa.display();
        System.out.println(dfa.final_state);
    }
}
