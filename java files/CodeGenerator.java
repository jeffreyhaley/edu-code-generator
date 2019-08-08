package generate;

import java.util.*;
import java.io.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: This class contains the main method and handles the code
 * bulk of the code generation. </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CodeGenerator {

    static FileWriter outfile;
    static BufferedWriter out;
    Vector table = new Vector();
    Generate g = new Generate();
    Expression e = new Expression();
    String theClassName;
    int currentPosition=2, label=0, curly=0;
    public static Vector code = new Vector();
    String returns="ireturn";

    public CodeGenerator(String filename) {
        try {
            outfile = new FileWriter(filename);
            out = new BufferedWriter(outfile);
        }
        catch (IOException e) {
            System.out.println(e);
        }

    }

    public void generateCode(Vector code){
        boolean main = false;

        for(int i=0; i<code.size(); i++){
            //Write Class name and initializing information
            if(((Token)code.elementAt(i)).kind == Token.CLASS){
                theClassName = ((Token)code.elementAt(i+1)).spelling;
               initialize(out, theClassName);
               i++;
            }

            //Store variables in VariableTable
            else if(((Token)code.elementAt(i)).kind == Token.INT && ((Token)code.elementAt(i+1)).kind == Token.IDENTIFIER &&
                    ((Token)code.elementAt(i+2)).kind != Token.LPAREN){
                addToTable(((Token)code.elementAt(i + 1)).spelling, table);
                i++;
            }

            //Write method initializing information
            else if(((Token)code.elementAt(i)).kind == Token.INT && ((Token)code.elementAt(i+1)).kind == Token.IDENTIFIER &&
                    ((Token)code.elementAt(i+2)).kind == Token.LPAREN){
                String methodName = ((Token)code.elementAt(i+1)).spelling;
                String methodCode="";
                int currentMethodPosition=0;
                int currentPosition=0;

                //MAIN
                if(methodName.equals("main")){
                    returns="return";
                    try{
                        out.write(";Main Method\n.method public static main([Ljava/lang/String;)V\n"
                                  +".limit stack 30\n.limit locals 30\n");
                    }
                    catch(IOException e){
                        System.out.println(e);
                    }
                }

                else{
                    int numOfParameters = 0;
                    i += 2;
                    while (((Token) code.elementAt(i)).kind != Token.RPAREN) {
                        if (((Token) code.elementAt(i)).kind ==
                            Token.IDENTIFIER) {
                            numOfParameters++;
                            table.add(new VariableTable(((Token) code.elementAt(
                                    i)).spelling, currentMethodPosition));
                            currentMethodPosition++;
                            //currentPosition++;

                        }
                        i++;
                    }
                    initializeMethod(out, methodName, numOfParameters, methodCode);
                }
            }

            //Assignment; Write expression code to file and update  store
            //position of expression result
            else if (((Token)code.elementAt(i)).kind == Token.IDENTIFIER && ((Token)code.elementAt(i+1)).kind == Token.BECOMES &&
                     (((Token)code.elementAt(i+2)).kind != Token.IDENTIFIER || ((Token)code.elementAt(i+3)).kind != Token.LPAREN)){
                int from = i+2;

                String var = ((Token)code.elementAt(i)).spelling;
                int position = g.findStorePosition(var, table);

                while(((Token)code.elementAt(i)).kind != Token.SEMICOLON){
                    i++;
                }
                int to = i;
                String expressionCode="";
                if((to-from)!=1){
                    Vector vtemp = g.subVector(from, to-1, code);
                    expressionCode = e.evaluate(vtemp, currentPosition, table);
                    expressionCode += "istore "+String.valueOf(position)+"\n";
                }
                else{
                    if (!g.isVar(((Token) code.elementAt(i - 1)).spelling)) {

                        expressionCode += "bipush " +
                                ((Token) code.elementAt(i - 1)).spelling + "\n";
                        expressionCode += "istore " + String.valueOf(position) +
                                "\n";
                    }

                    else {
                        int pos = g.findStorePosition(((Token) code.elementAt(i -
                                1)).spelling, table);
                        expressionCode += "iload " + String.valueOf(pos) + "\n";
                        expressionCode += "istore " + String.valueOf(position) +
                                "\n";
                    }
                }
                initializeExpression(out, expressionCode);
                currentPosition++;
            }


            //Method Call;

            else if (((Token)code.elementAt(i)).kind == Token.IDENTIFIER && ((Token)code.elementAt(i+1)).kind == Token.BECOMES &&
                     ((Token)code.elementAt(i+2)).kind == Token.IDENTIFIER && ((Token)code.elementAt(i+3)).kind == Token.LPAREN){
                String loadCode="";
                String var = ((Token)code.elementAt(i)).spelling;

                int position = g.findStorePosition(var, table);

                String methodName = ((Token)code.elementAt(i+2)).spelling;
                int numOfParameters=0;
                i+=3;
                while(((Token)code.elementAt(i)).kind != Token.RPAREN){
                    if(((Token)code.elementAt(i)).kind == Token.IDENTIFIER){
                        int pos = g.findStorePosition(((Token)code.elementAt(i)).spelling, table);
                        loadCode += "iload "+String.valueOf(pos)+"\n";
                        numOfParameters++;

                    }
                    i++;
                }

                initializeMethodCall(out, loadCode, methodName, numOfParameters, position);
            }

            //Return
            else if(((Token)code.elementAt(i)).kind == Token.RETURN){
                i++;
                int from =i;
                while(((Token)code.elementAt(i)).kind != Token.SEMICOLON){
                   i++;
               }
               int to=i;
               String expressionCode="";
               if((to-from)!=1){
                    Vector vtemp = g.subVector(from+1, to-2, code);
                    expressionCode = e.evaluate(vtemp, currentPosition, table);
                    currentPosition++;
                }
                else{
                    int pos = g.findStorePosition(((Token)code.elementAt(i-1)).spelling, table);
                    expressionCode += "iload "+String.valueOf(pos)+"\n";
                }
                expressionCode += returns+"\n.end method\n\n\n";
                try{out.write(expressionCode);}
                catch(IOException e){}
            }

            //Print
            else if(((Token)code.elementAt(i)).kind == Token.PRINT){
                boolean cons = false;
                String printCode="";
                String printOut = "\n;Load Print Object\ngetstatic java/lang/System/out Ljava/io/PrintStream;\n"
                            + "\n\n";

                //Its a print string
                if(((Token)code.elementAt(i+1)).kind == Token.QUOTE){
                    i+=2;
                    while(((Token)code.elementAt(i)).kind != Token.QUOTE){
                        printCode += ((Token)code.elementAt(i)).spelling;
                        i++;
                    }
                    printOut += "ldc \""+printCode+"\"\n";

                }

                //its a print expression
                else{
                    cons = true;
                    int from =i+1;
                    while(((Token)code.elementAt(i)).kind != Token.SEMICOLON){
                        i++;
                    }
                    int to = i;

                    String expressionCode="";
                    if((to-from)!=1){
                        Vector vtemp = g.subVector(from, to-1, code);
                        expressionCode = e.evaluate(vtemp, currentPosition, table);
                        currentPosition++;
                    }
                    else{
                        if(!g.isVar(((Token)code.elementAt(i-1)).spelling)){
                            expressionCode += "astore 0\nbipush "+((Token)code.elementAt(i-1)).spelling+"\n";
                        }
                        else{
                            int pos = g.findStorePosition(((Token) code.
                                    elementAt(i - 1)).spelling, table);
                            expressionCode += "astore 0\niload " + String.valueOf(pos) +
                                    "\n";
                        }
                    }
                    printOut +=expressionCode;
                }



                try{
                    if(!cons){
                        out.write(printOut +
                                "\n;call the PrintStream.println() method.\n"
                                  +
                                "invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n\n");
                    }
                    else{
                        out.write(printOut +
                                "invokestatic java/lang/String/valueOf(I)Ljava/lang/String;\n"
                                  +
                                "astore 1\naload 0\naload 1\n\n;call the PrintStream.println() method.\n"
                                  +
                                "invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n\n");

                    }
                }
                catch(IOException e){}
            }
            //if statement
            else if(((Token)code.elementAt(i)).kind == Token.IF){
                boolean gt;
                i++;
                String ifCode="", ifCode2="";
                int from =i;
                while(((Token)code.elementAt(i)).kind != Token.GT && ((Token)code.elementAt(i)).kind != Token.LT){
                    i++;
                }
                int to=i;
                if(((Token)code.elementAt(i)).kind == Token.GT){
                    gt = true;
                } else{
                    gt=false;
                }

                Vector sub = g.subVector(from+1, to-1, code);
                ifCode += e.evaluate(sub, currentPosition, table)+"\nistore "+currentPosition+"\n";
                from =i;
                while(((Token)code.elementAt(i)).kind != Token.LCURLY){
                    i++;
                }
                to=i-1;
                curly++;
                sub = g.subVector(from+1, to-1, code);
                ifCode += e.evaluate(sub, currentPosition, table)+"\niload "+currentPosition+"\nisub\n";
                currentPosition++;
                label++;
                if(gt){
                    ifCode += "iflt label"+label+"\n";
                }
                else{
                    ifCode += "ifgt label"+label+"\n";
                }

                try{
                    out.write(ifCode);
                }
                catch(IOException e){}

            }
            else if(((Token)code.elementAt(i)).kind == Token.RCURLY && curly>=1){
                curly--;
                try{
                    out.write("label"+label+":\n");
                    label--;
                }
                catch(IOException e){}

            }


        }
        try{
            out.close();
        }
        catch(IOException e){}



    }

    private void initialize(BufferedWriter out, String className){
        try{
            out.write(".class public "+className+"\n.super java/lang/Object\n"
                      +".method public <init>()V\naload 0\ninvokenonvirtual "
                      +"java/lang/Object/<init>()V\nreturn\n.end method\n");

        }
        catch(IOException e){
            System.out.println(e);
        }
    }

    private void addToTable(String variable, Vector table){
        table.add(new VariableTable(variable, currentPosition));
        currentPosition++;
    }

    private void initializeMethod(BufferedWriter out, String methodName, int numParam, String methodCode){
        String parameters = "";
        for(int i=0; i<numParam; i++){
            parameters += "I";
        }
        try{
           out.write("\n\n.method static "+methodName+"("+parameters+")I\n"
                     +".limit stack 30\n.limit locals 30\n\n;Variable initialization\n"
                     +methodCode+"\n");
       }
       catch(IOException e){
           System.out.println(e);
       }
    }

    private void initializeExpression(BufferedWriter out, String expressionCode){
        try{
            out.write(expressionCode);
        }
        catch(IOException e){
            System.out.println(e);
        }
    }

    private void initializeMethodCall(BufferedWriter out, String loadCode, String methodName, int numParam, int position){
        String parameters = "";
        for(int i=0; i<numParam; i++){
            parameters += "I";
        }

        try{
            out.write(loadCode+"\ninvokestatic "+theClassName+"/"+methodName+"("
                    +parameters+")I\nistore "+position);
        }
        catch(IOException e){
            System.out.println(e);
        }
    }

    private void updateTable(String variable, int position, Vector table){
        int i=0;
        while(i<table.size()){
            if(((VariableTable)table.elementAt(i)).symbol == variable){
                ((VariableTable)table.elementAt(i)).position = position;
            }
            i++;
        }
    }


    public static void main(String[] args){
        CodeGenerator c = new CodeGenerator(args[0].substring(0,args[0].length()-5)+".j");
        try{
            CharReader charReader = new CharReader(args[0]);
            Scanner s = new Scanner(charReader);
            Vector cd = s.scan();
            c.generateCode(cd);

        }
        catch(IOException e){System.out.println(e);}

    }





}
