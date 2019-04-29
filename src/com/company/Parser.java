package com.company;

import com.sun.deploy.panel.ITreeNode;

import java.io.*;
import java.util.*;

public class Parser {
    // -----变量声明部分-----
    private String file_buffer;
    public HashMap<Integer,Status> statuses;
    public Vector<Grammar> G;
    public Vector<String> V,T;
    public HashMap<String, HashSet<String>> FIRST,FOLLOW;
    public Atable action;
    public Gtable goTo;
    public HashMap<String,HashSet<Integer>>indexToV;// HashSet是非终结符号的定义式（有若干个）
    public static String[]  _V={"START","P","D","S","L","E","C","T","F"},
                            _T={"id","int","float","if","else","while","num", ";",">","<","==","=","+","-","*","/","(",")","~"};

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
        }
        nextSymbol=symbolset.get(maxindex);

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
        FOLLOW.get("P").add("~");

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

    public Status get_closure(Status p){
        boolean change = true;
        Status pptmp;
        while(change){
            change=false;
            pptmp=p;
            for(Iterator it = pptmp.set.iterator();it.hasNext();){
                Project pro=(Project)it.next();

                if(pro.dot_position==G.get(pro.pro_num).right.size())
                    continue;
                String symbol=G.get(pro.pro_num).right.get(pro.dot_position);
                if (!inVT(symbol)){
                    continue;
                }
                HashSet<String> new_successor;
                if(pro.dot_position==G.get(pro.pro_num).right.size())
                    new_successor=pro.successors;
                else {
                    Vector<String> vtmp = new Vector<>();
                    for(int i=pro.dot_position+1;i<G.get(pro.pro_num).right.size();i++){
                        vtmp.add(G.get(pro.pro_num).right.get(i));
                    }
                    new_successor=judge_first(vtmp);
                    if (new_successor.contains("~")){
                        new_successor.addAll(pro.successors);
                        new_successor.remove("~");
                    }
                }
                Project ptmp = new Project();
                for(Iterator it2 = indexToV.get(symbol).iterator();it.hasNext();){
                    int i=(int)it2.next();
                    ptmp.pro_num=i;
                    ptmp.dot_position=0;
                    ptmp.successors=new_successor;

                    Project p_project = null;
                    boolean ptmp_in_p = false;
                    for(Iterator it3 = p.set.iterator();it3.hasNext();){
                        p_project = (Project)it3.next();
                        if(p_project.pro_num==ptmp.pro_num && p_project.dot_position == ptmp.dot_position){
                            ptmp_in_p = true;
                            break;
                        }
                    }
                    if(!ptmp_in_p){
                        // ptmp不在p中
                        p.set.add(ptmp);
                        change=true;
                    }
                    else {
                        int ori_size = p_project.successors.size();
                        ptmp.successors.addAll(p_project.successors);
                        p.set.remove(p_project);
                        p.set.add(ptmp);
                        if(ori_size<ptmp.successors.size())
                            change = true;
                    }
                }
            }
        }
        return p;
    }

    boolean judge_repeat(Status s1, Status s2){
        if(s1.set.size()==s2.set.size()){
            for(Iterator it1 = s1.set.iterator();it1.hasNext();){
                Project p1 = (Project)it1.next();
                if(!s2.set.contains(p1))
                    return false;
            }
            return true;
        }
        return false;
    }

    Object [] judge_conflict(Status s,HashSet<String> result){

        boolean flag = false;
        HashSet<String> tmp = new HashSet<>();
        for(Iterator it = s.set.iterator();it.hasNext();){
            Project pro = (Project)it.next();
            if(pro.dot_position==G.get(pro.pro_num).right.size()){
                tmp.addAll(pro.successors);
            }
        }
        for(Iterator it = s.set.iterator();it.hasNext();){
            Project pro = (Project)it.next();
            if(pro.dot_position<G.get(pro.pro_num).right.size()){
                String next = G.get(pro.pro_num).right.get(pro.dot_position);
                if(tmp.contains(next)){
                    result.add(next);
                    flag = true;
                }
            }
        }
        Object [] obj_return = new Object[3];
        obj_return[0] = flag;
        obj_return[1] = s;
        obj_return[2] = result;
        return obj_return;
    }

    public void get_status(){
        int t=0;
        Project ptmp = new Project();
        ptmp.pro_num = 0;
        ptmp.dot_position=0;
        ptmp.successors = new HashSet<>();
        ptmp.successors.add("~");

        Status tmp_status = new Status();
        tmp_status.set = new HashSet<>();
        tmp_status.set.add(ptmp);
        tmp_status=get_closure(tmp_status);

        for(Iterator iterator = tmp_status.set.iterator();iterator.hasNext();){
            Project p=(Project)iterator.next();
            System.out.println(p.pro_num+","+p.dot_position);
        }


        statuses.put(t,tmp_status);

        boolean change=true;
        HashSet<Integer> record = new HashSet<>();
        HashMap<Integer,Status> sstmp = null;
        HashSet<String> conflict = null;

        while (change){
            change = false;
            sstmp = statuses;

            for(Object sta:sstmp.keySet()){
                int key = (int)sta;
                Status value = sstmp.get(key);
                if (record.contains(key)){
                    continue;
                }
                record.add(key);
                HashSet<String> record_status = new HashSet<>();
                for(Iterator it = value.set.iterator();it.hasNext();){
                    Project pros = (Project) it.next();
                    if(G.get(pros.pro_num).right.get(0)=="~" || pros.dot_position == G.get(pros.pro_num).right.size()){
                        for (Iterator it2 = pros.successors.iterator();it2.hasNext();){
                            String sucess = (String)it2.next();
                            if (!action.map.get(key).containsKey(sucess)){
                                String tmp_str = "r"+pros.pro_num;
                                action.map.get(key).put(sucess,tmp_str);
                            }
                        }
                        continue;
                    }
                    String trans = G.get(pros.pro_num).right.get(pros.dot_position);
                    if (record_status.contains(trans))
                        continue;
                    record_status.add(trans);
                    tmp_status.set.clear();
                    ptmp.pro_num = pros.pro_num;
                    ptmp.dot_position = pros.dot_position+1;
                    ptmp.successors = pros.successors;
                    tmp_status.set.add(ptmp);

                    for (Iterator it3 = value.set.iterator();it3.hasNext();){
                        Project protmp = (Project)it3.next();
                        if(protmp.dot_position<G.get(protmp.pro_num).right.size() && G.get(protmp.pro_num).right.get(protmp.dot_position).equals(trans) && !(protmp == pros)){
                            ptmp.pro_num = protmp.pro_num;
                            ptmp.dot_position = protmp.dot_position + 1;
                            ptmp.successors = protmp.successors;
                            tmp_status.set.add(ptmp);
                        }
                    }

                    tmp_status = get_closure(tmp_status);
                    boolean flag = true;
                    for (Object s:sstmp.keySet()){
                        int s_first = (int)s;
                        Status s_second = sstmp.get(s);

                        if(judge_repeat(s_second,tmp_status)){
                            if (inVT(trans)){
                                HashMap<String,Integer> tmp_map = new HashMap<>();
                                tmp_map.put(trans,s_first);
                                goTo.map.put(key,tmp_map);
                            }
                            else {
                                HashMap<String,String> tmp_map = new HashMap<>();
                                String tmp_str = "s"+Integer.toString(s_first);
                                tmp_map.put(trans,tmp_str);
                                action.map.put(key,tmp_map);
                            }
                            flag = false;
                            break;
                        }
                    }
                    if (!flag)
                        continue;
                    statuses.put(++t,tmp_status);
                    change = true;

                    if (inVT(trans)){
                        HashMap<String,Integer> tmp_map = new HashMap<>();
                        tmp_map.put(trans,t);
                        goTo.map.put(key,tmp_map);
                    }
                    else {
                        HashMap<String,String> tmp_map = new HashMap<>();
                        String tmp_str = "s"+t;
                        tmp_map.put(trans,tmp_str);
                        action.map.put(key,tmp_map);
                    }

                }
            }

        }

        int tmp_int = goTo.map.get(0).get("P");
        if (!action.map.containsKey(tmp_int)){
            HashMap<String,String> tmp_map = new HashMap<>();
            tmp_map.put("~","acc");
            action.map.put(tmp_int,tmp_map);
        }
    }

    public void print_table(){
        for (Object f1_first:action.map.keySet()){
            HashMap<String,String> f1_second = action.map.get(f1_first);
            for (Object f2_first:f1_second.keySet()){
                String f2_second = f1_second.get(f2_first);
                System.out.println(f1_first+" "+f2_first+" "+f2_second);
            }
        }

        for (Object f1_first:goTo.map.keySet()){
            HashMap<String,Integer> f1_second = goTo.map.get(f1_first);
            for (Object f2_first:f1_second.keySet()){
                int f2_second = f1_second.get(f2_first);
                System.out.println(f1_first+" "+f2_first+" "+f2_second);
            }
        }
    }

    public Parser(){
        G = new Vector<Grammar>();  // 文法
        V = new Vector<String>();   // 非终结符
        T = new Vector<String>();   // 终结符
        FIRST = new HashMap<>();
        FOLLOW = new HashMap<>();
        indexToV = new HashMap<>();
        statuses = new HashMap<>();
        action = new Atable();
        action.map = new HashMap<>();
        goTo = new Gtable();
        goTo.map = new HashMap<>();

        get_garmmer();  //从文件读入文法符号
        get_first();    //获得FIRST集
        get_follow();   //获得FOLLOW集
        get_status();
        print_table();
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

class Project{
    int pro_num;
    int dot_position;
    HashSet<String> successors;
}

class Status{
    HashSet<Project> set;
}

class Atable{
    HashMap<Integer,HashMap<String,String>> map;
}

class Gtable{
    HashMap<Integer,HashMap<String,Integer>> map;
}