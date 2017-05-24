package sample.mainbody;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by hp-laptop on 4/13/2017.
 */
public class InstructionFormate implements Controlling {

private BufferedReader reader;
private String line;
private HashMap<String,InFormation> instructionMap;
private static InstructionFormate formates=null;
public enum Register {
        A(0),X(1),L(2),B(3),
        S(4),T(5),F(6),pc(8),sw(9);
       private final int location;
       Register(int location) {
           this.location = location;
       }

    public int getLocation() {
        return location;
    }
}
    private InstructionFormate() {
        instructionMap=new HashMap<String,InFormation>();
        System.out.println("Starting loading Instruction");
        onStart();
        System.out.println("done");
        System.out.println("**************************************");
    }

    @Override
    public void onStart() {

        try{
                   reader=new BufferedReader(new FileReader("E:\\Ahmed\\GITHUB_RES\\Git2\\sic-xe\\src\\sample\\files/InstructionSetTable.txt"));
                    onRead();
           }catch(Exception ex){
                   System.err.println("The instruction File is not exist");
//                    System.exit(0);
           }

    }
    @Override
    public void onRead() {
        try {
            while((line =reader.readLine())!=null){
                String[] data = line.split("[ ]+");
                if(data.length==4)
                    instructionMap.put(data[0], new InFormation(data[1], data[2], data[3]));
                else
                    instructionMap.put(data[0],new InFormation(data[1],data[2],data[3],data[4]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, InFormation> getInstructionMap() {
        return instructionMap;
    }

    public static InstructionFormate getInstructionTable() {
        if(formates == null) {
            formates=new InstructionFormate();
        }
        return formates;
    }



    private class InFormation {
        private Integer formate;
        private String oppCode;
        private Integer numberOfRegister;
        private Integer numberOfRegister2;

        public InFormation(String formate, String oppCode, String numberOfRegister, String numberOfRegister2) {
            this.formate = new Integer(formate);
            this.oppCode = oppCode;
            this.numberOfRegister = new Integer(numberOfRegister);
            this.numberOfRegister2 = new Integer(numberOfRegister2);
        }

        public InFormation(String formate, String oppCode, String numberOfRegister) {
            this.formate = new Integer(formate);
            this.oppCode = oppCode;
            this.numberOfRegister = new Integer(numberOfRegister);
        }

        @Override
        public String toString() {
            return "The formate is "+formate+"  The oppCode is "+oppCode
                    +" The No oF Register "+numberOfRegister +" The No oF Register in Formate 2 "+numberOfRegister2+"\n";
        }

    }


    public Integer getFormate(String Label) {
        return instructionMap.get(Label).formate;
    }

    public String getOppCode(String Label) {
        return instructionMap.get(Label).oppCode;
    }

    public Integer getNumberOfRegister(String Label) {
        return instructionMap.get(Label).numberOfRegister;
    }

    public Integer getNumberOfRegister2(String Label) {
        return instructionMap.get(Label).numberOfRegister2;
    }

    public boolean Exists (String Label)
    {
        return instructionMap.containsKey(Label);
    }

}
