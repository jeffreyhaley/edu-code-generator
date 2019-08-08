package generate;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class VariableTable {
    public String symbol;
    public int position;

    public VariableTable() {
    }

    public VariableTable(String s){
        symbol = s;
        position = -1;
    }


    public VariableTable(String s, int p){
        symbol = s;
        position = p;
    }

}
