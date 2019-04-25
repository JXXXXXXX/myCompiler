package com.company;

public class Main {

    public static void main(String[] args) {
	// write your code here
        //Lexer lexer = new Lexer();
        Parser p=new Parser();
        String  str = "123int";
        System.out.println(p.getNextSymbol(str,3));
    }
}
