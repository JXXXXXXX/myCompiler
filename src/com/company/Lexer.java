package com.company;

import java.util.Vector;
import java.io.*;

public class Lexer {
    public int syn,index;
    public Table id;    // 标识符表
    public Table num;   // 常数表
    public String input_code;   // 输入代码
    public Token tokens;    // 生成的token序列

    public void readcode(){
        // 读源程序文件
        try{
            char temp_char;
            File file = new File("E://input.txt");
            Reader reader = new InputStreamReader(new FileInputStream(file));
            while((temp_char=(char) reader.read())!='$'){
                input_code=input_code+temp_char;
            }
            reader.close();
        }catch (IOException e){
            System.out.println("IOException.");
        }
    }

    public void pre_op(){
        // 预处理，过滤空格和注释
        StringBuffer tempString = new StringBuffer();
        int count=0;

        //对注释的处理
        for(int i=0; i<input_code.length(); i++)
        {// 扫描每一个字符

            if(input_code.charAt(i)=='/'&&input_code.charAt(i+1)=='/')
            /**扫描到'//'，则略过这行**/
                while(input_code.charAt(i)!='\n')
                    i++;
            if(input_code.charAt(i)=='/'&&input_code.charAt(i+1)=='*')
            {   // 扫描'/** **/'类型的注释
                i+=2;
                while(input_code.charAt(i)!='*'||input_code.charAt(i+1)!='/')
                {
                    i++;
                    if(input_code.charAt(i)=='$')
                    {
                        System.out.println("注释出错");
                        return;
                    }
                }
                i+=2;
            }
            if(input_code.charAt(i)!='\n'&&input_code.charAt(i)!='\t'&&input_code.charAt(i)!='\r')
            {   // 遇到不是以下情况：换行/制表符/垂直制表/回车，count++
                tempString.insert(count++,input_code.charAt(i));
            }
        }
        tempString.insert(count,'\0');
        input_code=tempString.toString();
    }

    public Token Scanner(){
        Token token = new Token();
        StringBuffer cisu = new StringBuffer();

        while(input_code.charAt(index)==' '){
            // 跳过空白字符
            index++;
        }

        // todo:isletter

        return token;
    }

    public Lexer(){
        syn=0;
        index=0;
        id = new Table();
        num = new Table();
        input_code = new String();
        tokens = new Token();
        readcode();
        pre_op();
    }
}

class Token extends Vector<Token> {
    String type;
    String value;
}

class Table{
    String content[];
    int size=0;
}

