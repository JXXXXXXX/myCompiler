package com.company;

import java.io.*;
import java.util.*;

public class Parser {
    private String file_buffer;
    public Vector<Grammar> G;
    public Vector<String> V,T;
    public HashMap<String, HashSet<String>> FIRST,FOLLOW;
    public HashMap<String,HashSet<Integer>>indexToV;// HashSet是非终结符号的定义式（有若干个）
    public static String[]  _V={"P","D","S","L","E","C","T","F"},
                            _T={"id","int","float","if","else","while","num", ";",">","<","==","=","+","-","*","/","(",")","~"};

    public void readfile(String filepath){
        // 读源程序文件
        file_buffer = new String();
        try{
            char temp_char;
            File file = new File(filepath);
            Reader reader = new InputStreamReader(new FileInputStream(file));
            while((temp_char=(char) reader.read())!='$'){
                if (temp_char==' '||temp_char=='\t'||temp_char=='\r')
                    continue;
                else {
                    file_buffer = file_buffer + temp_char;
                }
            }
            file_buffer+='\0';
            reader.close();
        }catch (IOException e){
            System.out.println("IOException.");
            System.exit(0);
        }
    }
    public String getNextSymbol(String str,int index){
        // 在字符串str里的index处向后，找第一个匹配的文法V或T
        while(str.charAt(index)==' '){
            index++;
        }

        String nextSymbol = null;
        for (int i=0;i<V.size();i++){
            // 在非终结符号里找
            if(str.indexOf(V.get(i),index)==index)
                nextSymbol = V.get(i);
        }
        for (int i=0;i<T.size();i++){
            // 在终结符号里找
            if(str.indexOf(T.get(i),index)==index)
                nextSymbol = T.get(i);
        }
        if(str.charAt(index)=='-'&&str.charAt(index+1)=='>')
            nextSymbol="->";
        return nextSymbol;
    }

    public void get_garmmer(){
        // 1. 生成 V,T向量
        for(int i=0;i<_V.length;i++){
            V.add(_V[i]);
        }
        for(int i=0;i<_T.length;i++){
            T.add(_T[i]);
        }

        // 2. 读文件
        String filepath = "E://Grammar.txt";
        readfile(filepath);
        // 3.增广文法
        Grammar g0 = new Grammar();
        g0.left = "START";
        g0.right.add("P");
        G.add(g0);      // 添加到文法G
        V.add("START"); // 添加到非终结符V

        int row=0; // 记录经历的行数
        for(int index=0;file_buffer.charAt(index)!='\0'; )
        {
            String left = null;
            String right = null;
            Grammar new_grammar = new Grammar();
            while((left=getNextSymbol(file_buffer,index))!="->"){
                // left
                left = getNextSymbol(file_buffer,index);
                if (left!=null){
                    new_grammar.left = left;
                    index=index+left.length();
                }
                else {
                    System.out.println("文法符号查找错误"+",left"+",row:"+row);
                    System.exit(0);
                }
            }
            index+=left.length();

            while(file_buffer.charAt(index)!='\n'){
                // right
                right = getNextSymbol(file_buffer,index);
                if(right!=null){
                    new_grammar.right.add(right);
                    index+=right.length();
                }
                else {
                    System.out.println("文法符号查找错误"+",right"+",row:"+row);
                    System.exit(0);
                }
            }

            G.add(new_grammar);
            index+=1;
            row++;
        }

        // 4.生成indexToV
        for(int i=0;i<G.size();i++){
            String left = G.get(i).left;
            if(indexToV.containsKey(left)){ // 若HashMap包含了left这个非终结符
                indexToV.get(left).add(i);  // 添加索引
            }
            else {
                indexToV.put(left,new HashSet<Integer>());// 创建关于该非终结符的map键
                indexToV.get(left).add(i);// 添加索引
            }
        }
    }

    public boolean inVT(String s){
        // 判断s在集合V还是T中，若在V中，返回True
        for(int i=0;i<V.size();i++){
            if(s.equals(V.get(i)))
                return true;
        }
        return false;
    }
    public void get_first(){

    }
    public void get_follow(){

    }
    public Parser(){
        G = new Vector<Grammar>();  // 文法
        V = new Vector<String>();   // 非终结符
        T = new Vector<String>();   // 终结符
        FIRST = new HashMap<>();
        FOLLOW = new HashMap<>();
        indexToV = new HashMap<>();

        get_garmmer();  //从文件读入文法符号
        get_first();    //获得FIRST集
        get_follow();   //获得FOLLOW集
    }
}

class Grammar{
    String left;
    Vector<String> right;

    Grammar(){
        left = null;
        right = new Vector<String>();
    }
}
