import java.util.LinkedList;
import java.util.List;

// for each NFA the only thing you exactly need to know is the start node and the end node
// whenever operating the NFA itself or dealing problem with it
public class NFA {
    private Node start;
    private Node end;

    public NFA(char c) {
        start = new Node();
        end = new Node();
        start.addNext(end, c);
    }

    public NFA(Node start, Node end) {
        this.start = start;
        this.end = end;
    }

    // *operation
    public NFA kleene(NFA nfa) {
        Node oldEnd = nfa.getEnd();
        Node oldStart = nfa.getStart();
        Node newStart = new Node();
        Node newEnd = new Node();
        //add the 4 edge with ε
        oldEnd.addNext(newEnd, 'ε');
        oldEnd.addNext(oldStart, 'ε');
        newStart.addNext(oldStart, 'ε');
        newStart.addNext(newEnd, 'ε');
        nfa.setStart(newStart);
        nfa.setEnd(newEnd);
        return nfa;
    }

    // ·operation
    public NFA concat(NFA nfa1, NFA nfa2) {
        Node start = nfa1.getEnd();
        for (Transmission tran : nfa2.getStart().getTrans()) {
            end.addNext(tran.getNext(), tran.getSymbol());
        }
        return new NFA(nfa1.start,nfa2.end);
    }

    // |operation
    public NFA union(NFA nfa1, NFA nfa2) {
        Node newStart = new Node();
        newStart.addNext(nfa1.getStart(), 'ε');
        newStart.addNext(nfa2.getStart(), 'ε');
        Node newEnd = new Node();
        nfa1.getEnd().addNext(newEnd, 'ε');
        nfa2.getEnd().addNext(newEnd, 'ε');
        return new NFA(newStart, newEnd);
    }

    public Node getStart() {
        return start;
    }

    public void setStart(Node start) {
        this.start = start;
    }

    public Node getEnd() {
        return end;
    }

    public void setEnd(Node end) {
        this.end = end;
    }
}

class Node {
    private boolean isFinal;
    private boolean isStart;
    private List<Transmission> trans;

    public Node() {
        this(false, false);
    }

    public Node(boolean isFinal, boolean isStart) {
        this.isFinal = isFinal;
        this.isStart = isStart;
        this.trans = new LinkedList<Transmission>();
    }

    public void addNext(Node node, char c) {
        trans.add(new Transmission(node, c));
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }


    public List<Transmission> getTrans() {
        return trans;
    }

    public void setTrans(List<Transmission> trans) {
        this.trans = trans;
    }
}

class Transmission {
    private Node next;
    private char symbol;

    Transmission(Node next, char symbol) {
        this.next = next;
        this.symbol = symbol;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }
}
