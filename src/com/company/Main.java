package com.company;

import java.util.*;

public class Main {

    public static void main(String[] args) {
	// write your code here
        //Lexer lexer = new Lexer();
        Parser parser = new Parser();
        System.out.println(parser.FIRST);
    }
}
