package sample.mainbody;
import java.util.ArrayList;
import java.util.HashMap;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class Expression {

    private String expression;
    private int NumberOfOperands;
    private String[] Operands;
    private String NewExpression;
    private ArrayList<Character> Operators;
    private int Value;
    private final HashMap<String,Integer> Labels;
    private String PC;
    private boolean AorR=true;
    private boolean isLegal=true;

    public Expression (String expression , HashMap<String,Integer> labels , String PC)
    {
        Operators = new ArrayList<Character>();
        Labels = labels;
        this.expression=expression;
        this.PC=PC;
        Labels.put("*", Integer.valueOf(PC));
        setNumberOfOperands();
        split();
        if(checkLegal())
        {
            setNewExpression();
            try {
                getExpressionValue();
            } catch (ScriptException e) {
                e.printStackTrace();
            }
            if(Value >=0 && Value <= (Math.pow(2, 15)-1) ) //Memory bounds
                isLegal=true;
            else
                isLegal=false;
        }

    }

    private boolean checkLegal()
    {
        if(expression.length()>1)
        {

            for (int i=0 ; i<expression.length() ; i++)
            {
                if (expression.charAt(i)=='/' || expression.charAt(i)=='*')
                    return false;
            }
        }
        if (NumberOfOperands<Operators.size()+1)
            return  false;
        int Labels=0;
        int Numbers=0;
        int Plus=1;
        int Minus=0;
        ArrayList<Boolean> Brackets = new ArrayList<Boolean>();
        boolean isBracket = false;

        for (String operand : Operands)
        {
            int i=0;
            if(operand.charAt(0)=='(')
            {
                i++;
                isBracket=true;
            }
            else if(operand.endsWith(")"))
            {
                isBracket=false;
            }
            if(isBracket)
                Brackets.add(true);
            else
                Brackets.add(false);
            char c= operand.charAt(i);
            if (isNumber(c))
                Numbers++;
            else if (isLetter(c))
                Labels++;
        }
        if (Labels==1&& Numbers==0)
        {
            AorR=false;
            return true;
        }
        else if (Numbers==1 && Labels==0)
        {
            AorR=true;
            return true;
        }
        boolean first=true;
        boolean Negative = false;
        for (int i=0 ; i<Operators.size() ; i++)
        {
            if(Negative)
            {
                if (Operators.get(i)=='+')
                    Minus++;
                else if (Operators.get(i)=='-')
                    Plus++;
            }
            else
            {
                if (Operators.get(i)=='+')
                    Plus++;
                else if (Operators.get(i)=='-')
                    Minus++;
            }
            if(Brackets.get(i+1) && first)
            {
                first=false;
                if(Operators.get(i)=='-')
                {
                    Negative=true;
                }
            }

            if (!Brackets.get(i+1))
            {
                first=true;
                Negative=false;
            }

        }

        if(Numbers==0)
        {
            if( (NumberOfOperands%2==0) && (Plus==Minus) ){
                AorR=true;
                return  true;
            }
            else if ( (NumberOfOperands%2==1) && (Plus==Minus+1) )
            {
                AorR=false;
                return  true;
            }
        }
        else {
            if (Labels==0)
            {
                AorR=true;
                return  true;
            }
            else
            {
                if(isNumber(Operands[0].charAt(0)))
                    Plus--;
                for (int j=0 ; j<Operators.size() ; j++)
                {
                    int k=0;
                    if(Operands[j+1].charAt(0)=='(')
                        k++;
                    if(isNumber(Operands[j+1].charAt(k)))
                    {
                        if(Operators.get(j)=='+')
                            Plus--;
                        else if (Operators.get(j)=='-')
                            Minus--;
                    }
                }
                if( (Labels%2==0) && (Plus==Minus) ){
                    AorR=true;
                    return  true;
                }
                else if ( (Labels%2==1) && (Plus==Minus+1) )
                {
                    AorR=false;
                    return  true;
                }


            }

        }
        return  false;
    }

    boolean isNumber(char c)
    {
        if (c>='1' && c<='9')
            return true;
        return false;
    }
    boolean isLetter(char c)
    {
        if ((c>='A' && c<='Z') || (c>='a' && c<='z'))
            return true;
        return false;
    }
    private void setNumberOfOperands()
    {
        NumberOfOperands = 1;
        for (int i=0 ; i<expression.length() ; i++)
        {
            if(i>0)
                if (expression.charAt(i)=='+' || expression.charAt(i)=='-' )
                {
                    NumberOfOperands++;
                    Operators.add(expression.charAt(i));
                }
        }

    }

    private void split()
    {
        if (expression.contains("+") && expression.contains("-"))
            Operands = expression.split("[+-]+");

        else if (expression.contains("+"))
            Operands = expression.split("[+]+");

        else if (expression.contains("-"))
            Operands = expression.split("[-]+");
        else
        {
            Operands = new String[1];
            Operands[0] = new String(expression);
        }

    }


    private void setNewExpression()
    {
        String operand;
        if(Operands[0].startsWith("[(]"))
            operand=new String(Operands[0].substring(1));
        else if (Operands[0].endsWith("[)]"))
            operand= new String(Operands[0].substring(0, Operands[0].length()-2));
        else
            operand = new String(Operands[0]);
        if(operand.charAt(0)>='1' && operand.charAt(0)<='9')
            NewExpression = new String(operand);
        else
            NewExpression = new String(Labels.get(operand).toString());
        if (NumberOfOperands>1)
        {
            NewExpression = NewExpression + Operators.get(0);
            for (int i=1 ; i<Operands.length ; i++)
            {
                if(Operands[i].startsWith("("))
                {
                    operand=new String(Operands[i].substring(1));
                    if(operand.charAt(0)>='1' && operand.charAt(0)<='9')
                        NewExpression = NewExpression + "(" + operand;
                    else
                        NewExpression = NewExpression + "(" + Labels.get(operand).toString();
                }
                else if (Operands[i].endsWith(")"))
                {
                    operand= new String(Operands[i].substring(0, Operands[i].length()-1));
                    if(operand.charAt(0)>='1' && operand.charAt(0)<='9')
                        NewExpression = NewExpression + operand + ")";
                    else
                        NewExpression = NewExpression + Labels.get(operand).toString() + ")";
                }
                else
                {
                    operand = new String(Operands[i]);
                    if(operand.charAt(0)>='1' && operand.charAt(0)<='9')
                        NewExpression = NewExpression + operand;
                    else
                        NewExpression = NewExpression + Labels.get(operand).toString();
                }
                if(i>=Operators.size())
                    break;
                NewExpression = NewExpression + Operators.get(i);
            }
        }

    }
    private void getExpressionValue() throws ScriptException
    {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        Value = (int) engine.eval(NewExpression);
    }
    public int getValue()
    {
        return Value;
    }
    public boolean AbsoluteOrRelative()
    {
        return AorR;
    }

    public boolean isLegal()
    {
        return isLegal;
    }

}
