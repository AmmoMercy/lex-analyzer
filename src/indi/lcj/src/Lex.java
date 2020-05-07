package indi.lcj.src;


import java.io.*;
import java.util.*;

public class Lex {
    private final static String DIR = "D:\\Doc\\java\\lex\\src\\";
    private final static String NUM = "0|1|2|3|4|5|6|7|8|9";
    private final static String UPPER_CHARS = "A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z";
    private final static Character EPSILON = 'ε';

    public static void main(String[] args) throws Exception {
        String path = DIR + "regexp";
        InputStreamReader reader = new InputStreamReader(new FileInputStream(path));
        BufferedReader bufferedReader = new BufferedReader(reader);
        List<RegExp> list = new LinkedList<RegExp>();
        String str = bufferedReader.readLine();
        while (str != null) {
            String pattern = str;
            String reg = bufferedReader.readLine();
            list.add(new RegExp(pattern, reg));
            str = bufferedReader.readLine();
        }
        for (RegExp regExp : list) {
            pretreatReg(regExp);
        }
        System.out.println();
    }

    public static boolean alphabet(char c) {
        return c >= 'a' && c <= 'z' || c == EPSILON;
    }


    public static NFA thompson(String regex) {

        Stack<Character> operators = new Stack<Character>();
        Stack<NFA> operands = new Stack<NFA>();
        Stack<NFA> concat_stack = new Stack<NFA>();
        boolean ccflag = false; // concat flag
        char op, c; // current character of string
        int para_count = 0;
        NFA nfa1, nfa2;

        for (int i = 0; i < regex.length(); i++) {
            c = regex.charAt(i);
            if (alphabet(c)) {
                operands.push(new NFA(c));
                if (ccflag) { // concat this w/ previous
                    operators.push('.'); // '.' used to represent concat.
                } else
                    ccflag = true;
            } else {
                if (c == ')') {
                    ccflag = false;
                    if (para_count == 0) {
                        System.out.println("Error: More end paranthesis " +
                                "than beginning paranthesis");
                        System.exit(1);
                    } else {
                        para_count--;
                    }
                    // process stuff on stack till '('
                    while (!operators.empty() && operators.peek() != '(') {
                        op = operators.pop();
                        if (op == '.') {
                            nfa2 = operands.pop();
                            nfa1 = operands.pop();
                            operands.push(NFA.concat(nfa1, nfa2));
                        } else if (op == '|') {
                            nfa2 = operands.pop();

                            if (!operators.empty() &&
                                    operators.peek() == '.') {

                                concat_stack.push(operands.pop());
                                while (!operators.empty() &&
                                        operators.peek() == '.') {

                                    concat_stack.push(operands.pop());
                                    operators.pop();
                                }
                                nfa1 = NFA.concat(concat_stack.pop(),
                                        concat_stack.pop());
                                while (concat_stack.size() > 0) {
                                    nfa1 = NFA.concat(nfa1, concat_stack.pop());
                                }
                            } else {
                                nfa1 = operands.pop();
                            }
                            operands.push(NFA.union(nfa1, nfa2));
                        }
                    }
                } else if (c == '*') {
                    operands.push(NFA.kleene(operands.pop()));
                    ccflag = true;
                } else if (c == '(') { // if any other operator: push
                    operators.push(c);
                    para_count++;
                } else if (c == '|') {
                    operators.push(c);
                    ccflag = false;
                }
            }
        }
        while (operators.size() > 0) {
            if (operands.empty()) {
                System.out.println("Error: imbalanace in operands and "
                        + "operators");
                System.exit(1);
            }
            op = operators.pop();
            if (op == '.') {
                nfa2 = operands.pop();
                nfa1 = operands.pop();
                operands.push(NFA.concat(nfa1, nfa2));
            } else if (op == '|') {
                nfa2 = operands.pop();
                if (!operators.empty() && operators.peek() == '.') {
                    concat_stack.push(operands.pop());
                    while (!operators.empty() && operators.peek() == '.') {
                        concat_stack.push(operands.pop());
                        operators.pop();
                    }
                    nfa1 = NFA.concat(concat_stack.pop(),
                            concat_stack.pop());
                    while (concat_stack.size() > 0) {
                        nfa1 = NFA.concat(nfa1, concat_stack.pop());
                    }
                } else {
                    nfa1 = operands.pop();
                }
                operands.push(NFA.union(nfa1, nfa2));
            }
        }
        NFA res = operands.pop();
        HashSet<Integer> fromStates = new HashSet<>();
        HashSet<Integer> toStates = new HashSet<>();

        for (FA.Trans tran : res.transitions) {
            fromStates.add(tran.state_from);
            toStates.add(tran.state_to);
        }
        for (int i : fromStates) {
            toStates.remove(i);
        }
        for (int i : toStates) res.final_state = i;
        return res;
    }

    public static DFA determine(NFA nfa) {
        Set<Character> characters = nfa.getChars();
        List<HashSet> charSetMap = new LinkedList<>();
        HashSet<Integer> start = new HashSet();
        DFA res = new DFA();
        start.add(nfa.states.get(0));
        charSetMap.add(epsilonClosure(nfa, start));
        for (int i = 0; i < charSetMap.size(); i++) {
            res.states.add(i);
            for (char c : characters) {
                HashSet<Integer> cur = epsilonClosure(nfa, smove(nfa, charSetMap.get(i), c));
                boolean flag = false;
                int j = 0;
                for (; j < charSetMap.size(); j++) {
                    if (charSetMap.get(j).equals(cur)) {
                        flag = true;
                        break;
                    }
                }
                if (flag == false) {
                    charSetMap.add(cur);
                }
                if (cur.contains(nfa.final_state)) res.final_state = charSetMap.indexOf(cur);
                res.transitions.add(new FA.Trans(i, j, c));
            }
        }
        return res;
    }

    static HashSet<Integer> smove(NFA nfa, HashSet<Integer> set, char c) {
        ArrayList<FA.Trans> trans = nfa.transitions;
        HashSet<Integer> res = new HashSet<>();
        for (int i : set) {
            for (FA.Trans tran : trans) {
                if (tran.state_from == i && tran.trans_symbol == c) {
                    res.add(tran.state_to);
                }
            }
        }
        return res;
    }

    static HashSet<Integer> epsilonClosure(NFA nfa, HashSet<Integer> set) {
        ArrayList<FA.Trans> trans = nfa.transitions;
        HashSet<Integer> res = (HashSet<Integer>) set.clone();
        while (true) {
            int old = res.size();
            for (FA.Trans tran : trans) {
                if (res.contains(tran.state_from) && tran.trans_symbol == EPSILON) {
                    res.add(tran.state_to);
                }

            }
            if (old == res.size()) break;
        }

        return res;
    }


    private static void pretreatReg(RegExp regExp) {
        String regContent = regExp.getContent();
        regContent = regContent.replace("[0-9]", NUM);
        regContent = regContent.replace("[a-z]", UPPER_CHARS.toLowerCase());
        regContent = regContent.replace("[A-Z]", UPPER_CHARS);
        regContent = regContent.replace("[a-z|A-Z]", UPPER_CHARS + "|" + UPPER_CHARS.toLowerCase());
        regContent = regContent.replace("[A-Z|a-z]", UPPER_CHARS + "|" + UPPER_CHARS.toLowerCase());
        regExp.setContent(regContent);
    }
}
