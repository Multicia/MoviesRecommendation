import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Vector;

public class MysqlReader {
	//LDA
	public Dictionary localDict_user= new Dictionary();  // user dictionary
	public Dictionary localDict_movie= new Dictionary(); // movie dictionary
	Vector<Integer> user_ids = new Vector<Integer>();  //problem
	Vector<Integer> movie_ids= new Vector<Integer>();
	public int M= 0; // number of users
	public int V= 0; 
    String url="jdbc:mysql://localhost:3306/userlog";//JDBC的协议、主机名、端口号、需要连接的数据库
	String Mysql_user="root";	    //登录MySQL数据库的用户名
	String Mysql_passwd="1994";		//登录密码
	String sql= null;
	Connection con= null;
	Statement stat= null;
	String[] sqlStore= { "SELECT * FROM collect_before_group",// limit 0, 10000  limit 0, 600000   12.8114
			              "SELECT * FROM play_before_group"   //861003009000021000000606b71562e4 
	};
	//String[] sqlStore= { "SELECT * FROM collect_new where deviceid<='861003009000021000000606b71562e4'", //     12.8114
    //                     "SELECT * FROM play_new where deviceid<='861003009000021000000606b71562e4'"
    //};
	//ResultSet[] rs; 
	/**/
	public static void main(String[] args) {
		try {
			MysqlReader mr= new MysqlReader();
			mr.buildDict();

			//System.out.println("Users Dictionary built:  " + mr.M + " users");
			//System.out.println("Movies Dictionary built:  " + mr.V + " movies");
		    System.out.println("Successful");
		} catch (Exception e) {
			System.out.println("Error");
		}
	}
	public void showDict(MysqlReader mr) {
		Iterator<String> it = mr.localDict_user.word2id.keySet().iterator();
		while (it.hasNext()){
			String key = it.next();
			Integer value = mr.localDict_user.word2id.get(key);
			System.out.println("key: "+ key + "  "+"value: " + value);
		}
	}
	public void buildDict() {
		try {
			stat= con.createStatement();
			for (int i= 0; i< sqlStore.length; ++i) {				
				ResultSet rs= stat.executeQuery(sqlStore[i]);			//执行SELECT语句
				//按顺序读取结果集中的每一条记录
				while(rs.next()){
					String user= rs.getString("deviceid");     //read user
					String movie= rs.getString("title");
					String[] words= movie.split(",");
					int user_id= localDict_user.word2id.size();				
					if (localDict_user.contains(user))
						user_id = localDict_user.getID(user);      //if not new, then update user_id
					else {                                         //if new, the add it to dictionary 
					    localDict_user.addWord(user);
					    user_ids.add(user_id);
					}
					
					for (String word : words) {
						int movie_id= localDict_movie.word2id.size();   //assign _id based on temporary size
						if (localDict_movie.contains(word))
							movie_id = localDict_movie.getID(word);      //if not new, then update _id
						else {
							localDict_movie.addWord(word);
							movie_ids.add(movie_id);
						}
					}
				}
			}
			M= user_ids.size();
			V= movie_ids.size();  // number of total movies
			System.out.println("Users Dictionary building successed:  " + M + " users");
			System.out.println("Movies Dictionary building successed:  " + V + " movies");
		} catch (SQLException e) {
			System.out.println("连接数据库服务器失败");
		}
	}
	public int buildUsersDict() {
		try {
			stat= con.createStatement();			//创建Statement对象
			for (int i= 0; i< sqlStore.length; ++i) {				
				ResultSet rs= stat.executeQuery(sqlStore[i]);			//执行SELECT语句
				//按顺序读取结果集中的每一条记录
				while(rs.next()){
					String user= rs.getString("deviceid");     //read user
					int user_id= localDict_user.word2id.size();

					if (localDict_user.contains(user))
						user_id = localDict_user.getID(user);      //if not new, then update user_id
					else {                                         //if new, the add it to dictionary 
					    localDict_user.addWord(user);
					    user_ids.add(user_id);
					}				
				}
			}
			M= user_ids.size();
			System.out.println("Users Dictionary built:  " + M + " users");
/*
			//关闭ResultSet对象
			if(rs!=null){
				rs.close();
				rs=null;
			}	
			//关闭Statement对象
			if(stat!=null){
				stat.close();
				stat=null;
			}			
			//关闭Connection对象
			if(con!=null){
				con.close();
				con=null;
			}
*/
		} catch (SQLException e) {
			System.out.println("连接数据库服务器失败");
		}
		return M;
	}
	public int testUsersDict() {
		try {
			Statement stat=con.createStatement();			//创建Statement对象
			//for (int i= 0; i< sqlStore.length; ++i) {				
				ResultSet rs=stat.executeQuery(sqlStore[1]);			//执行SELECT语句
				//按顺序读取结果集中的每一条记录
				while(rs.next()){
					String user= rs.getString("deviceid");     //read user
					int user_id= localDict_user.word2id.size();

					if (localDict_user.contains(user))
						user_id = localDict_user.getID(user);      //if not new, then update user_id
					else {                                         //if new, the add it to dictionary 
					    localDict_user.addWord(user);
					    user_ids.add(user_id);
					}				
				}					
				//关闭ResultSet对象		
			//}
			M= user_ids.size();
		} catch (SQLException e) {
			System.out.println("连接数据库服务器失败");
		}
		return M;
	}
	public void testMysql() {
		try {
			Statement stat= con.createStatement();			//创建Statement对象
			ResultSet rs= stat.executeQuery(sqlStore[1]);			//执行SELECT语句
			int len= 0;
			rs.last();
			len =rs.getRow();
			System.out.println("users: " + len);
			rs.beforeFirst();
			//按顺序读取结果集中的每一条记录
			while(rs.next()){
				//读取每个字段的值
				String user_id= rs.getString("deviceid");
				String movies= rs.getString("title");
				String[] words = movies.split(",");
				System.out.println(user_id+":  "+ words.length + "  " + movies);
				//System.out.println("users: " + len);
			}
			System.out.println("users: " + len);

			//关闭ResultSet对象
			if(rs!=null){
				rs.close();
				rs=null;
			}
			//关闭Statement对象
			if(stat!=null){
				stat.close();
				stat=null;
			}
			//关闭Connection对象
			if(con!=null){
				con.close();
				con=null;
			}
		}catch (SQLException e) {
			System.out.print("sql错误");
		}
	}
	public MysqlReader() {

		try {
			Class.forName("com.mysql.jdbc.Driver");			//指定MySQL驱动
		    con = DriverManager.getConnection(url, Mysql_user, Mysql_passwd);//建立到MySQL的连接
			System.out.println("连接数据库服务器成功");
		} catch (ClassNotFoundException e) {
			System.out.println("没有找到MySQL驱动");
		} catch (SQLException e) {
			System.out.println("连接数据库服务器失败");
		}
	}
}