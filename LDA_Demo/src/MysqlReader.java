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
    String url="jdbc:mysql://localhost:3306/userlog";//JDBC��Э�顢���������˿ںš���Ҫ���ӵ����ݿ�
	String Mysql_user="root";	    //��¼MySQL���ݿ���û���
	String Mysql_passwd="1994";		//��¼����
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
				ResultSet rs= stat.executeQuery(sqlStore[i]);			//ִ��SELECT���
				//��˳���ȡ������е�ÿһ����¼
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
			System.out.println("�������ݿ������ʧ��");
		}
	}
	public int buildUsersDict() {
		try {
			stat= con.createStatement();			//����Statement����
			for (int i= 0; i< sqlStore.length; ++i) {				
				ResultSet rs= stat.executeQuery(sqlStore[i]);			//ִ��SELECT���
				//��˳���ȡ������е�ÿһ����¼
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
			//�ر�ResultSet����
			if(rs!=null){
				rs.close();
				rs=null;
			}	
			//�ر�Statement����
			if(stat!=null){
				stat.close();
				stat=null;
			}			
			//�ر�Connection����
			if(con!=null){
				con.close();
				con=null;
			}
*/
		} catch (SQLException e) {
			System.out.println("�������ݿ������ʧ��");
		}
		return M;
	}
	public int testUsersDict() {
		try {
			Statement stat=con.createStatement();			//����Statement����
			//for (int i= 0; i< sqlStore.length; ++i) {				
				ResultSet rs=stat.executeQuery(sqlStore[1]);			//ִ��SELECT���
				//��˳���ȡ������е�ÿһ����¼
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
				//�ر�ResultSet����		
			//}
			M= user_ids.size();
		} catch (SQLException e) {
			System.out.println("�������ݿ������ʧ��");
		}
		return M;
	}
	public void testMysql() {
		try {
			Statement stat= con.createStatement();			//����Statement����
			ResultSet rs= stat.executeQuery(sqlStore[1]);			//ִ��SELECT���
			int len= 0;
			rs.last();
			len =rs.getRow();
			System.out.println("users: " + len);
			rs.beforeFirst();
			//��˳���ȡ������е�ÿһ����¼
			while(rs.next()){
				//��ȡÿ���ֶε�ֵ
				String user_id= rs.getString("deviceid");
				String movies= rs.getString("title");
				String[] words = movies.split(",");
				System.out.println(user_id+":  "+ words.length + "  " + movies);
				//System.out.println("users: " + len);
			}
			System.out.println("users: " + len);

			//�ر�ResultSet����
			if(rs!=null){
				rs.close();
				rs=null;
			}
			//�ر�Statement����
			if(stat!=null){
				stat.close();
				stat=null;
			}
			//�ر�Connection����
			if(con!=null){
				con.close();
				con=null;
			}
		}catch (SQLException e) {
			System.out.print("sql����");
		}
	}
	public MysqlReader() {

		try {
			Class.forName("com.mysql.jdbc.Driver");			//ָ��MySQL����
		    con = DriverManager.getConnection(url, Mysql_user, Mysql_passwd);//������MySQL������
			System.out.println("�������ݿ�������ɹ�");
		} catch (ClassNotFoundException e) {
			System.out.println("û���ҵ�MySQL����");
		} catch (SQLException e) {
			System.out.println("�������ݿ������ʧ��");
		}
	}
}