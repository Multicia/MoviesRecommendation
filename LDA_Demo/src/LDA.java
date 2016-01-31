import java.io.BufferedReader;
import java.io.IOException;

public class LDA implements Runnable {
    //LDA run function
	@Override
	public void run() {
		LDAOption option = new LDAOption();
		//option.dir = "D:\\LDA\\FromMysql";//Specify directory   ////
		option.dir = "E:\\Projects\\MovieData\\LDA\\FromMysql";
		//option.dfile = "mediaWords.txt";//Specify resource data filename    ////
		option.Table= 2;
		option.est = true; // Specify whether we want to estimate model from scratch
		//option.estc = true;
		option.inf = false;//Specify whether we want to do inference
		option.modelName = "model-final";//Specify the model level to which you want to applied. ///
		option.niters = 1000;//Specify the number of iterations  //
		option.K = 50;
		option.twords = 20;
		Estimator estimator = new Estimator();
		estimator.init(option);    //initialize option based on the settings above
		estimator.estimate();
	}

	public static void main(String[] args) {
		new LDA().run();
	}

}
