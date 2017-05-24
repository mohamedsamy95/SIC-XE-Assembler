package sample.mainbody;

import java.util.HashMap;

public class SymbolicTable
{
private HashMap<String ,SymbolData> rowInformmation;
    private static SymbolicTable table=null;
    public void setRow (String Label,int value ,int address) {
        rowInformmation.put(Label,new SymbolData(value,address));
    }

    public HashMap<String, SymbolData> getRowInformmation() {
        return rowInformmation;
    }

    public void setRow(String Label, int address) {
        rowInformmation.put(Label,new SymbolData(address));
    }


    private SymbolicTable() {
        rowInformmation=new HashMap<>();
    }

    public static SymbolicTable getTable() {
        if(table == null) {
            table=new SymbolicTable();
        }
        return table;
    }
    private class SymbolData{
private  int   value =0;
private  int   address=0;
private  String base =null;

        public SymbolData(int value, int address) {
            this.value = value;
            this.address = address;
        }

        public SymbolData(int address) {
            this.address = address;
        }

        @Override
        public String toString() {
            return "The Address is "+address+" The value is " +value+" The base is " +base;
        }
    }

    public int getValue(String label) {
        return rowInformmation.get(label).value;
    }
    public int getAddress(String label) {
        return rowInformmation.get(label).address;
    }
    public  void setAddress(String label,int pc){
        rowInformmation.get(label).address=pc;
    }

    public  void setValue(String label,int value){
        rowInformmation.get(label).value=value;
    }
    public String getBase() {
        return rowInformmation.get("Bse").base;
    }
    public  void setBase(String state){
        rowInformmation.get("Bse").base=state;
    }
    public boolean exists(String Label)
    {
        return rowInformmation.containsKey(Label);
    }

}

