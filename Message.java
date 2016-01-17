import java.util.*;

public class Message{
    public Header messageHeader;
    public String message="";

    public Message(ArrayList<String> messageList){
	int newLine=findBreak(messageList)+1;
	for(int i=newLine;i<messageList.size()-1;i++){
	    message+=messageList.get(i)+"\n";
	}
	messageList.removeRange(newLine,messageList.size());
	messageHeader=new Header(messageList);
    }
    
    public int findBreak(ArrayList<String> messageList){
	for (int i=0;i<messageList.size();i++){
	    if (messageList.get(i).equals("\n")||messageList.get(i).equals("\r\n")){
		return i;
	    }
	}
    }

    public String getMessage(){
	return message;
    }
    
}
