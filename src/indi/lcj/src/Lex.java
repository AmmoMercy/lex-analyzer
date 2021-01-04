package indi.lcj.src;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 *
 */
public class Lex {
    private final static String DIR = "D:\\Doc\\java\\lex\\src\\";
    private final static String NUM = "0|1|2|3|4|5|6|7|8|9";
    private final static String UPPER_CHARS = "A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z";
    private final static Character EPSILON = 'ε';


    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        //将打印方式设为输出到文件
        PrintStream out = new PrintStream(DIR + "out");
        System.setOut(out);

        //读取正则
        String regpath = DIR + "regexp";
        InputStreamReader reader = new InputStreamReader(new FileInputStream(regpath));
        BufferedReader bufferedReader = new BufferedReader(reader);
        List<RegExp> list = new LinkedList<RegExp>();
        String str = bufferedReader.readLine();
        while (str != null) {
            String pattern = str;
            String reg = bufferedReader.readLine();
            list.add(new RegExp(pattern, reg));
            str = bufferedReader.readLine();
        }
        //对正则进行预处理
        for (RegExp regExp : list) {
            pretreatReg(regExp);
        }
        //存放DFA的map
        HashMap<String, DFA> dfaHashMap = new HashMap<>();
        LinkedList<String> keys = new LinkedList<>();
        for (RegExp exp : list) {
            NFA nfa = thompson(exp.getContent());
            System.out.println("\n" + exp.getName() + " NFA");
            nfa.display();
            DFA dfa = determine(nfa);
            System.out.println("\n" + exp.getName() + " DFA");
            dfa.display();
            minimize(dfa);
            System.out.println("\n" + exp.getName() + " minimized DFA");
            dfa.display();
            dfaHashMap.put(exp.getName(), dfa);
            keys.add(exp.getName());
        }
        //读取源程序
        Path SourcePath = Paths.get(DIR + "sourcecode.c");
        byte[] data = Files.readAllBytes(SourcePath);
        String sourcecode = new String(data, "utf-8");
        //去除多余字符
        sourcecode = sourcecode.replace("\n", "");
        sourcecode = sourcecode.replace(" ", "");
        sourcecode = sourcecode.replace("\r", "");
        int left = 0;
        while (left < sourcecode.length()) {
            boolean flag = false;
            for (String key : keys) {
                int res = dfaHashMap.get(key).read(left, sourcecode);
                if (res != left) {
                    System.out.println(sourcecode.substring(left, res) + " " + key);
                    left = res;
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                System.out.println(sourcecode.charAt(left) + " error!");
                break;
            }
        }
    }

    static boolean alphabet(char c) {
        return c != '(' && c != ')' && c != '*' && c != '|';
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
                if (c == '\\') c = regex.charAt(++i);
                operands.push(new NFA(c));
                if (ccflag) { // concat this w/ previous
                    operators.push('.'); // '.' used to represent concat.
                } else
                    ccflag = true;
            } else {
                if (c == ')') {
                    ccflag = false;

                    // 构造NFA直到遇到 '('
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
                            ccflag = true;
                        }
                    }
                } else if (c == '*') {
                    operands.push(NFA.kleene(operands.pop()));
                    ccflag = true;
                } else if (c == '(') { // if any other operator: push
                    if (ccflag) {
                        operators.push('.');
                        ccflag = false;
                    }
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
                //空闭包是空集 跳过
                if (cur.size() == 0) continue;
                boolean flag = false;
                int j = 0;
                for (; j < charSetMap.size(); j++) {
                    if (charSetMap.get(j).equals(cur)) {
                        flag = true;
                        break;
                    }
                }
                //没有包含该闭包
                if (flag == false) {
                    charSetMap.add(cur);
                }
                //是终态
                if (cur.contains(nfa.final_state) && !res.finalStates.contains(charSetMap.indexOf(cur)))
                    res.finalStates.add(charSetMap.indexOf(cur));
                res.transitions.add(new FA.Trans(i, j, c));
            }
        }
        res.calculateStates();
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

    public static DFA minimize(DFA dfa) {
        DFA res = new DFA();
        LinkedList<Integer> states = new LinkedList<>((ArrayList<Integer>) dfa.states.clone());
        LinkedList<LinkedList<Integer>> piNew = new LinkedList<>();
        for (int i : dfa.finalStates) {
//            LinkedList<Integer> temp = new LinkedList<>();
//            temp.add(i);
//            piNew.add(temp);
            states.remove(states.indexOf(i));
        }
        piNew.addFirst(states);
        piNew.add(new LinkedList<>(dfa.finalStates));
        final LinkedList<Character> chars = new LinkedList<>(dfa.getChars());
        while (true) {
            LinkedList<LinkedList<Integer>> piOld = (LinkedList<LinkedList<Integer>>) piNew.clone();
            for (int i = 0; i < piNew.size(); i++) {
                LinkedList<Integer> G = piNew.get(i);
                if (G.size() == 1) continue;
                LinkedList<LinkedList<Integer>> dividision = divide(dfa, G, chars, piOld);
                piOld.remove(G);
                piOld.addAll(dividision);
            }
            if (piNew.equals(piOld)) {
                break;
            } else {
                piNew = piOld;
            }
        }

        List<FA.Trans> trans = dfa.transitions;
        for (FA.Trans tran : trans) {
            for (int i = 0; i < piNew.size(); i++) {
                LinkedList<Integer> list = piNew.get(i);
                if (list.contains(tran.state_from)) {
                    tran.state_from = list.get(0);
                }
                if (list.contains(tran.state_to)) {
                    tran.state_to = list.get(0);
                }
            }
        }
        dfa.calculateStates();
        dfa.clear();
        return dfa;


    }

    static LinkedList<LinkedList<Integer>> divide(DFA dfa, LinkedList<Integer> G, LinkedList<Character> chars, LinkedList<LinkedList<Integer>> pi) {
        LinkedList<LinkedList<Integer>> res = new LinkedList<>();
        HashMap<Integer, LinkedList<Integer>> container = new HashMap<>();
        for (int state : G) {
            LinkedList<Integer> list = new LinkedList<>();
            for (char c : chars) {
                list.add(move(dfa, state, c, pi));
            }
            container.put(state, list);
        }

        LinkedList<Integer> keys = new LinkedList<>(container.keySet());
        boolean[] visited = new boolean[keys.size()];
        for (int i = 0; i < keys.size(); i++) {
            if (visited[i]) continue;
            LinkedList<Integer> temp = new LinkedList<>();
            temp.add(keys.get(i));
            for (int j = i + 1; j < keys.size(); j++) {
                if (!visited[j] && container.get(keys.get(j)).equals(container.get(keys.get(i)))) {

                    temp.add(keys.get(j));
                    visited[j] = true;
                }
            }
            res.add(temp);
        }
        return res;
    }

    static int move(DFA dfa, int s, char symbol, LinkedList<LinkedList<Integer>> pi) {
        int toState = -1;
        for (FA.Trans tran : dfa.transitions) {
            if (tran.state_from == s && tran.trans_symbol == symbol) {
                toState = tran.state_to;
                break;
            }
        }
        if (toState == -1) return -1;
        for (int i = 0; i < pi.size(); i++) {
            if (pi.get(i).contains(toState)) return i;
        }
        return -1;
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
