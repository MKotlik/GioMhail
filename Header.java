import java.util.*;
public class Header{
    public HashMap<String,String> header;
    
    public class Header(String[] top){
	for(int i=0;i<top.length;i++){
	    if (top[i].substring(0,3).equals("Dat")){
		header.put("Date", top[i].substring(6,top[i].length));
	    }else if (top[i].substring(0,3).equals("Fro")){
		header.put("From", top[i].substring(6,top[i].length));		
	    }else if (top[i].substring(0,3).equals("To:")){
		header.put("To", top[i].substring(4,top[i].length));
	    }else if (top[i].substring(0,3).equals("Sub")){
		header.put("Subject", top[i].substring(8,top[i].length));
	    }else if (top[i].substring(0,3).equals("CC:")){
		header.put("CC", top[i].substring(4,top[i].length));
	    }else if (top[i].substring(0,3).equals("BCC")){
		header.put("BCC", top[i].substring(5,top[i].length));
	    }
	}
    }
}
