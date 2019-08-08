package generate;

import java.util.*;
import java.lang.*;

/**
 * <p>Title: Generate</p>
 *
 * <p>Description: This class was my starting point for the project so the
 * class name is a little deceiving.  Generate simply generates code for
 * given expressions.</p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * @author Jeffrey Haley
 * @version 1.0
 */
public class Generate {
    public Generate() {
    }

    public String gen(Vector exp, int currentLocalPosition, Vector vt){

        int tempVar = 0, tempPosition = 0, amountOfTempVariables=0, start=0;
        boolean loaded = false, justExited = false;
        String instruction1 = "", instruction2 = "", constantAppend, temp="", operation="";

        for(int i=0; i<=(exp.size()-2); i=i+2){

            if (((Token)exp.elementAt(i + 1)).kind == Token.MUL ||
                ((Token)exp.elementAt(i + 1)).kind == Token.DIV) {
                if(!loaded)
                    start = i;

                if (!loaded) {
                    //First operand
                    if (isVar(((Token) exp.elementAt(i)).spelling)) {
                        instruction1 = "iload ";
                        constantAppend = String.valueOf(findStorePosition(((
                                Token) exp.elementAt(i)).spelling, vt));
                    } else {
                        instruction1 = "bipush ";
                        constantAppend = ((Token) exp.elementAt(i)).spelling;
                    }
                    temp += instruction1 + constantAppend + "\n";

                    //Second operand
                    if (isVar(((Token) exp.elementAt(i+2)).spelling)) {
                        instruction2 = "iload ";
                        constantAppend = String.valueOf(findStorePosition((
                                String) ((Token)exp.elementAt(i + 2)).spelling, vt));
                    } else {
                        instruction2 = "bipush ";
                        constantAppend = ((Token) exp.elementAt(i + 2)).spelling;
                    }
                    temp += instruction2 + constantAppend + "\n";

                    //Operation
                    if (((Token)exp.elementAt(i + 1)).kind == Token.MUL) {
                        temp += "imul" + "\n";
                    } else if (((Token) exp.elementAt(i + 1)).kind == Token.DIV) {
                        temp += "idiv" + "\n";
                    }
                    justExited = true;
                    loaded = true;
                }

                else if (loaded) {
                    //Follow operand
                    if (isVar(((Token) exp.elementAt(i + 2)).spelling)) {
                        instruction2 = "iload ";
                        constantAppend = String.valueOf(findStorePosition((
                                String) exp.elementAt(i + 2), vt));
                    } else {
                        instruction2 = "bipush ";
                        constantAppend = ((Token) exp.elementAt(i + 2)).spelling;
                    }
                    temp += instruction2 + constantAppend + "\n";

                    //Operation
                    if (exp.elementAt(i + 1) == "*") {
                        temp += "imul" + "\n";
                    } else if (((Token) exp.elementAt(i + 1)).kind == Token.DIV) {
                        temp += "idiv" + "\n";
                    }

                    justExited = true;

                }

            } else if(justExited){
                loaded = false;
                justExited = false;
                if(additionORsubtraction(exp)){
                    temp += "istore " +
                            String.valueOf(amountOfTempVariables +
                                           currentLocalPosition) + "\n";
                    vt.add(new VariableTable("tempa" +
                                             String.valueOf(
                            amountOfTempVariables + currentLocalPosition),
                                             amountOfTempVariables +
                                             currentLocalPosition));

                    exp = replace(start, i,
                                  "tempa" +
                                  String.valueOf(amountOfTempVariables + currentLocalPosition),
                                  exp);

                    amountOfTempVariables++;
                    i = start;
                    operation += ((Token) exp.elementAt(i + 1)).spelling;
                }
            }

        }

        for(int i=0; i<exp.size()-2; i=i+2){
            if (((Token) exp.elementAt(1)).kind == Token.ADD||((Token) exp.elementAt(1)).kind == Token.SUB){
                if (i == 0) {
                    if (isVar(((Token) exp.elementAt(i)).spelling)) {
                        instruction1 = "iload ";
                        constantAppend = String.valueOf(findStorePosition(
                                ((Token)exp.elementAt(i)).spelling, vt));

                    } else {
                        instruction1 = "bipush ";
                        constantAppend = ((Token) exp.elementAt(i)).spelling;
                    }
                    temp += instruction1 + constantAppend + "\n";
                } else if (i < exp.size() - 2) {
                    if (isVar(((Token)exp.elementAt(i)).spelling)) {
                        instruction1 = "iload ";
                        constantAppend = String.valueOf(findStorePosition(((
                                Token) exp.elementAt(i)).spelling, vt));
                    } else {
                        instruction1 = "bipush ";
                        constantAppend = ((Token) exp.elementAt(i)).spelling;
                    }
                    temp += instruction1 + constantAppend + "\n";
                    if (((Token) exp.elementAt(i - 1)).kind == Token.ADD) {
                        temp += "iadd" + "\n";
                    } else if (((Token)exp.elementAt(i - 1)).kind == Token.SUB) {
                        temp += "isub" + "\n";
                    }

                }

            }
        }
        return temp;
    }

    protected boolean isVar(String var){
        return !Character.isDigit(var.charAt(0));
    }

    protected int findStorePosition(String var, Vector vt){
        for(int i=0; i<vt.size(); i++){
            if (((VariableTable)vt.elementAt(i)).symbol.equals(var))
                return ((VariableTable)vt.elementAt(i)).position;
        }
        return -1;
    }

    public Vector replace(int start, int end, String replacement, Vector v){
        v.add(start, new Token(50, replacement));
        for(int i=start+1; i<=end+1; i++){
            v.removeElementAt(start+1);
        }
        return v;
    }

    public Vector subVector(int from, int to, Vector v){
        Vector newV = new Vector();
        for(int i=from; i<=to; i++){
            newV.add(v.elementAt(i));
        }
         newV.add(new Token(-1, "")); newV.add(new Token(-1, ""));  newV.add(new Token(-1, ""));
        return newV;
    }

    protected boolean additionORsubtraction(Vector v){
        for(int i=0; i<v.size(); i++){
            if(((Token)v.elementAt(i)).kind == Token.ADD || ((Token)v.elementAt(i)).kind == Token.SUB)
                return true;
        }
        return false;
    }



}
