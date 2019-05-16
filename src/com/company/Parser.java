package com.company;

import jdk.management.resource.internal.inst.FileOutputStreamRMHooks;

import java.io.*;
import java.util.*;

public class Parser {
    // -----变量声明部分-----
    private String file_buffer;
    public Vector<Grammar> G;
    public Vector<String> V,T;
    public HashMap<String, HashSet<String>> FIRST,FOLLOW;
    public Atable ACTION;
    public Gtable GOTO;
    public HashMap<String,HashSet<Integer>>indexToV;// HashSet是非终结符号的定义式（有若干个）
    public static String[]  _V={"START","P","D","S","L","E","C","T","F","Q1","Q2","M","N"},
                            /*_V={"START","P","D","S","L","E","C","T","F"},*/
                            _T={"id","int","float","if","else","while","num", ";",">","<",
                                    "==","=","+","-","*","/","(",")","~"},
                            _VT={"START","P","D","S","L","E","C","T","F","Q1","Q2","M","N",
                                    "id","int","float","if","else","while","num", ";",">",
                                    "<","==","=","+","-","*","/","(",")","~"};
/*                            _VT={"START","P","D","S","L","E","C","T","F",
                                    "id","int","float","if","else","while","num", ";",">",
                                    "<","==","=","+","-","*","/","(",")","~"};*/

    /*    public static String[]  _V2={"START","P","T","F"},
                            _T2={"id","+","*","(",")"},
                            _VT2={"START","P","T","F","id","+","*","(",")"};*/
    public Vector<Status> LRO_items; // LR(0)项集族
    public Stack<Integer> status_Stack; // 状态栈
    public Stack<String> symbol_Stack;  // 符号栈
    public String[] keyword,op;

    // -----函数实现部分-----
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

        Vector<String> symbolset = new Vector<>();
        String tempsymbol = null;
        String nextSymbol = null;
        for (int i=0;i<V.size();i++){
            // 在非终结符号里找
            if(str.indexOf(V.get(i),index)==index){
                tempsymbol = V.get(i);
                symbolset.add(tempsymbol);
            }
        }
        for (int i=0;i<T.size();i++){
            // 在终结符号里找
            if(str.indexOf(T.get(i),index)==index){
                tempsymbol = T.get(i);
                symbolset.add(tempsymbol);
            }
        }

        int maxindex=0;
        if(symbolset.size()!=0){
            for(int i=1;i<symbolset.size();i++){
                if(symbolset.get(i).length()>symbolset.get(maxindex).length())
                    maxindex=i;
            }
            nextSymbol=symbolset.get(maxindex);
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
        /*String filepath = "input/Grammar.txt";*/
        String filepath = "input/Grammar_new.txt";
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
                indexToV.put(left,new HashSet<>());// 创建关于该非终结符的map键
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
        // 初始化 FIRST
        for(int i=0;i<V.size();i++){
            FIRST.put(V.get(i),new HashSet<>());
        }
        for (int i=0;i<T.size();i++){
            FIRST.put(T.get(i),new HashSet<>());
            FIRST.get(T.get(i)).add(T.get(i)); // 终结符 加入自己
        }


        boolean change = true; // 生成过程中是否发生改动，若改动则需重新遍历
        boolean is_empty;
        int t;
        while (change){// 循环直到没有改动
            change = false;
            for (int i=0;i<G.size();i++){
                // 对每一个文法
                Grammar g = G.get(i);
                is_empty = true;
                t=0;
                while (is_empty && t<g.right.size() ){
                    is_empty = false;
                    if (!inVT(g.right.get(t))){
                        // 若右端第t个文法符号是终结符
                        if(!FIRST.get(g.left).contains(g.right.get(t))){
                            // 如果g.left对应的 FIRST集 中，不包含g.right.get(t)这个终结符,则加入它
                            FIRST.get(g.left).add(g.right.get(t));
                            change =true;
                        }
                        continue;
                    }
                    for(Iterator it=FIRST.get(g.right.get(t)).iterator();it.hasNext();){
                        // 对于g.right.get(t)对应的 FIRST集 中的每个元素
                        String temp = (String) it.next();
                        if(!FIRST.get(g.left).contains(temp) && temp!="~"){
                            FIRST.get(g.left).add(temp);
                            change =true;
                        }
                    }
                    if (FIRST.get(g.right.get(t)).contains("~")){
                        // 若g.right.get(t)对应的 FIRST集 包含 空产生式‘~’
                        is_empty = true;
                        t++;
                    }
                }
                if (t == g.right.size() && !FIRST.get(g.left).contains("~")){
                    FIRST.get(g.left).add("~");
                    change = true;
                }
            }
        }

        FIRST.remove("START");
    }
    public HashSet<String> judge_first(Vector<String> strSet){
        HashSet<String> result = new HashSet<>();
        int count = 0;
        String symbol;
        for(int i=0;i<strSet.size();i++){
            symbol = strSet.get(i);
            if(!inVT(symbol)){
                // 终结符
                result.add(symbol);
                break;
            }
            // 非终结符
            if (!FIRST.get(symbol).contains("~")){
                //symbol的first集里 没有“~”
                result.addAll(FIRST.get(symbol));
                break;
            }
            result.addAll(FIRST.get(symbol));
            result.remove("~");
            count++;
        }
        if (count==strSet.size())
            result.add("~");

        return result;
    }

    public void get_follow(){
        // 初始化FOLLOW
        for(int i=0;i<V.size();i++){
            FOLLOW.put(V.get(i),new HashSet<>());
        }
        FOLLOW.get("P").add("$");

        boolean change = true;
        String symbolRight;
        while (change) {
            change = false;
            int nextIndex;
            for (int i=0;i<G.size();i++){
                Grammar g = G.get(i);
                for(int j=0;j<g.right.size();j++){
                    //对于文法g右侧的每一个文法符号g.right.get(j)
                    symbolRight = g.right.get(j);
                    if (!inVT(symbolRight)){
                        continue;
                    }
                    int ori_size = FOLLOW.get(symbolRight).size();
                    nextIndex = j+1;
                    if(nextIndex!=g.right.size()){
                        Vector<String> tmp = new Vector<>();
                        for(int k=nextIndex;k<g.right.size();k++)
                            tmp.add(g.right.get(k));
                        HashSet<String> stmp = judge_first(tmp);
                        FOLLOW.get(symbolRight).addAll(stmp);
                        //System.out.println(g.left+"--"+symbolRight+"--"+FOLLOW.get(symbolRight));
                        if(stmp.contains("~")){
                            FOLLOW.get(symbolRight).remove("~");
                            FOLLOW.get(symbolRight).addAll(FOLLOW.get(g.left));
                        }
                        if (ori_size<FOLLOW.get(symbolRight).size())
                            change = true;
                    }
                    else {
                        FOLLOW.get(symbolRight).addAll(FOLLOW.get(g.left));
                        //System.out.println(g.left+"--"+FOLLOW.get(g.left)+"|"+symbolRight+"--"+FOLLOW.get(symbolRight));
                        if (ori_size<FOLLOW.get(symbolRight).size())
                            change = true;
                    }
                }
            }
        }
        FOLLOW.remove("START");
    }

    public boolean if_in_Set(Status s,Project p){
        boolean flag = false;
        for (Iterator it = s.set.iterator();it.hasNext();){
            Project p_set = (Project)it.next();
            if (p.pro_num==p_set.pro_num && p.dot_position == p_set.dot_position){
                flag = true;
            }
        }
        return flag;
    }

    public Status CLOSURE(Status I){
        Status J = new Status();
        Status J2 = new Status();
        J2.set=(HashSet<Project>) I.set.clone();
        Boolean change = true;
        while (change){
            change = false;
            J.set = (HashSet<Project>) J2.set.clone();// 解决在迭代的同时不能修改的问题，将迭代对象J复制一份到J2

            for (Iterator it = J.set.iterator();it.hasNext();){
                // 访问项集J中的每个产生式
                Project project = (Project)it.next();
                if (G.get(project.pro_num).right.size()-1>=project.dot_position){
                    String next_Symbol = G.get(project.pro_num).right.get(project.dot_position);
                    if (!inVT(next_Symbol)){
                        // 下个文法符号不是非终结符
                        continue;
                    }
                    else {
                        for (Iterator it2 = indexToV.get(next_Symbol).iterator();it2.hasNext();){
                            int g_num = (int)it2.next();
                            Project new_project = new Project();
                            new_project.pro_num = g_num;
                            new_project.dot_position = 0;
                            if (!if_in_Set(J,new_project)){
                                J2.set.add(new_project);
                                change = true;
                            }
                        }//for
                    }//else
                }
            }
        }
        return J2;
    }

    public boolean in_LRO_items(Status s){
        // 判断项集s是否在LRO_items中
        boolean flag = false;
        for (int i=0;i<LRO_items.size();i++){
            if (s.set.size()==LRO_items.get(i).set.size()){
                Status si = LRO_items.get(i); // 从LRO_items中选出一个set集si
                int count=0;
                for (Iterator it = s.set.iterator();it.hasNext();){// 对s中的所有对象进行循环
                    Project tmp_p = (Project)it.next();
                    if (!if_in_Set(si,tmp_p)){
                        break;// 如果tmp_p不在si里，就立刻跳出内层循环
                    }
                    count++;
                }
                if (count==s.set.size()){
                    // 说明si和s完全匹配
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    public Status GOTO(Status I,String Symbol){
        Status prj_in_I = new Status();
        prj_in_I.set = new HashSet<>();
        for (Iterator it = I.set.iterator();it.hasNext();){// for循环找出项集I中所有【下个符号为Symbol的产生式】加入prj_in_I中
            Project tmp_prj = (Project) it.next(); // I中的某一条产生式tmp_prj
            Project new_prj = new Project();
            new_prj.pro_num = tmp_prj.pro_num;
            new_prj.dot_position = tmp_prj.dot_position;// 解决java对象的引用，避免修改了原始的数据

            if (G.get(new_prj.pro_num).right.size()-1>=new_prj.dot_position){
                // 如果dot在产生式右侧的长度(-1)之内,则允许继续向后读入文法符号
                String next_symbol = G.get(new_prj.pro_num).right.get(new_prj.dot_position);
                if (next_symbol.equals(Symbol)){
                    new_prj.dot_position++;
                    prj_in_I.set.add(new_prj);
                }
            }
        }
        return CLOSURE(prj_in_I);
    }

    public void get_items(){
        // 生成LR(0)项集族函数
        LRO_items = new Vector<>();

        Project prj_init = new Project();
        Status status_init = new Status();
        status_init.set = new HashSet<>();
        prj_init.pro_num = 0;// 对应G中的增广文法产生式
        prj_init.dot_position=0;
        status_init.set.add(prj_init);

        status_init = CLOSURE(status_init);
        LRO_items.add(status_init);
        boolean change = true;
        while (change){
            change = false;
            for (int i=0;i<LRO_items.size();i++){
                // 对于项集族中的每一个产生式I
                Status I = LRO_items.get(i);
                for (int j=0;j<_VT.length;j++){
                    // 对文法符号中的每一个变量X
                    if (_VT[j].equals("~"))
                        continue;
                    String X = _VT[j];
                    Status new_status = GOTO(I,X);
                    if (new_status.set.size()!=0 && in_LRO_items(new_status)==false){
                        // 若GOTO(I,X)不为空，且不在项集族中,则加入项集族
                        LRO_items.add(new_status);
                        change = true;
                    }
                }
            }
        }
    }

    public void print_items(){
        System.out.println("----------LR(0)项集族："+LRO_items.size()+"个----------");
        for (int i=0;i<LRO_items.size();i++){
            Status I = LRO_items.get(i);
            System.out.println("###I("+i+")###");

            for (Iterator it = I.set.iterator();it.hasNext();){
                Project tmp_p = (Project)it.next();
                Grammar tmp_g = G.get(tmp_p.pro_num);
                System.out.print("  "+tmp_g.left+"->");
                int count = 0;
                for (int j=0;j<tmp_g.right.size();j++){
                    if (count==tmp_p.dot_position)
                        System.out.print(".");
                    System.out.print(tmp_g.right.get(j));
                    count++;
                }
                if (count==tmp_p.dot_position)
                    System.out.print(".");
                System.out.println();
            }
        }
        System.out.println();
    }

    public void print_G(){
        // 打印文法G
        System.out.println("------G(是增广文法)---------");
        for(int i=0;i<G.size();i++){
            System.out.print(i+":"+G.get(i).left);
            System.out.print("->");
            for (int j=0;j<G.get(i).right.size();j++){
                System.out.print(G.get(i).right.get(j));
            }
            System.out.println();
        }
    }

    public void print_firstANDfollow(){
        // 打印first&follow
        System.out.println("-------first---------");
        for (Object key:FIRST.keySet()){
            System.out.println(key+":"+FIRST.get(key));
        }
        System.out.println("------follow---------");
        for (Object key:FOLLOW.keySet()){
            System.out.println(key+":"+FOLLOW.get(key));
        }
    }

    public void printALLINFO(){
        print_G(); // 打印文法G
        print_firstANDfollow(); // 打印first和follow集
        print_items(); // 打印LR(0)项
        printAnalysisTable(); // 打印ACTION和GOTO分析表
    }

    public String getNextSymbol(Project p){
        // 获得文法p的下一个字符，若没有则返回null
        String nextSymbol = null;
        if (p.pro_num>=0 && p.pro_num<G.size()){
            if (p.dot_position>=0 && p.dot_position<=G.get(p.pro_num).right.size()-1){
                nextSymbol = G.get(p.pro_num).right.get(p.dot_position);
            }
        }
        return nextSymbol;
    }

    public int getIndexOfItem(Status s){
        // 判断一个LR0项s的在LRO_items中的序号
        int index=-1;
        for (int i=0;i<LRO_items.size();i++){
            if (s.set.size()==LRO_items.get(i).set.size()){
                Status si = LRO_items.get(i); // 从LRO_items中选出一个set集si
                int count=0;
                for (Iterator it = s.set.iterator();it.hasNext();){// 对s中的所有对象进行循环
                    Project tmp_p = (Project)it.next();
                    if (!if_in_Set(si,tmp_p)){
                        break;// 如果tmp_p不在si里，就立刻跳出内层循环
                    }
                    count++;
                }
                if (count==s.set.size()){
                    // 说明si和s完全匹配
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    public void get_AnalysisTable(){
        // 生成ACTION表和GOTO表
        ACTION = new Atable();
        GOTO = new Gtable();
        ACTION.map = new HashMap<>();
        GOTO.map = new HashMap<>();

        for (int i=0;i<LRO_items.size();i++){// 访问所有LR0项
            Status I = LRO_items.get(i);
            for (Iterator it = I.set.iterator();it.hasNext();){// 遍历项I的每一个产生式
                Project p = (Project)it.next();
                String nextSymbol = getNextSymbol(p);
                if (nextSymbol!=null){
                    if (inVT(nextSymbol)){
                        // 如果是语法变量V--填写GOTO
                        int index = getIndexOfItem(GOTO(I,nextSymbol));
                        if (index!=-1){
                            if (GOTO.map.get(i)==null){
                                HashMap<String,Integer> map1 = new HashMap<>();
                                map1.put(nextSymbol,index);
                                GOTO.map.put(i,map1);// 加入GOTO
                            }
                            else{
                                GOTO.map.get(i).put(nextSymbol,index);
                            }
                        }
                    }
                    else if (nextSymbol.equals("~")){
                        // 若是形如A->.~,则执行-归约-
                        // 对应归约的产生式号：p.pro_num
                        String left = G.get(p.pro_num).left;
                        HashSet<String> FOLLOW_A = FOLLOW.get(left);
                        for (Iterator it2 = FOLLOW_A.iterator();it2.hasNext();){//对于FOLLOW(A)中的所有符号
                            String a = (String)it2.next();
                            Object [] rj = new Object[2];
                            rj[0]="r";
                            rj[1]=p.pro_num;

                            if (ACTION.map.get(i)==null){
                                HashMap<String,Object[]> map2 = new HashMap<>();
                                map2.put(a,rj);
                                ACTION.map.put(i,map2);
                            }
                            else {
                                ACTION.map.get(i).put(a,rj);
                            }
                        }
                    }
                    else {
                        // 如果是【终结符号T】--填写ACTION
                        int index = getIndexOfItem(GOTO(I,nextSymbol));
                        if (index!=-1){
                            Object [] sj = new Object[2];
                            sj[0]="s";
                            sj[1]=index;
                            if (ACTION.map.get(i)==null){
                                HashMap<String,Object[]> map2 = new HashMap<>();
                                map2.put(nextSymbol,sj);
                                ACTION.map.put(i,map2);
                            }
                            else {
                                ACTION.map.get(i).put(nextSymbol,sj);
                            }
                        }
                    }//else
                }
                else {
                    // nextSymbol = null：形如A-a.的产生式--进行归约
                    // 对应归约的产生式号：p.pro_num
                    if (p.pro_num!=0){
                        String left = G.get(p.pro_num).left;
                        HashSet<String> FOLLOW_A = FOLLOW.get(left);
                        for (Iterator it2 = FOLLOW_A.iterator();it2.hasNext();){//对于FOLLOW(A)中的所有符号
                            Object [] rj = new Object[2];
                            rj[0]="r";
                            rj[1]=p.pro_num;
                            String a = (String)it2.next();
                            if (ACTION.map.get(i)==null){
                                HashMap<String,Object[]> map2 = new HashMap<>();
                                map2.put(a,rj);
                                ACTION.map.put(i,map2);
                            }
                            else {
                                ACTION.map.get(i).put(a,rj);
                            }

                        }
                    }
                    else {
                        // p.pro_num=0,对于与S'->S. 则在ACTION[I,$]=ACC
                        Object [] acc = new Object[2];
                        acc[0]="acc";
                        acc[1]=0;
                        if (ACTION.map.get(i)==null){
                            HashMap<String,Object[]> map2 = new HashMap<>();
                            map2.put("$",acc);
                            ACTION.map.put(i,map2);
                        }
                        else {
                            ACTION.map.get(i).put("$",acc);
                        }

                    }

                }
            }
        }
    }

    public void printAnalysisTable(){
        System.out.println("------------ACITON------------");
        for (int i=0;i<_T.length;i++){
            System.out.print("   "+_T[i]+"  ");
        }
        System.out.print("  $  ");
        System.out.println();

        for (int i=0;i<ACTION.map.size();i++){
            System.out.print(i+":");
            for (int j=0;j<_T.length;j++){
                if (ACTION.map.get(i)!=null && ACTION.map.get(i).get(_T[j])!=null){
                    String s = (String) ACTION.map.get(i).get(_T[j])[0];
                    int num= (int) ACTION.map.get(i).get(_T[j])[1];
                    System.out.print("  "+s+num+"  ");
                }
                else {
                    System.out.print("  --  ");
                }
            }
            // 额外判断一列'$'
            if (ACTION.map.get(i)!=null && ACTION.map.get(i).get("$")!=null){
                String s = (String) ACTION.map.get(i).get("$")[0];
                int num= (int) ACTION.map.get(i).get("$")[1];
                System.out.print("  "+s+num+"  ");
            }
            else {
                System.out.print("  --  ");
            }
            System.out.println();
        }

        System.out.println("------------GOTO------------");
        for (int i=1;i<_V.length;i++){
            System.out.print("   "+_V[i]+"    ");
        }
        System.out.println();
        for (int i=0;i<ACTION.map.size();i++){
            System.out.print(i+":");
            for (int j=1;j<_V.length;j++){
                if (GOTO.map.get(i)!=null && GOTO.map.get(i).get(_V[j])!=null){
                    int num= GOTO.map.get(i).get(_V[j]);
                    System.out.print("   "+num+"  ");
                }
                else {
                    System.out.print("  --  ");
                }
            }
            System.out.println();
        }
    }

    public void output_AnalysisTable(String filename){
        try {
            String filepath = "output/";
            filepath = filepath+filename;
            File writeName = new File(filepath);
            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            try (FileWriter writer = new FileWriter(writeName);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
                out.write("**ACTION**\r\n"); // \r\n即为换行
                String out_line = "| |";
                for (int i=0;i<_T.length;i++){
                    out_line=out_line+_T[i]+"|";
                }
                out_line=out_line+"$|\r\n";
                out.write(out_line);// 输出表格字段

                out_line = "|";
                for (int i=0;i<_T.length+2;i++){
                    out_line=out_line+"-|";
                }
                out_line=out_line+"\r\n";
                out.write(out_line);// 输出分隔符

                for (int i=0;i<ACTION.map.size();i++){
                    out_line="|"+i+"|";
                    for (int j=0;j<_T.length;j++){
                        if (ACTION.map.get(i)!=null && ACTION.map.get(i).get(_T[j])!=null){
                            String s = (String) ACTION.map.get(i).get(_T[j])[0];
                            int num= (int) ACTION.map.get(i).get(_T[j])[1];
                            out_line=out_line+s+num+"|";
                        }
                        else {
                            out_line=out_line+"null|";
                        }
                    }
                    // 额外判断一列'$'
                    if (ACTION.map.get(i)!=null && ACTION.map.get(i).get("$")!=null){
                        String s = (String) ACTION.map.get(i).get("$")[0];
                        int num= (int) ACTION.map.get(i).get("$")[1];
                        out_line=out_line+s+num+"|";
                    }
                    else {
                        out_line=out_line+"null|";
                    }
                    out_line=out_line+"\r\n";
                    out.write(out_line);// 输出表的内容(每行)
                }

                out.write("**GOTO**\r\n");
                out_line = "| |";
                for (int i=1;i<_V.length;i++){
                    out_line=out_line+_V[i]+"|";
                }
                out_line=out_line+"\r\n";
                out.write(out_line);// 输出表格字段

                out_line = "|";
                for (int i=0;i<_V.length;i++){
                    out_line=out_line+"-|";
                }
                out_line=out_line+"\r\n";
                out.write(out_line);// 分隔符

                for (int i=0;i<ACTION.map.size();i++){
                    out_line="|"+i+"|";
                    for (int j=1;j<_V.length;j++){
                        if (GOTO.map.get(i)!=null && GOTO.map.get(i).get(_V[j])!=null){
                            int num= GOTO.map.get(i).get(_V[j]);
                            out_line=out_line+num+"|";
                        }
                        else {
                            out_line=out_line+"null|";
                        }
                    }
                    out_line=out_line+"\r\n";
                    out.write(out_line);// 输出表的内容(每行)
                }

                out.flush(); // 把缓存区内容压入文件
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void do_Analysis(Vector<Token> tokens){
/*      LR语法分析算法
        输入：1.LR语法分析表ACTION和GOTO
             2.token输入串*/
        int s;
        Token a;
        Object action_obj [];
        String type;

        // 初始化状态栈和符号栈
        status_Stack.push(0);
        symbol_Stack.push("$");

        for (int i=0;i<tokens.size();i++){
            s = status_Stack.peek();// 获得栈顶状态
            a = tokens.get(i); //获得当前输入的第一个token

            // 处理操作符op
            if (a.type.equals("op"))
                type = op[Integer.parseInt(a.value)];
            else
                type = a.type;

            if (ACTION.map.get(s).get(type)!=null){
                action_obj=ACTION.map.get(s).get(type);
                if (action_obj[0].equals("s")){
                    // 移入
                    status_Stack.push((int)action_obj[1]);
                    symbol_Stack.push(type);
                    //System.out.print(i+":"+action_obj[0]+action_obj[1]);

                }
                else if (action_obj[0].equals("r")){
                    // 归约

                    i--;//归约时，不读入token

                    Grammar g = G.get((int)action_obj[1]);// 按产生式g进行归约

                    if (g.right.get(0).equals("~")){
                        // g形如 D->.~
                        int next_status = GOTO.map.get(status_Stack.peek()).get(g.left);
                        status_Stack.push(next_status);
                        symbol_Stack.push(g.left);
                    }
                    else {
                        for (int j=0;j<g.right.size();j++){
                            symbol_Stack.pop();
                            status_Stack.pop();
                        }
                        symbol_Stack.push(g.left);
                        int next_status = GOTO.map.get(status_Stack.peek()).get(g.left);
                        status_Stack.push(next_status);
                    }

                    //System.out.print(i+":"+action_obj[0]+action_obj[1]);
                }
                else if (action_obj[0].equals("acc")){
                    // 接受
                    System.out.println("Finish analysis.");
                    break;
                }
                else {
                    // 出错处理
                    System.out.println("[error]:token<"+type+a.value+">");
                }
            }
            else {
                // 循环当前的LR0项，如果有形如A->a.Db和D->.~的项，则当作GOTO(i,D)=j,跳转到状态j
                i--;
                Status I = LRO_items.get(s);
                for (Iterator it = I.set.iterator();it.hasNext();){
                    Project p = (Project)it.next();
                    String nextSymbol = getNextSymbol(p);
                    if (nextSymbol.equals("~")){
                        String left = G.get(p.pro_num).left;
                        symbol_Stack.push(left);
                        status_Stack.push(GOTO.map.get(s).get(left));
                    }
                }
            }

 /*           // 输出当前栈的状态
            System.out.print(i+":");
            Stack<Integer> tmpstack2 = new Stack<>();
            while(!status_Stack.empty()){
                int i1  = status_Stack.pop();
                tmpstack2.push(i1);
            }
            while(!tmpstack2.empty()){
                int i1 = tmpstack2.pop();
                status_Stack.push(i1);
                System.out.print(i1+" ");
            }
            System.out.print("|");

            Stack<String> tmpstack = new Stack<>();
            while(!symbol_Stack.empty()){
                String s1  = symbol_Stack.pop();
                tmpstack.push(s1);
            }
            while(!tmpstack.empty()){
                String s2 = tmpstack.pop();
                symbol_Stack.push(s2);
                System.out.print(s2+" ");
            }
            System.out.println();*/

        }// for

    }

    public Parser(){
        // 变量初始化
        G = new Vector<>();
        V = new Vector<>();
        T = new Vector<>();
        FIRST = new HashMap<>();
        FOLLOW = new HashMap<>();
        indexToV = new HashMap<>();
        status_Stack = new Stack<>();
        symbol_Stack = new Stack<>();

        // 分析部分
        Lexer lexer = new Lexer();  // 词法分析
        keyword = lexer.keyword;
        op = lexer.op;

        get_garmmer();              // 从文件读入文法符号
        get_first();                // 获得FIRST集
        get_follow();               // 获得FOLLOW集
        get_items();                // 生成LR(0)项集族
        get_AnalysisTable();        // 创建ACTION和GOTO分析表
        do_Analysis(lexer.tokens);  // 语法分析
        //print_items();
        //output_AnalysisTable("AnalysisTable_new.md"); // 输出ACTION和GOTO表
        //System.out.println("finish");
    }
}

class Grammar{
    String left;
    Vector<String> right;

    Grammar(){
        left = null;
        right = new Vector<>();
    }
}

class Project{
    // LR(0) item
    int pro_num;
    int dot_position;
}

class Status{
    // set of LR(0) item
    HashSet<Project> set;
}

class Atable{
    HashMap<Integer,HashMap<String,
            Object[]
            >> map;
}

class Gtable{
    HashMap<Integer,HashMap<String,
            Integer
            >> map;
}