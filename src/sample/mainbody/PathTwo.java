package sample.mainbody;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.*;
import java.security.SecureRandom;
import java.util.*;

public class PathTwo implements Controlling {
    private final int diplacement;
    private PathOne one ;
    private String FileName;
    private String strLine;
    private ArrayList<String> TRecord;
    private InstructionFormate InstructionSet;
    private ObjectCode ObjectCode;
    private int PC;
    private boolean indexed =false;
    private final int BASE;
    private BufferedReader reader;
    private ArrayList<String>modify;
    private ArrayList<String>modifylabel;
    private ArrayList<String>Adresses;
    private String fileName;
    private final int start;
    private ArrayList<String> lastlist;
    private String Startinst;
    private final HashMap<String,Integer>controlSection;
    private final HashMap<String,String>EXTREF;
    private final HashMap<String,String>EXTDEF;
    private String bControl="norm";
    Formatter updatedFile;
    private int tyeef=0;
    private boolean cSectionflag=false;
    private String mybControl="norm";

    public PathTwo() {
        this.one = new PathOne("code.txt");
        if(one.getSymboltable().getRowInformmation().get("Bse")!=null) {
            BASE = one.getSymboltable().getAddress(one.getSymboltable().getBase());
        }
        else
             BASE = 0;
        modify=new ArrayList<>();
        modifylabel=new ArrayList<>();
        Adresses=new ArrayList<>();
        diplacement=one.getDispalcement();
        fileName=one.getProjectName();
        start=one.getStart();
        Startinst=one.getStartInst();
        controlSection=one.getControlsection();
        controlSection.put("norm",0);
        EXTDEF=one.getEXTDEF();
        EXTREF=one.getEXTREF();
    }
    public PathTwo (String FileName)
    {   this();
        this.FileName=FileName;
        TRecord= new ArrayList();
        InstructionSet=PathOne.getInstructionSet();
        System.out.println(SymbolicTable.getTable().getRowInformmation().toString());
        if(SymbolicTable.getTable()!=null&&SymbolicTable.getTable().getValue("ErrOrS")==0&&checkUndefinedAddress(one.getSymboltable()))  {
            onStart();
        }else{
            System.err.println("WE could not formate object code because there is errors");
        }

    }
    @Override
    public void onStart() {
        try {
            reader=new BufferedReader(new FileReader(FileName));
        } catch (FileNotFoundException e) {
            System.err.println("Wrong File Path or File does not exist!");
            e.printStackTrace();
        }
        onRead();
    }
    public String[] concat(String[] a, String[] b) {
        int aLen = a.length-1;
        int bLen = b.length;
        String[] c= new String[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
    @Override
    public void onRead() {
        try {

            Adresses.add("norm");
            TRecord.add("sec");
            mainloop:
            while ((strLine = reader.readLine()) != null)   {
                indexed=false;
                String[] data1 = strLine.split("[ ]+");
                String []data2=data1[data1.length-1].split(",");
                String [] tokens  =concat(data1, data2);
                int location=0;
                while(InstructionFormate.getInstructionTable().getInstructionMap().get(tokens[location])==null)
                    location++;

                 if(tokens[location].equals("END"))
                     break mainloop;

                if(tokens[tokens.length-1].equals("X")&&InstructionFormate.getInstructionTable().getFormate(tokens[location])>=3) {
                    tokens = Arrays.copyOfRange(tokens, 0, tokens.length - 1);
                    indexed=true;
                }if(tokens[location].equals("CSECT")) {
                    modify.add("sec");
                    modifylabel.add(tokens[location-1]);
                    cSectionflag=true;
                }
                if(InstructionFormate.getInstructionTable().getFormate(tokens[location])==4) {
                 String tem=tokens[location+1].substring(1);
                 try{
                        int temp=Integer.parseInt(tem);
                    }catch (Exception ex){
                        modify.add(tokens[0]);
                        modifylabel.add(tokens[location+1]);
                     System.out.println(modify.toString());
                     System.out.println(modifylabel.toString());
                 }
                }
                if(InstructionFormate.getInstructionTable().getFormate(tokens[location])!=0||(tokens[location].equals("BYTE")||tokens[location].equals("WORD"))
                        ||(tokens[location].equals("RESB")||tokens[location].equals("RESW"))) {
                    Adresses.add(tokens[0]);
                }else if((tokens[location].equals("EXTDEF"))||(tokens[location].equals("EXTREF"))){
                    Adresses.add(0+"");
                }else if((tokens[location].equals("CSECT"))){
                    Adresses.add(tokens[1]);
                }

                ObjectCode=new ObjectCode(tokens,location,InstructionFormate.getInstructionTable().getFormate(tokens[location
                        ]),BASE,indexed?"X":"l");
                TRecord.add(ObjectCode.getObjectCode());
            }
        } catch (IOException e) {
            System.err.println("Empty File!");
            e.printStackTrace();
        }

        updateTRecord();
    }
    private void modifyList(){
    lastlist=new ArrayList<>();
        Iterator<String> it = TRecord.iterator();
    while (it.hasNext()) {
     String tem=it.next();
//        System.out.println(tem);
     if(tem==null)
         it.remove();

}
    Iterator<String> i = Adresses.iterator();
    Iterator<String> t = TRecord.iterator();
    while (i.hasNext()) {
        String tem=i.next();
        String te1=t.next();
        lastlist.add(tem+"#"+te1);

    }
}
    private void updateTRecord() {
        modifyList();
        openFile();
           String start=Integer.toHexString(this.start);
           while(start.length()<6)
               start="0"+start;
           String disp=Integer.toHexString(diplacement);
        while(disp.length()<6)
            disp="0"+disp;
           String line="H"+"^"+fileName+"^"+start+"^"+disp;
           addUpdate(line);
        String []temp;
        String rec="^";
        boolean state=false;
        String beginadress = null;
        Iterator<String> List = lastlist.iterator();
        loop:
        while (List.hasNext())
        {
            String val=List.next();
            temp=val.split("#");
            if(temp[1].equals("sec"))
                bControl=temp[0];
            if(!state){
                beginadress=temp[0];
                while(beginadress.length()<6){
                    beginadress="0"+beginadress;
                }
                state=true;
            }
            if(temp[1].equals("DEF")){
                String DEF="D^";
            for(Map.Entry entery:EXTDEF.entrySet()){
                 String var=entery.getKey().toString();
                if(EXTDEF.get(var).equals(bControl)) {
                 DEF+=var+"^"+Integer.toHexString(SymbolicTable.getTable().getAddress(var))+"^";
                }
            }
            addUpdate(DEF);
        }
            if(temp[1].equals("REF")){
                String REF="R^";
                for(Map.Entry entery:EXTREF.entrySet()){
                    String var=entery.getKey().toString();
                    if(EXTREF.get(var).equals(bControl)) {
                        REF+=var+"^";
                    }
                }
                addUpdate(REF);
            }
            if(temp[1].equals("sec")){
            if(!temp[0].equals("norm")){
                modifyrecord(modify);
                addUpdate("E^000000");
                addUpdate("");
                addUpdate("H^"+temp[0]+"^000000^"+(tyeef==0?"000010":"00002c"));
            tyeef++;
              }}

            if(!(temp[1].equals("Sep")||temp[1].equals("DEF")||temp[1].equals("REF")||temp[1].equals("sec"))&&(countRecord(rec)+temp[1].length())/2<=30){
                rec+=temp[1]+"^";
            }
            else if(countRecord(rec)!=0){
                String temper=rec;
                String length=Integer.toHexString(countRecord(rec)/2);
                length=length.length()!=2?"0"+length:length;
                while(beginadress.length()<6){
                    beginadress="0"+beginadress;
                }
                String trec="T"+"^"+beginadress+"^"+length+rec;
                trec=trec.substring(0,trec.length()-1);
                addUpdate(trec);
                state=false;
                rec="^";
                if((countRecord(temper)+temp[1].length())/2>30) {
                beginadress=temp[0];
                state=true;
                rec+=temp[1]+"^";
                }
            }else  state=false;
            }
          if(countRecord(rec)!=0){
              String length=Integer.toHexString(countRecord(rec)/2);
              length=length.length()!=2?"0"+length:length;
              String trec="T"+"^"+beginadress+"^"+length+rec;
              trec=trec.substring(0,trec.length()-1);
              addUpdate(trec);

          }
    modifyrecord(modify);
        String endindex=Integer.toHexString(SymbolicTable.getTable().getAddress(Startinst));
        while(endindex.length()<6){
            endindex="0"+endindex;
        }
        String End="E^"+Startinst+"^"+endindex;
        addUpdate(End);

    }

    private void modifyrecord(ArrayList<String> modify) {

        Iterator<String> l = modify.iterator();
        Iterator<String> il = modifylabel.iterator();
        try{
            while (l.hasNext()) {
            String casse=l.next();
                String var=il.next();
                if(casse.equals("sec")){
                   mybControl=var;
                    l.remove();
                    il.remove();
                    break;
                }
            Integer i=Integer.valueOf(casse,16);
            String mod;
            i++;
            String loc=Integer.toString(i);
            while(loc.length()<6){
                loc="0"+loc;
            }
            if(cSectionflag) {
                    int locat=0;
                if(controlSection.get(var)!=null)
                     locat=controlSection.get(var);
                else if(EXTREF.get(var)!=null)
                    locat=SymbolicTable.getTable().getAddress(var)+controlSection.get(EXTDEF.get(var));
                    String operator=locat<controlSection.get(mybControl)?"-":"+";
                    mod="M^"+loc+"^05^"+operator+var;
            }else
                 mod="M^"+loc+"^05";
            addUpdate(mod);
            l.remove();
            il.remove();
        }}catch (Exception ex){

        }
    }

    private int countRecord(String line){
       int i=0;
       int counter=0;
       while (i<line.length()){
           if(line.charAt(i)!='^')
               counter++;
        i++;
       }
       return counter;
}
    private boolean checkUndefinedAddress(SymbolicTable symboltable) {
        for(Map.Entry entery:symboltable.getRowInformmation().entrySet()){

            if(symboltable.getAddress(entery.getKey().toString())==-1) {
                System.err.println("There is undifined label  "+entery.getKey());

                return false;
            }
        }
        return true;

    }
    private void openFile(){
        try {

             updatedFile = new Formatter(new BufferedWriter(new FileWriter("Record.txt", true)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void addUpdate(String line){
        PrintWriter outputFile= null;
        try {
            outputFile = new PrintWriter(new FileWriter("Record.txt", true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        outputFile.printf("%s",line);
        outputFile.println();
        outputFile.close();

    }
    private void closefile(){
        updatedFile.close();
    }
}
