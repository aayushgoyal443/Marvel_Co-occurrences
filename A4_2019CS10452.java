import java.io.*;
import java.util.Vector;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map;

public class A4_2019CS10452{

    
    public static void main(String[] args) throws FileNotFoundException {

        // Scanner scan = new Scanner(System.in);

        File nodefile = new File(args[0]);
        File edgesfile = new File(args[1]);
        String function_name  =args[2];
        // scan.close();
        
        A4_2019CS10452 aa = new A4_2019CS10452();

        Scanner node_read = new Scanner(nodefile);
        Scanner edges_read = new Scanner(edgesfile);

        node_read.nextLine();
        edges_read.nextLine();

        // Getting all the labels in the labels vector
        Vector <String> labels  = new Vector<>();
        while (node_read.hasNextLine()) {
            String line = node_read.nextLine();

            String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            // Gotta implemet my own split function since this thing works in exponential time.

            if (tokens[1].charAt(0)=='"') {
                labels.add(tokens[1].substring(1, tokens[1].length()-1));
            } else {
                labels.add(tokens[1]);
            }            
        } 

        // Making the adjacency list

        // Putting empty vectors in the hash map
        int n = labels.size();
        for (int i = 0; i < n; i++) {
            Vector <Pair> v = new Vector<>();
            aa.adj.put(labels.get(i), v);
        }

        // Putting values in the vector assuming this is undirected graph.
        while (edges_read.hasNextLine()){
            String line = edges_read.nextLine();

            String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            // Gotta implemet my own split function since this thing works in exponential time.

            int weight = Integer.parseInt(tokens[2]);
            if (tokens[1].charAt(0)=='"') {
                tokens[1] = tokens[1].substring(1, tokens[1].length()-1);
            }
            if (tokens[0].charAt(0)=='"') {
                tokens[0] = tokens[0].substring(1, tokens[0].length()-1);
            }

            aa.adj.get(tokens[0]).add(new Pair(tokens[1], weight));
            aa.adj.get(tokens[1]).add(new Pair(tokens[0], weight));
               
        }

        node_read.close(); edges_read.close();
        
        if (function_name.equals("average")){
            // System.out.println(function_name);
            long sum=0;
            for (Map.Entry<String,Vector<Pair>> itr : aa.adj.entrySet()){
                sum += itr.getValue().size();
            }
            // System.out.println(sum);
            // System.out.println(n);
            
            if (n!=0) System.out.printf("%.2f%n", (sum*1.0)/n);
            else System.out.println("0.00");
        }

        else if (function_name.equals("rank")){
            Vector <Pair> degree = new Vector<>();
            for (Map.Entry<String,Vector<Pair>> itr : aa.adj.entrySet()){
                int sum=0;
                for (Pair pair : itr.getValue() ) {
                    sum += pair.x;
                }
                degree.add(new Pair(itr.getKey(), sum));
            }

            Sorting<Pair> sp = new Sorting<>();
            // Now we need to print them in order descending order of their degrees. Means gotta sort these pairs.
            sp.Merge_sort( degree, 0, n - 1);

            for (int i = 0; i < n; i++) {
                System.out.print(degree.get(i).s);
                if (i<n-1) System.out.print(",");
            }System.out.print("\n");

        }

        else if (function_name.equals("independent_storylines_dfs")){
            
            for (int i = 0; i < n; i++) {
                aa.visited.put(labels.get(i), 0);
            }
            Vector<VectorString> components = new Vector<>();
            for (Map.Entry<String, Vector<Pair>> itr : aa.adj.entrySet()){
                if (aa.visited.get(itr.getKey())==0){
                    Vector <String> v = new Vector<>();
                    aa.DFS(itr.getKey(), v);
                    components.add(new VectorString(v));
                }
            }

            Sorting<String> ss = new Sorting<>();
            Sorting<VectorString> svs = new Sorting<>();
            // Printing after sorting is left.
            int count = components.size();
            for (VectorString vector : components) {
                ss.Merge_sort(vector.vs, 0, vector.vs.size()-1);
            }

            svs.Merge_sort(components, 0, count-1);
            int l=0; 
            for (VectorString vector : components) {
                l = vector.vs.size();
                for (int i=0;i<l ;i++) {
                    System.out.print(vector.vs.get(i));
                    if (i<l-1) System.out.print(',');
                }
                System.out.print("\n");
            }

        }

    }

    public HashMap <String, Vector<Pair> > adj = new HashMap<>();
    public HashMap<String,Integer> visited = new HashMap<>();

    public void DFS(String s, Vector<String> v){
        v.add(s);
        this.visited.put(s, 1);
        Vector <Pair> adjedges = adj.get(s);
        for (Pair pair : adjedges) {
            if (visited.get(pair.s)==0) DFS(pair.s, v);
        }                
    }

}

class VectorString implements Comparable<VectorString>{

    public Vector<String> vs; 

    public VectorString(Vector<String> vs){
        this.vs =vs;
    }

    public int compareTo(VectorString v){
        if (this.vs.size()!= v.vs.size()) return this.vs.size()- v.vs.size();
        else{
            int n = v.vs.size();
            for (int i = 0; i < n; i++) {
                if (! this.vs.get(i).equals(v.vs.get(i)) ){
                    return this.vs.get(i).compareTo(v.vs.get(i));
                }
            }
        }
        return 0;
    }
}

class Pair implements Comparable<Pair> {
    public String s;
    public int x;
    
    public Pair( String s, int x){
        this.s = s;
        this.x = x;
    }
    public int compareTo(Pair p) {
        if ( this.x != p.x) return this.x - p.x;
        else return this.s.compareTo(p.s);
    }
}


class Sorting<T extends Comparable<T>>{

    public void Merge_sort(Vector<T> v, int low, int high) {
        if (low>=high) return;
        int mid = (low + high)/2;
        Merge_sort(v, low, mid);
        Merge_sort(v, mid+1, high);
        Merge(v, low, high);
        
    }

    public void Merge(Vector<T> v, int low, int high) {
        int mid = (low+high)/2;
        int s1 = mid - low + 1;
        int s2 = high - mid;
        Vector<T> L = new Vector<>();
        Vector<T> R = new Vector<>();

        for (int i = 0; i < s1; i++) {
            L.add(v.get(low + i));
        }
        for (int i = 0; i < s2; i++) {
            R.add(v.get(mid + 1 + i));
        }
        int i = 0;
        int j = 0;
        int k = low;
        while (i < s1 && j < s2) {
            if (R.get(j).compareTo(L.get(i)) > 0) {
                v.set(k++, R.get(j++));
            } else {
                v.set(k++, L.get(i++));
            }
        }
        while (i < s1) {
            v.set(k++, L.get(i++));
        }
        while (j < s2) {
            v.set(k++, R.get(j++));
        }
    }
}