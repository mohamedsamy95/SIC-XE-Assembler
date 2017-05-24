package sample.mainbody;
public class ObjectCode {

    private String mnemonic;
    private String operand;
    private char AddressingMode;
    private int numberOfOperands;
    private int numberOfRegisters;
    private int format;
    private String objectCode;
    private InstructionFormate InstructionSet;
    private int pc;
    private int base;
    private String [] Operands;
    private SymbolicTable SymTab;
    private final static String ZEROES3 = "000";
    private final static String ZEROES5 = "00000";
    private int  locationMnemonic;
    private String []data;
    private final String indexed;
    private int  xbpe =0;
    public ObjectCode (String [] data, int locationMnemonic, int format, int base ,String indexed )
    {
        this.data=data;
        mnemonic=data[locationMnemonic];
        pc=Integer.valueOf(data[0],16)+format;
        this.base=base;
        this.locationMnemonic=locationMnemonic;
        this.format=format;
        this.indexed=indexed;
          numberOfOperands=InstructionFormate.getInstructionTable().getNumberOfRegister(mnemonic);
          numberOfRegisters=InstructionFormate.getInstructionTable().getNumberOfRegister2(mnemonic);
        InstructionSet = PathOne.getInstructionSet();
        setInfo();


    }
    private void setInfo() {
        switch (format) {
            case 0:
                            if (mnemonic.equals("START") || mnemonic.equals("END"))
                                     break;
                            if (mnemonic.equals("BYTE") || mnemonic.equals("WORD")) {
                                     objectCode = Integer.toHexString(SymbolicTable.getTable().getValue(data[1]));
                            if (mnemonic.equals("BYTE")&&objectCode.length()%2!=0)
                                     objectCode="0"+objectCode;
                            while((mnemonic.equals("WORD")&&objectCode.length()<6))
                                      objectCode="0"+objectCode;

                            }
                            if (mnemonic.equals("RESB") || mnemonic.equals("RESW")||mnemonic.equals("USE"))
                                      objectCode = "Sep";
                            if (mnemonic.equals("EXTDEF"))
                                      objectCode = "DEF";
                             if (mnemonic.equals("EXTREF"))
                                objectCode = "REF";
                               if (mnemonic.equals("CSECT"))
                                   objectCode = "sec";

                break;
            case 1:
                objectCode = InstructionFormate.getInstructionTable().getOppCode(mnemonic);
                break;
            case 2:
                if (numberOfOperands == 2) {
                    objectCode = InstructionFormate.getInstructionTable().getOppCode(mnemonic)
                            + registers(data[data.length - 2])
                            + registers(data[data.length - 1]);
                } else objectCode = InstructionSet.getOppCode(mnemonic) + registers(data[data.length - 1]) + "0"
                        ;//check if shift greater than F
                break;
            case 3:
                int Address;
                String label;
                boolean  immediate=false;
                boolean value=false;
                if (numberOfOperands==1)
                {
                     if(indexed.equals("X"))xbpe=8;

                    if (reservedchar(data[locationMnemonic + 1].charAt(0))) {
                        label = data[locationMnemonic + 1].substring(1);
                        try{
                            Address = SymbolicTable.getTable().getAddress(label);
                        }catch (NullPointerException EX){
                            Address = Integer.parseInt(label);
                            value=true;
                        }
                        if(data[locationMnemonic+1].charAt(0)=='#'&&value) {
                            xbpe+= handleX(indexed);
                            immediate=true;
                        }else
                        xbpe+= handleX(indexed) + handleB(Address) + handleP(Address);
                    }else{
                        Address = SymbolicTable.getTable().getAddress(data[data.length-1]);
                        xbpe+= handleX( indexed) + handleB(Address) + handleP(Address);
                    }

                    if (handleP(Address)!=0)
                    {
                        String displacement = immediate? Integer.toHexString(Address) :Integer.toHexString(Address - pc);
                        if(displacement.length()>3) {
                            displacement= displacement.substring(displacement.length()-3);
                        }

                        objectCode = Integer.toHexString((Integer.parseInt(InstructionSet.getOppCode(mnemonic), 16)
                                + handleNI(data[data.length-1])))+ Integer.toHexString(xbpe)+""+(displacement.length()< 3 ? ZEROES3.substring(displacement.length())+ displacement
                                : displacement);
                        while(objectCode.length()<=5)
                            objectCode="0"+objectCode;

                    }
                    else if (handleB(Address)!=0)
                    {
                        String displacement = Integer.toHexString(Address - base);
                        objectCode = Integer.toHexString((Integer.parseInt(InstructionSet.getOppCode(mnemonic), 16)
                                + handleNI(data[data.length-1])))
                                + Integer.toHexString(xbpe) +
                                (displacement.length()< 3 ? ZEROES3.substring(displacement.length())+ displacement
                                        : displacement) ;
                    }else if(value){
                        String displacement=Address+"";
                        while(displacement.length()<3){
                            displacement="0"+displacement;
                        }
                        objectCode =Integer.toHexString((Integer.parseInt(InstructionSet.getOppCode(mnemonic), 16)
                                + handleNI(data[data.length-1])))
                                + Integer.toHexString(xbpe)+displacement;
                    }

                    else{
                        objectCode = "**********ERROR! Displacement exceeds limit**********";
                        System.err.println(objectCode);
                    }

                }
                else objectCode = Integer.toHexString(Integer.parseInt(InstructionSet.getOppCode(mnemonic), 16)+3)
                        + "0000";


             break;
            case 4:

                boolean val=false;
                int Addres;
                if (reservedchar(data[locationMnemonic + 1].charAt(0))) {
                    label = data[locationMnemonic + 1].substring(1);
                    try{
                        Addres = SymbolicTable.getTable().getAddress(label);
                    }catch (NullPointerException EX){
                        Addres = Integer.parseInt(label);
                    }
                }else{
                    Addres = SymbolicTable.getTable().getAddress(data[data.length-1]);
                }

                    xbpe = 1 + handleX(indexed);
                    String add=Integer.toHexString(Addres);
                    while(add.length()<5)
                        add="0"+add;
                    objectCode = Integer.toHexString((Integer.parseInt(InstructionSet.getOppCode(mnemonic), 16)
                                + handleNI(data[locationMnemonic+1].charAt(0)+"")))
                                + Integer.toHexString(xbpe) +add;



                break;
        }
        }
    private boolean reservedchar(char c) {
        switch (c){
            case '@':case '#':
                return true;
            default:
                return false;
        }}
    private int registers(String str){
        switch (str){
            case "A":return 0;
            case "X":return 1;
            case "L":return 2;
            case "B":return 3;
            case "S":return 4;
            case "T":return 5;
            case "F":return 6;
            case "SW":return 9;

        }
          return -1;
    }
    private int handleNI (String Operand)
    {
        if (Operand.charAt(0)=='@') return 2;
        else if (Operand.charAt(0)=='#') return 1;
        else return 3;
    }
    private int handleX (String indexed)
    {
        if(indexed!=null)
            if(data[data.length-1].charAt(0) == 'X') return 8;
        return 0;
    }
    private int handleP(int TA) {
        int displacement =  (TA) - pc;
        if (displacement >= ((-1)*2048) && displacement <= 2047) return 2;
        return 0;
    }

    private int handleB(int TA) {
        if (handleP(TA) == 0)
        {
            int displacement = (TA) - base;
            if (displacement>= 0 && displacement <= 4095 ) return 4;
        }
        return 0;
    }

    public String getObjectCode()
    {
        return objectCode;
    }

}
