package generate;

import java.util.*;

/**
 * <p>Title: Expression</p>
 *
 * <p>Description: Expression peals off the parenthesis and store the results
 * in temporary local variables</p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * @author Jeffrey Haley
 * @version 1.0
 */
public class Expression {
    public Expression() {
    }

    public String evaluate(Vector exp, int currentPosition, Vector table){
        int size = exp.size();
        String con = ((Token)exp.elementAt(0)).spelling;
        exp.add(0, new Token(40, "("));
        exp.add(exp.size()-3, new Token(41, ")"));
        String temp="";
        Vector subExpression, leftParenPositions = new Vector();
        Generate g = new Generate();
        int amountOfTempVariables=0;

        if(!(size<=3)&&!g.isVar(con)){
            return "bipush "+con;
        }
        for(int i=0; i<exp.size(); i++){
            //Encounter a lparen
            if(((Token)exp.elementAt(i)).kind == Token.LPAREN){
                leftParenPositions.add(Integer.toString(i));
            }
            //Encounter a rparen and then evaluate expression
            else if(((Token)exp.elementAt(i)).kind ==Token.RPAREN){
                int from = Integer.parseInt((String)leftParenPositions.elementAt(leftParenPositions.size()-1));
                subExpression = g.subVector(from+1, i-1, exp);
                temp +=g.gen(subExpression, currentPosition+amountOfTempVariables, table);
                int posi = g.findStorePosition("tempb"+amountOfTempVariables, table);
                    table.add(0,new VariableTable("tempb"+amountOfTempVariables, currentPosition+amountOfTempVariables));
                    exp = g.replace(from, i, "tempb"+amountOfTempVariables, exp);
                leftParenPositions.removeElementAt(leftParenPositions.size()-1);
                if(g.additionORsubtraction(exp))
                    temp += "istore " +
                            String.valueOf(currentPosition + amountOfTempVariables) +
                            "\n";
                amountOfTempVariables++;
                i = from;
            }
        }
        return temp;
    }



}

