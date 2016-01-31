
public class TempTest {
	
	public static void main(String[] args) {
		//TempTest tt= new TempTest();
		//tt.testAddAdd();
		String a= "sad  sdaa";
		a= a.replace(" ", "");
		System.out.println(a);
	}
	public void testHashMap() {
		Dictionary dic= new Dictionary();
		
	}
	public void testAddAdd() {
		int i=0;
		for(i= 0; i< 20; ++i) {
			System.out.println("i=" + i);
			if(i == 20 ) {
				System.out.println("here");
				break;
			}
			i=19;
		}
		System.out.println("i=" + i);
	}
	public void testArray() {
		int M= 3; int i= 0;
		String[] str= new String[M];
		for(i= 0; i< M; ++i) {
			str[i]= new String("well");
			System.out.println(str[i]);
		}

	}
	public void testString() {
		String[] str= {"zero", "one", "two"};
		System.out.println(str.length);
		for(int i= 0; i< str.length; ++i){
			System.out.println(i + ": " + str[i]);
		}		
	}

}
