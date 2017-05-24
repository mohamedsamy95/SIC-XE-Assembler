package sample.mainbody;
import java.io.*;
import java.util.*;

public class PathOne implements Controlling{
    private int programCounter=0;
    private Integer opperandLocation=null;
    private String file;
    private BufferedReader reader;
    private String line;
    private int linecounter =0;
    private Boolean intiallocation=false;
    private static InstructionFormate formates=null;
    private SymbolicTable symboltable=null;
    private int Start=0;
    private int dispalcement=-1;
    private boolean breakflag=false;
    private Formatter updatedFile;
    private boolean basedefined=false;
    private int noOferrors=0;
    private HashSet<String> litteral;
    private String projectName;
    private String StartInst;
    private HashMap<String,Integer> label;
    private HashMap<String,Integer> controlsection;
    private int lastValue=0;
    private String bControl="norm";
    private HashMap<String,String> EXTDEF;
    private HashMap<String,String> EXTREF;
    public PathOne(String FileName) {
        file=FileName;
        formates=InstructionFormate.getInstructionTable();
        symboltable=SymbolicTable.getTable();
        litteral=new HashSet<String>();
        label=new HashMap();
        controlsection=new HashMap();
        EXTREF=new HashMap();
        EXTDEF=new HashMap();
        onStart();
    }

    @Override
    public void onStart() {
        try{
            reader=new BufferedReader(new FileReader("code.txt"));
            onRead();
        }catch(Exception ex){
            System.err.println("There is Error in the reader path one ");
            noOferrors++;
            System.out.println(ex.toString());

//            symboltable.setRow("ErrOrS", programCounter);
//            symboltable.setValue("ErrOrS", noOferrors);
            // System.exit(1);
        }

    }

    @Override
    public  void onRead() {

        try {
            while((line =reader.readLine())!=null){
                String[]trial=line.split("/");
                if(line.charAt(0)=='/')
                    continue;
                else if(trial.length>1)
                    line=trial[0];
                if(breakflag)
                    break;

                linecounter++;
                openFile();
                line=checkspace(line);
                String[] data1 = line.split("[ ]+");
                String []data2=data1[data1.length-1].split(",");
                String [] data =concat(data1, data2);
                String exp[]=line.split("[ ]+"+"EQU"+"[ ]+");

                if(intiallocation) {
                    onlyOneInstruct(data);
                   if(!data[opperandLocation].equals("EQU"))
                    addUpdate(programCounter, line);

                }
                if(exp.length>1){
                    handdleExp(exp);
                }else
                    handdleLine(data,line);
            }
            if(!breakflag) {
                System.err.println(" END STATEMENT IS MISSED ");
                noOferrors++;
            }            closefile();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("The error at line " + linecounter);
            noOferrors++;
//            System.exit(1);

        }
        litteralEnd();
    }

    private void handdleExp(String[] exp) {
        Expression expression3 = new Expression
                (exp[1] ,label,programCounter+"");
        if(symboltable.getRowInformmation().get(exp[0])!= null)
            symboltable.setAddress(exp[0],expression3.getValue());
        else
            symboltable.setRow(exp[0],expression3.getValue());
        if(expression3.getValue()==0) {
         noOferrors++;
            System.err.println("EQU ERROR" + linecounter);
            symboltable.setValue("ErrOrS", noOferrors);
        }  label.put(exp[0],expression3.getValue());
         addUpdate(expression3.getValue(),line);
    }


    public void handdleLine(String[] data,String line) {
        symboltable.setRow("ErrOrS", programCounter);
        if(onlyOneInstruct(data)){
            if(!intiallocation) {
                getStartCounter(data);
                addUpdate(programCounter,line);
            }
            search4Lapel(data);
            checkOperandNumber(data);
            defineMiddelLabel(data);
            reservedInstruction(data);

        }else {
            System.err.println("The error at line "+linecounter);
            noOferrors++;
//            System.exit(1);

        }
    }
    private boolean onlyOneInstruct(String[] data) {
        int counter=0;
        for(int i=0;i<data.length;i++ )
            if(formates.getInstructionMap().get(data[i])!=null){
                opperandLocation=i;
                counter++;
            }

        return counter==1?true:false;
    }

    private void search4Lapel(String[] data) {
        if (formates.getInstructionMap().get(data[0]) == null) {
            if(symboltable.getRowInformmation().get(data[0])==null) {
                symboltable.setRow(data[0], programCounter);
                label.put(data[0],programCounter);
            }else{
                if(symboltable.getAddress(data[0])==-1) {
                    symboltable.setAddress(data[0], programCounter);
                    label.put(data[0],programCounter);
                }else {
                    System.err.println("The label defined twise ");
                    System.err.println("The error line"+ linecounter);
                    noOferrors++;

//                       System.exit(1);
                }
            }

            if (formates.getInstructionMap().get(data[1]) == null) {
                System.err.println("UNDEFINED INSTRUCTION");
                System.err.println("The error at line "+linecounter);
//                System.exit(1);
            } else {
                programCounter += formates.getFormate(data[1]);
                opperandLocation=1;
            }
        }else if(data.length>=2&&(data[0].equals("END")||data[1].equals("END"))){
            if(symboltable.getRowInformmation().get(data[1])==null) {
                System.err.println("This Label does not exist at which the code should start");
                System.err.println("The line of Error is "+linecounter);
                noOferrors++;

            }else {
                opperandLocation = 0;
                dispalcement = programCounter-Start;
                breakflag = true;
                StartInst=data[opperandLocation+1];
            }
            symboltable.setValue("ErrOrS", noOferrors);
        }else{

            programCounter += formates.getFormate(data[0]);
            opperandLocation=0;
        }

    }

    private void checkOperandNumber(String[] data) {
        switch(formates.getFormate(data[opperandLocation])){
            case 0:
                if(formates.getNumberOfRegister(data[opperandLocation])!=data.length-opperandLocation-1&&data.length!=opperandLocation+2
                        &&!(data[opperandLocation].equals("EXTDEF")||data[opperandLocation].equals("EXTREF"))){
                    System.err.println("Syntax error in number of opperand");
                    System.err.println("The error at line "+linecounter);
                    noOferrors++;

//           System.exit(1);
                }
                break;
            case 1:
                if(data.length!=opperandLocation+1){
                    System.err.println("Syntax error in number of opperand");
                    System.err.println("The error at line "+linecounter);
                    noOferrors++;

//    System.exit(1);
                }

                break;
            case 2:
                if(formates.getNumberOfRegister(data[opperandLocation])!=data.length-opperandLocation-1&&(data.length!=opperandLocation+2||data.length!=opperandLocation+3)){
                    System.err.println("Syntax error in number of opperand");
                    System.err.println("The error at line "+linecounter);
                    noOferrors++;

//            System.exit(1);
                }if(data.length==opperandLocation+2) {
                try{
                    InstructionFormate.Register.valueOf(data[opperandLocation+1]);
                }catch (IllegalArgumentException ex){
                    System.err.println("this register does not exist");
                    System.err.println("The error at line "+linecounter);
                    noOferrors++;
//            System.exit(1);
                }
            }
                if(data.length==opperandLocation+3) {
                    try{ if(formates.getNumberOfRegister2(data[opperandLocation])==1){
                        InstructionFormate.Register.valueOf(data[opperandLocation+1]);
                        Integer.parseInt(data[opperandLocation+2]);
                    }else{
                        InstructionFormate.Register.valueOf(data[opperandLocation+1]);
                        InstructionFormate.Register.valueOf(data[opperandLocation+2]);
                    }
                    }catch (IllegalArgumentException ex){
                        System.err.println("this register doesnot exist   or opperand 2 should be immediate number not label");
                        System.err.println("The error at line "+linecounter);
                        noOferrors++;
//                System.exit(1);
                    }
                }
                break;
            case 3:
                boolean indexed=false;
                if(data.length==opperandLocation+3){
                    if(!data[data.length-1].equals("X"))
                        indexed = true;
                }
                if(formates.getNumberOfRegister(data[opperandLocation])!=data.length-opperandLocation-1&&data.length!=opperandLocation+2&&indexed){
                    System.err.println("Syntax error in number of opperand");
                    System.err.println("The error at line "+linecounter);
                    noOferrors++;
//            System.exit(1);
                }

                break;
            case 4:
                if(formates.getNumberOfRegister(data[opperandLocation])!=data.length-opperandLocation-1&&data.length!=opperandLocation+2){
                    System.err.println("Syntax error in number of opperand");
                    System.err.println("The error at line "+linecounter);
                    noOferrors++;
//                    System.exit(1);
                }

                break;
        }

    }
    private void defineMiddelLabel(String[] data) {
        int iCounter=data.length-1-opperandLocation;
        int subopperanlaocation=opperandLocation;
        boolean labelDefined=false;
        if(formates.getFormate(data[opperandLocation])!=2&&!data[opperandLocation].equals("BYTE"))
            while(iCounter!=0&&!labelDefined){
                subopperanlaocation++;
                data[subopperanlaocation]=reservedchar(data[subopperanlaocation].charAt(0))?data[subopperanlaocation].substring(1):data[subopperanlaocation];
                if(data[subopperanlaocation].charAt(0)=='=')
                    litteral.add(data[subopperanlaocation]);
                try{int i=Integer.parseInt(data[subopperanlaocation]) ;
//            System.out.println(i);

                }catch (NumberFormatException ex) {
                    if(!data[subopperanlaocation-1].equals("USE")&&symboltable.getRowInformmation().get(data[subopperanlaocation])==null)
                        symboltable.setRow(data[subopperanlaocation], -1);
                    labelDefined=true;
                    if(symboltable.getValue(data[subopperanlaocation])==-1&&formates.getFormate(data[opperandLocation])!=4
                            &&!(data[opperandLocation].equals("EXTDEF")||data[opperandLocation].equals("EXTREF")))
                        System.err.println("THE EXTERN MUST BE FORMATE 4  error in line  "+linecounter);
                }
                iCounter--;

            }
//        System.out.println(data[subopperanlaocation]);

    }
    private void reservedInstruction(String[] data) {

        switch(data[opperandLocation]){
            case "BYTE":
                if(formates.getNumberOfRegister(data[opperandLocation])!=data.length-opperandLocation-1&&data.length!=opperandLocation+2){
                    System.err.println("Syntax error in number of opperand");
                    System.err.println("The error at line "+linecounter);
                    noOferrors++;
//                 System.exit(1);
                }
                String val[]=data[opperandLocation+1].split("'");
                switch(val[0]){
                    case"C":programCounter+=val[1].length();
                        symboltable.setValue(data[opperandLocation-1],Integer.parseInt(hexaOfString(val[1]),16));
                        break;
                    case"X":programCounter+=Math.ceil(val[1].length()/2);
                        symboltable.setValue(data[opperandLocation-1],Integer.parseInt(val[1],16));
                        break;
                }
                break;
            case "WORD":
                if(formates.getNumberOfRegister(data[opperandLocation])!=data.length-opperandLocation-1&&data.length!=opperandLocation+2){
                    System.err.println("Syntax error in number of opperand");
                    System.err.println("The error at line "+linecounter);
                    noOferrors++;
//                 System.exit(1);
                }
                try{int value=Integer.parseInt(data[opperandLocation+1]);
                    programCounter+=3;
                    symboltable.setValue(data[opperandLocation-1],Integer.parseInt(data[opperandLocation+1],10));
                }catch (Exception ex){
                    System.err.println("you should add immediate value");
                    System.err.println("the error line "+linecounter);
                    noOferrors++;
                }
                break;

            case "RESW":
                if(formates.getNumberOfRegister(data[opperandLocation])!=data.length-opperandLocation-1&&data.length!=opperandLocation+2){
                    System.err.println("Syntax error in number of opperand");
                    System.err.println("The error at line "+linecounter);
                    noOferrors++;

//                 System.exit(1);
                }
                try{int value=Integer.parseInt(data[opperandLocation+1]);
                    programCounter+=value*3;
                }catch (Exception ex){
                    System.err.println("you should add immediate value");
                }
                break;


            case "RESB":
                if(formates.getNumberOfRegister(data[opperandLocation])!=data.length-opperandLocation-1&&data.length!=opperandLocation+2){
                    System.err.println("Syntax error in number of opperand");
                    System.err.println("The error at line "+linecounter);
                    noOferrors++;
//                 System.exit(1);
                }
                try{int value=Integer.parseInt(data[opperandLocation+1]);
                    programCounter+=value;
                }catch (Exception ex){
                    System.err.println("you should add immediate value");
                }
                break;
            case  "BASE":
                if(!basedefined) {
                    symboltable.setRow("Bse", programCounter);
                    symboltable.setBase(data[opperandLocation + 1]);
                }else{
                    System.err.println(" The base is already defined");
                    System.err.println("The error at line " +linecounter);
                    noOferrors++;
                }
                break;
            case "LTORG":
                if(formates.getNumberOfRegister(data[opperandLocation])!=data.length-opperandLocation-1&&data.length!=opperandLocation+1){
                    System.err.println("Syntax error in number of opperand");
                    System.err.println("The error at line "+linecounter);
                    noOferrors++;
//                 System.exit(1);
                }
                litteralEnd();
                break;
            case "ORG":
                programCounter=Integer.valueOf(data[opperandLocation+1],16);
                break;
            case "CSECT":
                EXTDEF.put(data[opperandLocation-1],data[opperandLocation-1]);
                controlsection.put(data[opperandLocation-1],programCounter+lastValue);
                symboltable.setValue(data[opperandLocation-1], -3);//-3 label of CSECT
                lastValue=programCounter;
                bControl=data[opperandLocation-1];
                programCounter=0;
             break;
            case "EXTREF":
                for(int i=opperandLocation+1;i<data.length;++i) {
                    EXTREF.put(data[i],bControl);
                    if (symboltable.getRowInformmation().get(data[i]) == null)
                        symboltable.setRow(data[i], -1);
                       symboltable.setValue(data[i], -1);
                }
                break;
            case "EXTDEF":

                for(int i=opperandLocation+1;i<data.length;++i) {

                   EXTDEF.put(data[i],bControl);
                    if (symboltable.getRowInformmation().get(data[i]) == null)
                        symboltable.setRow(data[i], -1);
                    symboltable.setValue(data[i], -2);

                }
                break;
        }
    }

    private void litteralEnd() {
        String temp,temp1;
        Iterator<String> itr=litteral.iterator();
        while(itr.hasNext()){
            temp= itr.next();
            temp1=temp.substring(1);
            String val[]=temp1.split("'");

            switch(val[0]){
                case"C":
                    System.out.println(Integer.parseInt(hexaOfString(val[1]),16));
                    symboltable.setValue(temp,Integer.parseInt(hexaOfString(val[1]),16));
                    symboltable.setAddress(temp,programCounter);
                    programCounter+=val[1].length();
                    break;
                case"X":
                    symboltable.setValue(temp,Integer.parseInt(val[1],16));
                    symboltable.setAddress(temp,programCounter);
                    programCounter+=Math.ceil(val[1].length()/2);
                    break;
            }
            itr.remove();
        }
    }

    private boolean reservedchar(char c) {
        switch (c){
            case '@':case '#':
                return true;
            default:
                return false;
        }
    }
    private void openFile(){
        try {

            updatedFile=
                    new Formatter(new BufferedWriter(new FileWriter("UpdateCode.txt", true)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void addUpdate(int addres,String line){
        String code = Integer.toHexString(addres);
        PrintWriter outputFile= null;
        try {
            outputFile = new PrintWriter(new FileWriter("UpdateCode.txt", true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        outputFile.printf("%s   %s",code,line);
        outputFile.println();
        outputFile.close();

    }
    private void closefile(){
        updatedFile.close();
    }



    public int getStart() {
        return Start;
    }

    public int getDispalcement() {
        return dispalcement;
    }

    public HashMap<String, Integer> getControlsection() {
        return controlsection;
    }

    private void getStartCounter(String[] data) {
        if(data[1].equals("START")){
            programCounter=Integer.parseInt(data[2],16);
            Start=programCounter;
            intiallocation=true;
            projectName=data[0];
        }else {
            System.err.println("START is missed to begin running the code");
            System.err.println("the error line "+linecounter);
            noOferrors++;

        }

    }
    private String hexaOfString(String data){
        int sum=0;
        for(int i=0;i<data.length();i++){
            sum=sum*100+data.charAt(i);
        }
        return sum!=0? sum+"":null;
    }
    private String checkspace(String line){
        while(line.charAt(0)==' '){
            line=line.substring(1);
        }
        return line;
    }
    public String[] concat(String[] a, String[] b) {
        int aLen = a.length-1;
        int bLen = b.length;
        String[] c= new String[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
    public SymbolicTable getSymboltable() {
        return symboltable;
    }
    public static InstructionFormate getInstructionSet()
    {
        return formates;
    }
    public String getProjectName() {
        return projectName;
    }

    public String getStartInst() {
        return StartInst;
    }

    public HashMap<String, String> getEXTDEF() {
        return EXTDEF;
    }

    public HashMap<String, String> getEXTREF() {
        return EXTREF;
    }
}