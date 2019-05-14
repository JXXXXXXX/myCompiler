package com.company;

import java.util.*;

public class Main {

    public static void main(String[] args) {
	// write your code here
        //Lexer lexer = new Lexer();
        Parser parser = new Parser();

/*        System.out.println("------G(是增广文法)---------");
        for(int i=0;i<parser.G.size();i++){
            System.out.print(i+":"+parser.G.get(i).left);
            System.out.print("->");
            for (int j=0;j<parser.G.get(i).right.size();j++){
                System.out.print(parser.G.get(i).right.get(j));
            }
            System.out.println();
        }*/

/*        System.out.println("-------first---------");
        for (Object key:parser.FIRST.keySet()){
            System.out.println(key+":"+parser.FIRST.get(key));
        }
        System.out.println("------follow---------");
        for (Object key:parser.FOLLOW.keySet()){
            System.out.println(key+":"+parser.FOLLOW.get(key));
        }*/


    }
}


