import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static String endCVF = "END:VCARD";
    private static String startCVF = "BEGIN:VCARD";

    private static int index = 0;

    public static List<User> list = new ArrayList();
    public static List<User> listEnd = new ArrayList();

    public static void main(String[] args){
        scanUser("111643.vcf");
        scanUser("111808.vcf");
        scanUser("111721.vcf");
        scanUser("111831.vcf");

        createNewEmptyBase(list);
        System.out.println(list.size());
        System.out.println(listEnd.size());

//        for (int i = 0; i < list.size(); i++) {
//            System.out.println(list.get(i));
//        }

        createBaseNotNull("ContactOma.vcf");

        for (int i = 0; i < listEnd.size(); i++) {
            System.out.println(listEnd.get(i));
        }

    }

    public static void scanUser(String address){
        File file = new File(address);
        FileReader fr = null;


        StringBuilder userName = new StringBuilder();

        try {
            fr = new FileReader(file);
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка чтения файла");
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(fr);
        try {
            String current = reader.readLine();
            String next = reader.readLine();

            boolean lock = false;
            boolean searchUserPieces = false;
            boolean searchNumber = false;
            boolean searchUserName = false;
            while (current != null){
                if (current.equals(startCVF) && !lock){
                    //System.out.println("---------------");
                    list.add(new User());
                    lock = true;
                }

                if (searchUserPieces && lock){
                    //System.out.println("this_line++");
                    userName.append(current + "\n");
                    if(next != null
                            &&
                            (next.equals(endCVF)
                            || ifNumberTrue(next)
                            || ifDataTrue(next))
                    ){
                        searchUserPieces = false;
                    }
                }

                if (ifDataTrue(current) && !searchUserName && lock){
                    //System.out.println("this_line");
                    userName.append(dataUser(current) + "\n");
                    searchUserName = true;
                    if(next != null
                            && !next.equals(endCVF)
                            && !ifNumberTrue(next)
                            && !ifDataTrue(next)
                    ){
                        searchUserPieces = true;
                    }
                }

                if (ifNumberTrue(current) && !searchNumber && lock){
                    list.get(index).setNumber(dataUserNumber(current));
                    //System.out.println("TELEPHONUMBER");
                    searchNumber = true;
                }

                if (current.equals(endCVF) && lock){
                    list.get(index).setName(koi8r(userName.toString()));
                    list.get(index).setNameData(userName.toString());
                    //System.out.println("------------------------------");
                    lock = false;
                    searchNumber = false;
                    searchUserName = false;
                    index ++;
                    userName.delete(0,2048);
                }

                try {
                    current = next;
                    next = reader.readLine();
                } catch (IOException e){
                    System.out.println("Строки для чтения в файле закончились.");
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения строки");
            e.printStackTrace();
        }
    }

    public static boolean ifDataTrue(String line){
        //String etholon = "FN;CHARSET=UTF-8;ENCODING=QUOTED-PRINTABLE:";
        char[] chars = line.toCharArray();
        if(chars == null || chars.length <=0){
            return false;
        }
        try{
            if((chars[0]=='O'
                    && chars[1]=='R'
                    && chars[2]=='G'
                    && chars[3]==';'
                    && chars[4]=='C')){
                return true;
            }
            if(chars[0]=='F'
                    && chars[1]=='N'
                    && chars[2]==';'
                    && chars[3]=='C'
                    && chars[4]=='H'){
                return true;
            }
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static boolean ifNumberTrue(String line){
        //String etholon = "TEL;CELL:";
        char[] chars = line.toCharArray();
        if(chars == null || chars.length <=0){
            return false;
        }
        try{
            if(chars[0]=='T'
                    && chars[1]=='E'
                    && chars[2]=='L'
                    && chars[3]==';'){
                return true;
            } else if (chars[0]=='F'
                    && chars[1]=='N'
                    && chars[2]==':'){
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static String dataUserNumber(String line){
        String etholon = "TEL;CELL:";
        String etholon2 = "FN:";
        StringBuilder stringBuilder = new StringBuilder();
        String res;
        char[] chars = line.toCharArray();
        boolean type = true;
        if(chars[0]=='T'
                && chars[1]=='E'
                && chars[2]=='L'
                && chars[3]==';'){
            type = true;
        } else if (chars[0]=='F'
                && chars[1]=='N'
                && chars[2]==':'){
            type = false;
        }
        if(type == true){
            for (int i = etholon.length(); i < chars.length; i++) {
                stringBuilder.append(chars[i]);
            }
        } else if (type == false){
            for (int i = etholon2.length(); i < chars.length; i++) {
                stringBuilder.append(chars[i]);
            }
        }

        res = stringBuilder.toString();
        stringBuilder.delete(0,1024);
        return res;
    }


    public static String dataUser(String line){
        String etholon = "FN;CHARSET=UTF-8;ENCODING=QUOTED-PRINTABLE:";
        String etholon2 = "ORG;CHARSET=UTF-8;ENCODING=QUOTED-PRINTABLE:";
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = line.toCharArray();
        boolean type = true;
        if((chars[0]=='O'
                && chars[1]=='R'
                && chars[2]=='G'
                && chars[3]==';'
                && chars[4]=='C')){
            type = false;
        } else if(chars[0]=='F'
                && chars[1]=='N'
                && chars[2]==';'
                && chars[3]=='C'
                && chars[4]=='H'){
            type = true;
        }
        if (type){
            for (int i = etholon.length(); i < chars.length; i++) {
                stringBuilder.append(chars[i]);
            }
        } else if (!type){
            for (int i = etholon2.length(); i < chars.length; i++) {
                stringBuilder.append(chars[i]);
            }
        }
        return stringBuilder.toString();
    }

    public static String koi8r(String string) {
        String input = string;
        String decode = decode(input, "UTF-8", "quoted-printable", "UTF-8");
        return decode;
    }

    public static String decode(String text, String textEncoding, String encoding, String charset) {
        if (text.length() == 0) {
            return text;
        }
        try {
            byte[] asciiBytes = text.getBytes(textEncoding);
            InputStream decodedStream = MimeUtility.decode(new ByteArrayInputStream(asciiBytes), encoding);
            byte[] tmp = new byte[asciiBytes.length];
            int n = decodedStream.read(tmp);
            byte[] res = new byte[n];
            System.arraycopy(tmp, 0, res, 0, n);
            return new String(res, charset);
        } catch (IOException | MessagingException e) {
            e.printStackTrace();
            return text;

        }
    }

    public static void createNewEmptyBase(List<User> listof){
        for (int i = 0; i < listof.size(); i++) {
            if(searchUser(listof.get(i))){
                listEnd.add(listof.get(i));
            }
        }
    }

    public static boolean searchUser(User userGetBoolean) {
        if(userGetBoolean.getName() == null |
        userGetBoolean.getNumber() == null ){
            return false;
        }
        if(     userGetBoolean.getName().equals("") |
                userGetBoolean.getNumber().equals("")){
            return false;
        }
        for (int i = 0; i < listEnd.size(); i++) {
            if(listEnd.get(i).getName().equals(userGetBoolean.getName()) |
            listEnd.get(i).getNumber().equals(userGetBoolean.getNumber())){
                return false;
            }
        }
        return true;
    }

    public static void createBaseNotNull(String str){
        File myFile = new File(str);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(myFile, true));
        } catch (IOException e) {

        }
        String lineSeparator = System.getProperty("line.separator");
        int index = 0;
        while (index < listEnd.size()){
//            if(listEnd.get(index).getName()!=null &&
//            list.get(index).getNumber() != null){
                try {
                    writer.write("BEGIN:VCARD" + lineSeparator);
                    writer.write("FN;CHARSET=UTF-8;ENCODING=QUOTED-PRINTABLE:"+listEnd.get(index).getNameData() + lineSeparator);
                    //writer.write("X-GROUP-MEMBERSHIP:My Contacts" + lineSeparator);
                    writer.write("TEL;CELL:" + listEnd.get(index).getNumber() + lineSeparator);
                    writer.write("END:VCARD" + lineSeparator);
                    writer.flush();
                } catch (IOException e) {

                }
//            }
            index++;
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
