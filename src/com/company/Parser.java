package com.company;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class Parser {
    private String file_buffer;
    public Vector<Grammar> G;
    public Vector<String> V,T;
    public HashMap<String, HashSet<String>> FIRST,FOLLOW;
    public static String[]  _V={"P","D","S","L","E","C","T","F"},
                            _T={"id","int","float","if","else","while","num", ";",">","<","==","=","+","-","*","/","(",")","ε"};

    public void readfile(String filepath){
        // 读源程序文件
        file_buffer = new String();
        try{
            char temp_char;
            File file = new File(filepath);
            Reader reader = new InputStreamReader(new FileInputStream(file));
            while((temp_char=(char) reader.read())!='$'){
                if (temp_char==' ')
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
    public String  getNextSymbol(String str,int index){
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
        for(int i=0;i<file_buffer.length();i++)
            System.out.println(file_buffer.charAt(i)-'\0');
        // 3.增广文法
        Grammar g0 = new Grammar();
        g0.left = "START";
        g0.right.add("P");
        G.add(g0);      // 添加到文法G
        V.add("START"); // 添加到非终结符V

        for(int index=0;file_buffer.charAt(index)!='\0';index+=2)
        {
            String left = null;
            String right = null;
            Grammar new_grammar = new Grammar();
            System.out.println("left:");
            while(file_buffer.charAt(index)!='-' && file_buffer.charAt(index+1)!='>'){
                // left
                left = getNextSymbol(file_buffer,index);
                System.out.println(left);
                index=index+left.length();
            }

            new_grammar.left = left;
            index=index+2; // 跳过 ‘->’
            System.out.println("right:");
            while(file_buffer.charAt(index)!='\0'){
                // right
                right = getNextSymbol(file_buffer,index);
                System.out.println(right);
                new_grammar.right.add(right);
                index+=right.length();
                // TODO 空指针
            }

        }
    }
    public void get_first(){

    }
    public void get_follow(){

    }
    public Parser(){
        G = new Vector<Grammar>();  // 文法
        V = new Vector<String>();   // 非终结符
        T = new Vector<String>();   // 终结符

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
