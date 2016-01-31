import java.io.File;

public class Estimator {
	
	// output model:total
	protected Model trnModel;
	LDAOption option;
	int table;
	//int table= option.Table;//
	// single model
	protected Model[] trnModelSingle;
	//LDAOption[] optionSingle= new LDAOption[2];
	
	public boolean init(LDAOption option) {
		boolean begin= true; 
		this.option= option;
		table= option.Table;
		trnModel= new Model();	   // setDefaultValues
		trnModelSingle= new Model[table];
		begin= begin && trnModel.initTotalModel(option); //init total
		for(int i= 0; i< table; ++i) {
			trnModelSingle[i]= new Model();
			option.cTable= i;
			begin= begin && trnModelSingle[i].initNewModel(option); //init single.
		}
		if (option.est){           // true		
			if (!begin)   //initNewModel(option) == ture
				return false;
			trnModel.data.localDict_user.writeWordMap(option.dir + File.separator + option.userMapFileName, false);
			trnModel.data.localDict_movie.writeWordMap(option.dir + File.separator + option.wordMapFileName, false);//false:cover
		}
		else if (option.estc){    // false
			if (!trnModel.initEstimatedModel(option))
				return false;
		}		
		return true;
	}
	
	public void estimate() {
		
		System.out.println("Sampling " + trnModel.niters + " iteration!");
		int lastIter = trnModel.liter;
		for (trnModel.liter = lastIter + 1; trnModel.liter < option.niters + lastIter; ++trnModel.liter){
			//System.out.println("Iteration " + trnModel.liter + " ...");
			// for all z_i
			for(int i= 0; i< table; ++i) { //here
				for (int m = 0; m < trnModel.M; ++m) {   //M number of documents/users				
					for (int n = 0; n < trnModelSingle[i].data.docs[m].length; ++n){
						int topic = sampling(trnModelSingle[i], m, n);
						trnModelSingle[i].z[m].set(n, topic);  //assign topic for a word/movie
					}// end for each word/movie
				}// end for each document/user
			}
			//computePhi();
			if (option.savestep > 0) {
				if (trnModel.liter % option.savestep == 0){
					System.out.println("Saving the model at iteration " + trnModel.liter + " ...");
					for(int i= 0; i< table; ++i) {
						computeTheta(trnModelSingle[i]);				
					}
					computePhi();

					//saveResults();
					//trnModel.saveModel("model-" + LDAUtils.zeroPad(trnModel.liter, 5));
				}
			}
			
		}// end iterations		
		
		System.out.println("Gibbs sampling completed!\n");
		System.out.println("Saving the final model!\n");
		
		computeTheta();
		computePhi();
		//--trnModel.liter;
		saveFinalResults();
	}
	/**
	 * Do sampling
	 * @param model Model
	 * @param m document number
	 * @param n word number
	 * @return topic id
	 */
	public int sampling(Model model, int m, int n) {
		// remove z_i from the count variable
		int topic = model.z[m].get(n);
		int w = model.data.docs[m].words[n];
		
		model.nw[w][topic] -= 1;
		model.nd[m][topic] -= 1;
		model.nwsum[topic] -= 1;
		model.ndsum[m] -= 1;
		
		double Vbeta = trnModel.V * trnModel.beta;
		double Kalpha = trnModel.K * trnModel.alpha;
		
		//do multinominal sampling via cumulative method
		for (int k = 0; k < trnModel.K; ++k) {
			for (int i= 0; i< table; ++i) {
				trnModel.nw[w][k] += trnModelSingle[i].nw[w][k];
				trnModel.nwsum[k] += trnModelSingle[i].nwsum[k];
			}
			model.p[k] = (model.nd[m][k] + trnModel.alpha)/(model.ndsum[m] + Kalpha) *
				    (trnModel.nw[w][k] + trnModel.beta)/(trnModel.nwsum[k] + Vbeta);
			trnModel.nw[w][k]= 0; //clear
			trnModel.nwsum[k]= 0; //clear
		}
		
		// cumulate multinomial parameters
		for (int k = 1; k < trnModel.K; ++k) {
			model.p[k] += model.p[k - 1];
		}
		
		// scaled sample because of unnormalized p[]
		double u = Math.random() * model.p[model.K - 1];
		
		for (topic = 0; topic < trnModel.K; ++topic) { 
			if (model.p[topic] > u) //sample topic w.r.t distribution p
			break;
		}
		if(topic==20) topic= 19;
		// add newly estimated z_i to count variables
		model.nw[w][topic] += 1;
		model.nd[m][topic] += 1;
		model.nwsum[topic] += 1;
		model.ndsum[m] += 1;
 		return topic;
	}
	/**
	 * Do sampling
	 * @param m document number
	 * @param n word number
	 * @return topic id
	 */
	public int sampling(int m, int n) {
		// remove z_i from the count variable
		int topic = trnModel.z[m].get(n);
		int w = trnModel.data.docs[m].words[n];
		
		trnModel.nw[w][topic] -= 1;
		trnModel.nd[m][topic] -= 1;
		trnModel.nwsum[topic] -= 1;
		trnModel.ndsum[m] -= 1;
		
		double Vbeta = trnModel.V * trnModel.beta;
		double Kalpha = trnModel.K * trnModel.alpha;
		
		//do multinominal sampling via cumulative method
		for (int k = 0; k < trnModel.K; k++){
			trnModel.p[k] = (trnModel.nw[w][k] + trnModel.beta)/(trnModel.nwsum[k] + Vbeta) *
					(trnModel.nd[m][k] + trnModel.alpha)/(trnModel.ndsum[m] + Kalpha);
		}
		
		// cumulate multinomial parameters
		for (int k = 1; k < trnModel.K; k++){
			trnModel.p[k] += trnModel.p[k - 1];
		}
		
		// scaled sample because of unnormalized p[]
		double u = Math.random() * trnModel.p[trnModel.K - 1];
		
		for (topic = 0; topic < trnModel.K; topic++){
			if (trnModel.p[topic] > u) //sample topic w.r.t distribution p
			break;
		}
		
		// add newly estimated z_i to count variables
		trnModel.nw[w][topic] += 1;
		trnModel.nd[m][topic] += 1;
		trnModel.nwsum[topic] += 1;
		trnModel.ndsum[m] += 1;
		
 		return topic;
	}

	public void computeTheta(Model model) {
		double Kalpha = trnModel.K * trnModel.alpha;
		for (int m = 0; m < trnModel.M; m++){
			for (int k = 0; k < trnModel.K; k++){
				model.theta[m][k] = (model.nd[m][k] + trnModel.alpha) / (model.ndsum[m] + Kalpha);
			}
		}
	}
	public void computeTheta() {
		for (int m = 0; m < trnModel.M; m++){
			for (int k = 0; k < trnModel.K; k++){
				trnModel.theta[m][k] = (trnModel.nd[m][k] + trnModel.alpha) / (trnModel.ndsum[m] + trnModel.K * trnModel.alpha);
			}
		}
	}
	
	public void computePhi(){
		double Vbeta = trnModel.V * trnModel.beta;
		for (int k = 0; k < trnModel.K; ++k) {
			for (int w = 0; w < trnModel.V; ++w) {
				for (int i= 0; i< table; ++i) {
					trnModel.nw[w][k] += trnModelSingle[i].nw[w][k];
					trnModel.nwsum[k] += trnModelSingle[i].nwsum[k];
				}
				trnModel.phi[k][w] = (trnModel.nw[w][k] + trnModel.beta) / (trnModel.nwsum[k] + Vbeta);
				trnModel.nw[w][k]= 0; //clear
				trnModel.nwsum[k]= 0; //clear
			}
		}
	}
	public void saveResults() {
		trnModel.saveModelPhi(option.dir + File.separator + "model-" + LDAUtils.zeroPad(trnModel.liter, 5) + trnModel.phiSuffix);
		trnModel.saveModelTwords(option.dir + File.separator + "model-" + LDAUtils.zeroPad(trnModel.liter, 5) + trnModel.twordsSuffix);
		for(int i= 0; i< table; ++i) {
			trnModelSingle[i].saveModel("model-" + i + "-" + LDAUtils.zeroPad(trnModel.liter, 5));
		}
	}
	public void saveFinalResults() {
		trnModel.saveModelPhi(option.dir + File.separator + "model-final" + trnModel.phiSuffix);
		trnModel.saveModelTwords(option.dir + File.separator + "model-final" + trnModel.twordsSuffix);
		//trnModel.saveCate("model-final");
		for(int i= 0; i< table; ++i) {
			trnModelSingle[i].saveModel("model-" + i + "-final");
		}
	}
}
