import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

//=====================================================================
class DictEntry2 {

    public int doc_freq = 0; // number of documents that contain the term
    public int term_freq = 0; //number of times the term is mentioned in the collection
    public HashSet<Integer> postingList;

    DictEntry2() {
        postingList = new HashSet<Integer>();
    }
}

//=====================================================================
class Index2 {
    
    //--------------------------------------------
    Map<Integer, String> sources;  // store the doc_id and the file name
    HashMap<String, DictEntry2> index; // THe inverted index
    //--------------------------------------------

    Index2() {
        sources = new HashMap<Integer, String>();
        index = new HashMap<String, DictEntry2>();
    }

//---------------------------------------------
    public void printDictionary() {
        Iterator it = index.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            DictEntry2 dd = (DictEntry2) pair.getValue();
            HashSet<Integer> hset = dd.postingList;// (HashSet<Integer>) pair.getValue();
            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
            Iterator<Integer> it2 = hset.iterator();
            while (it2.hasNext()) {
                System.out.print(it2.next() + ", ");
            }
            System.out.println("");
            //it.remove(); // avoids a ConcurrentModificationException
        }
        System.out.println("------------------------------------------------------");
        System.out.println("*** Number of terms = " + index.size());
    }

    //-----------------------------------------------
    public void buildIndex(String[] files) {
        int i = 0;
        for (String fileName : files) {
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                sources.put(i, fileName);
                String ln;
                while ((ln = file.readLine()) != null) {
                    String[] words = ln.split("\\W+");
                    for (String word : words) {
                        word = word.toLowerCase();
                        // check to see if the word is not in the dictionary
                        if (!index.containsKey(word)) {
                            index.put(word, new DictEntry2());
                        }
                        // add document id to the posting list
                        if (!index.get(word).postingList.contains(i)) {
                            index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term 
                            index.get(word).postingList.add(i); // add the posting to the posting:ist
                        }
                        //set the term_fteq in the collection
                        index.get(word).term_freq += 1;
                    }
                }
                // printDictionary();
            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            i++;
        }
    }


    public HashSet<Integer> find(String phrase) {

        HashSet<Integer> answer =new HashSet<Integer>();
        String[] words = phrase.split("\\W+");
        try {
            HashSet<Integer> res = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
            answer=normal(res);
        } catch (Exception e) {
            System.out.println("Not found");
        }
        return answer;
    }

    HashSet<Integer> normal(HashSet<Integer> pL1)
    {
return pL1;
    } 

    HashSet<Integer> And(HashSet<Integer> pL1, HashSet<Integer> pL2) {
        HashSet<Integer> answer = new HashSet();
        Iterator it1 = pL1.iterator();
        Iterator it2 = pL2.iterator();
 
        Integer n1 = (Integer) it1.next();
        Integer n2 = (Integer) it2.next();
 
        while(n1 != null && n2 != null) 
            {
            if(n1.compareTo(n2) == 0) 
                {
                answer.add(n1);
                n1 = (it1.hasNext())? (Integer) it1.next(): null;
                n2 = (it2.hasNext())? (Integer) it2.next(): null;
            }
            else if(n1.compareTo(n2) < 0) 
                {
                n1 = (it1.hasNext())? (Integer) it1.next(): null;
            }
            else 
                {
                n2 = (it2.hasNext())? (Integer) it2.next(): null;
            } 
        }
        return answer;
    }

    /*
    HashSet<Integer> And(HashSet<Integer> pL1, HashSet<Integer> pL2) {
        HashSet<Integer> answer = new HashSet<Integer>();
        Iterator<Integer> p1 = pL1.iterator();
        Iterator<Integer> p2 = pL2.iterator();
        int docID1 = 0, docID2 = 0;
        if (p1.hasNext()) {
            docID1 = p1.next();
        }
        if (p2.hasNext()) {
            docID2 = p2.next();
        }
        while (p1.hasNext() && p2.hasNext()) {
            if (docID1 == docID2) {
                answer.add(docID1);
                docID1 = p1.next();
                docID2 = p2.next();
            } else if (docID1 < docID2) {
                if (p1.hasNext()) {
                    docID1 = p1.next();
                } else {
                    return answer;
                }
            } else {
                if (p2.hasNext()) {
                    docID2 = p2.next();
                } else {
                    return answer;
                }
            }
        }
        if (docID1 == docID2) {
            answer.add(docID1);
        }
        return answer;
    }*/
    HashSet<Integer> OR(HashSet<Integer> pL1, HashSet<Integer> pL2) 
    {
        HashSet<Integer> answer = new HashSet<Integer>();
        for(int j:pL1)
{

    answer.add(j);
}
for(int i:pL2)
{

    answer.add(i);
}

            return answer;
    }
    
    
 
   public  HashSet<Integer> Not(HashSet<Integer> pL1) 
    {
        HashSet<Integer>result=new HashSet<Integer>();
for (int i : sources.keySet())
{
 result.add(i);
}       
         for(int j:pL1)
{
if(result.contains(j))
    result.remove(j);
}
return result;
    }
    public HashSet<Integer> Anding(String phrase) { // 2 term phrase  2 postingsLists
        String[] words = phrase.split("\\W+");
                HashSet<Integer> answer = new HashSet<Integer>();

        try {
            HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
            HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
            answer = And(pL1, pL2);
        } catch (Exception e) {
            System.out.println("Not found");
        }
        return answer;
    }
    public  HashSet<Integer> AndingNot(String phrase) { // 2 term phrase  2 postingsLists
                HashSet<Integer> answer = new HashSet<Integer>();
        String[] words = phrase.split("\\W+");
        try {
            HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
            HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
             answer = And(pL1,Not(pL2));
        } catch (Exception e) {
            System.out.println("Not found");
        }
        return answer;
    }
    public  HashSet<Integer> NotAndingNot(String phrase) { // 2 term phrase  2 postingsLists
                HashSet<Integer> answer = new HashSet<Integer>();
        String[] words = phrase.split("\\W+");
        try {
            HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
            HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
           answer = And(Not(pL1), Not(pL2));
        } catch (Exception e) {
            System.out.println("Not found");
        }
        return answer;
    }

public HashSet<Integer> NotAnding(String phrase) { // 2 term phrase  2 postingsLists
                HashSet<Integer> answer = new HashSet<Integer>();
        String[] words = phrase.split("\\W+");
        try {
            HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
            HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
           answer = And(Not(pL1), pL2);
        } catch (Exception e) {
            System.out.println("Not found");
        }
        return answer;
    }
     public  HashSet<Integer> Oring(String phrase) { // 2 term phrase  2 postingsLists
                HashSet<Integer> answer = new HashSet<Integer>();
        String[] words = phrase.split("\\W+");
        try {
            HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
            HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
            answer = OR(pL1, pL2);
        } catch (Exception e) {
            System.out.println("Not found");
        }
        return answer;
    }
     
    public HashSet<Integer> OringNot(String phrase) { // 2 term phrase  2 postingsLists
                HashSet<Integer> answer = new HashSet<Integer>();
        String[] words = phrase.split("\\W+");
        try {
            HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
            HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
            answer = OR(pL1,Not (pL2));
        } catch (Exception e) {
            System.out.println("Not found");
        }
        return answer;
    }
    
  
    public  HashSet<Integer> NotOring(String phrase) { // 2 term phrase  2 postingsLists
                HashSet<Integer> answer = new HashSet<Integer>();
        String[] words = phrase.split("\\W+");
        try {
            HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
            HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
            answer = OR(Not(pL1), pL2);
        } catch (Exception e) {
            System.out.println("Not found");
        }
        return answer;
    }
     
    
     public HashSet<Integer> NotOringNot(String phrase) { // 2 term phrase  2 postingsLists
                HashSet<Integer> answer = new HashSet<Integer>();
        String[] words = phrase.split("\\W+");
        try {
            HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
            HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
             answer = OR(Not(pL1),Not (pL2));
        } catch (Exception e) {
            System.out.println("Not found");
        }
        return answer;
    }
    
   public HashSet<Integer> Noting(String phrase) { // 2 term phrase  2 postingsLists
        String[] words = phrase.split("\\W+");
                        HashSet<Integer> answer = new HashSet<Integer>();

        try {
            HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
             answer = Not(pL1);
        } catch (Exception e) {
            System.out.println("Not found");
        }
        return answer;
    }
   

    public HashSet<Integer> parserone(String phrase)
     {
        HashSet<Integer> res = new HashSet<>();
        String[] words = phrase.split("\\W+");
        if(words[0].equals("not"))
        res=Noting(words[1]);
        else if(!(words[0].equals("not")))
        res=find(words[0]);
        return res;
    }

    
   public HashSet<Integer> parsertwo(String phrase) {
 HashSet<Integer> res = new HashSet<>();
        String[] words = phrase.split("\\W+");
    if(words[0].equals("not"))
    {
        if(words[2].equals("and")&&!(words[3].equals("not")))
        {
res=And(Noting(words[1]),find(words[3]));
        }
       else if(words[2].equals("and")&&words[3].equals("not"))
{
    res=And(Noting(words[1]),Noting(words[4]));
}
else if(words[2].equals("or")&&!(words[3].equals("not")))
{
    res=OR(Noting(words[1]),find(words[3]));
}
else if(words[2].equals("or")&&words[3].equals("not"))
{
    res=OR(Noting(words[1]),Noting(words[4]));
}
    }

   else if(!(words[0].equals("not")))
    {
        if(words[1].equals("and")&&!(words[2].equals("not")))
        {
res=And(find(words[0]),find(words[2]));
        }
       else if(words[1].equals("and")&&words[2].equals("not"))
{
  res=And(find(words[0]),Noting(words[3]));
}
else if(words[1].equals("or")&&!(words[2].equals("not")))
{
    res=OR(find(words[0]),find(words[2]));
}
else if(words[1].equals("or")&&words[2].equals("not"))
{
   res=OR(find(words[0]),Noting(words[3]));
}
}

return res;
       }  

       public HashSet<Integer> parserthree(String phrase)
       {
        String str="";
        String[] words = phrase.split("\\W+");
        HashSet<Integer>res1=new HashSet<>();
        HashSet<Integer>res=new HashSet<>();
if( words[words.length-2].equals("not"))
{
    for(int i=0;i<words.length-3;i++)
    {
        if(i==0)
        str+=words[i];
        else
str=str+" "+words[i];    }
    res1=parsertwo(str);
    if( words[words.length-3].equals("and"))
    {
        res=And(res1,Noting(words[words.length-1]));
    }
    if( words[words.length-3].equals("or"))
    {
        res=OR(res1,Noting(words[words.length-1]));
    }
}

else if (!(words[words.length-2].equals("not")))
{
    for(int i=0;i<words.length-2;i++)
    {
        if(i==0)
        str+=words[i];
        else
str=str+" "+words[i];
    }
    res1=parsertwo(str);
    if( words[words.length-2].equals("and"))
    {
        res=And(res1,find(words[words.length-1]));
    }
    if( words[words.length-2].equals("or"))
    {
        res=OR(res1,find(words[words.length-1]));
    }
}

return res;
       }


public static class InvertedIndex002 {
    public static void main(String args[]) throws IOException {
      try{ Index2 index = new Index2();
        String phrase = "";
        index.buildIndex(new String[]{
            "C:\\Users\\modaser\\Desktop\\New folder (6)\\m,m,\\docs\\100.txt",
            "C:\\Users\\modaser\\Desktop\\New folder (6)\\m,m,\\docs\\101.txt",
            "C:\\Users\\modaser\\Desktop\\New folder (6)\\m,m,\\docs\\102.txt",
            "C:\\Users\\modaser\\Desktop\\New folder (6)\\m,m,\\docs\\103.txt",
            "C:\\Users\\modaser\\Desktop\\New folder (6)\\m,m,\\docs\\104.txt",
            "C:\\Users\\modaser\\Desktop\\New folder (6)\\m,m,\\docs\\105.txt",
            "C:\\Users\\modaser\\Desktop\\New folder (6)\\m,m,\\docs\\106.txt",
            "C:\\Users\\modaser\\Desktop\\New folder (6)\\m,m,\\docs\\107.txt",
            "C:\\Users\\modaser\\Desktop\\New folder (6)\\m,m,\\docs\\108.txt",
            "C:\\Users\\modaser\\Desktop\\New folder (6)\\m,m,\\docs\\109.txt"
        });     
        
        System.out.println("Enter Query");
int c=0;
        HashSet<Integer>res=new HashSet<>();
          @SuppressWarnings("resource")
          BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
int m=0;
    phrase = in.readLine();
    phrase=phrase.toLowerCase();
    String[] words = phrase.split("\\W+");
    for(int i=0;i<words.length;i++)
    {
if ((words[i].equals("and"))||(words[i].equals("or")))
{
c++;        
}
    }
    if(c==0)
    res=index.parserone(phrase);
    else if(c==1)
    res=index.parsertwo(phrase);
    else if(c==2)
    res=index.parserthree(phrase);
    System.out.println("---------------------------------------------------------------------------------");
    System.out.println("document:");
for(int i:res)
{
    System.out.println("\t" + index.sources.get(i) + "\n");
m++;
}
if(m==0)
System.out.println("not found");
System.out.println("---------------------------------------------------------------------------------");
    }
    catch (Exception e) {
        System.out.println("Not found");
    }
}
}

}


