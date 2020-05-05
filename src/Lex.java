import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class Lex {
    private final static String DIR = "D:\\Doc\\java\\lex\\src\\";
    private final static String NUM="0|1|2|3|4|5|6|7|8|9";
    private final static String UPPER_CHARS="A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z";
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
        for(RegExp regExp:list){
            pretreatReg(regExp);
        }
        System.out.println();
    }

    private static void pretreatReg(RegExp regExp) {
        String regContent=regExp.getContent();
        regContent=regContent.replace(" ","");
        regContent=regContent.replace("[0-9]",NUM);
        regContent=regContent.replace("[a-z]",UPPER_CHARS.toLowerCase());
        regContent=regContent.replace("[A-Z]",UPPER_CHARS);
        regContent=regContent.replace("[a-z|A-Z]",UPPER_CHARS+"|"+UPPER_CHARS.toLowerCase());
        regContent=regContent.replace("[A-Z|a-z]",UPPER_CHARS+"|"+UPPER_CHARS.toLowerCase());
        regExp.setContent(regContent);

    }
}
