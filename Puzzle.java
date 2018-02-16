import java.util.*;
import java.io.*;

class Puzzle implements Runnable{
	static HashMap<String,Node> graph=new HashMap<>();
	static int[] arr;
	static class MinHeap{
		Node[] arr;
		int capacity;
		int size;
		MinHeap(){
			capacity=362880;
			size=0;
			arr=new Node[capacity];
		}
		boolean isEmpty(){
			return size==0;
		}
		int left(int i){
			return (2*i+1);
		}
		int right(int i){
			return (2*i+2);
		}
		int parent(int i){
			return (i-1)/2;
		}
		void add(Node o){
			if(size==capacity) return;
			size++;
			int i=size-1;
			arr[i]=o;
			while(i!=0 && arr[parent(i)].distance>arr[i].distance){
				Node n=arr[i];
				arr[i]=arr[parent(i)];
				arr[parent(i)]=n;
			}
			while(i!=0 && arr[parent(i)].distance==arr[i].distance && arr[parent(i)].moves>arr[i].moves){
				Node n=arr[i];
				arr[i]=arr[parent(i)];
				arr[parent(i)]=n;
			}
		}
		Node poll(){
			if(size<=0) return null;
			if(size==1){
				size--;
				return arr[0];
			}
			Node root=arr[0];
			arr[0]=arr[size-1];
			size--;
			MinHeapify(0);
			return root;
		}
		void MinHeapify(int i){
			int l=left(i);
			int r=right(i);
			int smallest=i;
			if(l<size && (arr[l].distance<arr[i].distance || (arr[l].distance==arr[i].distance && arr[l].moves<arr[i].moves)))
				smallest=l;
			if(r<size && (arr[r].distance<arr[smallest].distance || (arr[r].distance==arr[smallest].distance && arr[r].moves<arr[smallest].moves)))
				smallest=r;
			if(smallest!=i){
				Node n=arr[i];
				arr[i]=arr[smallest];
				arr[smallest]=n;
				MinHeapify(smallest);			
			}
		}
	}	
	static class Node{
		String str;
		int i;		
		boolean visited;
		boolean processed;
		int distance;
		int moves;
		String path;
		ArrayList<Edge> edge=new ArrayList<>();
		Node(int i,String str){
			visited=false;
			this.str=str;
			this.i=i;
		}

		public int compareTo(Node other){
				if(distance==other.distance) return moves-other.moves;
			return distance-other.distance;
		}
	}

	static class Edge{
		String str;
		int weight;
		String type;
		Edge(String str,int w,String type){
			this.str=str;
			weight=w;
			this.type=type;
		}
	}
	static void createGraph(String str){
		int i;
		Node node=graph.get(str);
		if(node.visited) return;

		node.visited=true;
		char arr[]=str.toCharArray();
			i=node.i;
		if(i-3>=0){
			int val=(int)(arr[i-3]-'0');
			String b="";
			b+=arr[i-3]+"D ";

			char swap=arr[i];
			arr[i]=arr[i-3];
			arr[i-3]=swap;
			String str1=new String(arr);
			node.edge.add(new Edge(str1,val,b));
			arr[i-3]=arr[i];
			arr[i]=swap;
			if(!graph.containsKey(str1))
				graph.put(str1,new Node(i-3,str1));
			if(!graph.get(str1).visited)
				createGraph(str1);
		}
		//left
		if(i-1>=0){
			int val=(int)(arr[i-1]-'0');
			String b="";
			b+=arr[i-1]+"R ";
			char swap=arr[i];
			arr[i]=arr[i-1];
			arr[i-1]=swap;
			String str1=new String(arr);
			node.edge.add(new Edge(str1,val,b));
			arr[i-1]=arr[i];
			arr[i]=swap;
			if(!graph.containsKey(str1))
				graph.put(str1,new Node(i-1,str1));
			if(!graph.get(str1).visited)
				createGraph(str1);
		}
		//right
		if(i+1<=8){
			int val=(int)(arr[i+1]-'0');
			String b="";
			b+=arr[i+1]+"L ";
			char swap=arr[i];
			arr[i]=arr[i+1];
			arr[i+1]=swap;
			String str1=new String(arr);
			node.edge.add(new Edge(str1,val,b));
			arr[i+1]=arr[i];
			arr[i]=swap;
			if(!graph.containsKey(str1))
				graph.put(str1,new Node(i+1,str1));
			if(!graph.get(str1).visited)
				createGraph(str1);
		}
		//down
		if(i+3<=8){
			int val=(int)(arr[i+3]-'0');
			String b="";
			b+=arr[i+3]+"U ";
			char swap=arr[i];
			arr[i]=arr[i+3];
			arr[i+3]=swap;
			String str1=new String(arr);
			node.edge.add(new Edge(str1,val,b));
			arr[i+3]=arr[i];
			arr[i]=swap;
			if(!graph.containsKey(str1))
				graph.put(str1,new Node(i+3,str1));
			if(!graph.get(str1).visited)
				createGraph(str1);
		}
	}

	static void print(){
	    for (String key : graph.keySet()) {
	        System.out.println("Key = " + key + " - " + graph.get(key).str+" "+graph.get(key).distance);
	    }

	}
	static void dijkstra(String src,String dest){
		for(String key:graph.keySet()){
			graph.get(key).processed=false;
			graph.get(key).distance=Integer.MAX_VALUE;
		}
		Comparator<Node> comp=new Comparator<Node>(){
			@Override
			public int compare(Node s,Node other){
					if(s.distance==other.distance) return s.moves-other.moves;
				return s.distance-other.distance;
			}
		};
		graph.get(src).distance=0;
		graph.get(src).moves=0;
		graph.get(src).path="";
		//PriorityQueue<Node> p=new PriorityQueue<>(362880,comp);
		MinHeap p=new MinHeap();
		p.add(graph.get(src));
		while(!p.isEmpty()){
			Node u=p.poll();
			if(u.str.equals(dest)) break;
			if(u.processed) continue;
			u.processed=true;
			for(Edge e:graph.get(u.str).edge){
				Node v=graph.get(e.str);
				int weight=arr[e.weight-1];
				if(u.distance+weight<v.distance){
					v.distance=u.distance+weight;
					v.moves=u.moves+1;
					v.path=u.path+e.type;	
					p.add(v);
				}
			}
		}
	}

	public static void main(String[] args) throws InterruptedException,FileNotFoundException{
		//try{
			long start=System.currentTimeMillis();
				Thread t = new Thread(null,new Puzzle(),"Puzzle",1<<28);
				t.start();
				t.join();
			
			arr=new int[8];
			Scanner sc=new Scanner(new File(args[0]));
			//File f=new File(args[1]);
			//BufferedReader in =new BufferedReader(new InputStreamReader(System.in));
			//PrintWriter out=new PrintWriter(args[1],"UTF-8");
			PrintWriter out=new PrintWriter(new File(args[1]));
			int b=sc.nextInt();
			while(b-->0){
				String s1=sc.next();
				String s2=sc.next();
				for(int i=0;i<8;i++)
					arr[i]=sc.nextInt();
				String ss1,ss2;
				char arr1[]=s1.toCharArray();
				char arr2[]=s2.toCharArray();
				for(int i=0;i<9;i++){
					if(arr1[i]=='G'){
						arr1[i]='0';
						break;
					}
				}
				for(int i=0;i<9;i++){
					if(arr2[i]=='G'){
						arr2[i]='0';
						break;
					}
				}
				ss1=new String(arr1);
				ss2=new String(arr2);
				dijkstra(ss1,ss2);
				int cost=graph.get(ss2).distance;
				int moves=graph.get(ss2).moves;
				String path=graph.get(ss2).path;
				if(cost==Integer.MAX_VALUE) {
					cost=-1;
					moves=-1;
					path="";
				}
				//System.out.println(moves+" "+cost);
				//System.out.println(path);
				out.println(moves+" "+cost);
				out.println(path);
				//out.close();
			}
			long end=System.currentTimeMillis();
			System.out.println(end-start);
			//System.out.println(graph.size());
			out.flush();
		//}
		/*catch(IOException e){

		}*/
	}
	@Override
	public void run(){
		String str="123468750";
		Node n=new Node(8,str);
		graph.put(str,n);
		createGraph(str);
		String str1="123468570";
		Node n1=new Node(8,str1);
		graph.put(str1,n1);
		createGraph(str1);
	}
}
