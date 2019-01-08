import java.util.*;
import java.io.*;
import java.text.DecimalFormat;

public class ice {
	private static String filename = "/userspace/h/hallock2/school/cs540/hw9/ice/data.txt";

	private static LinkedHashMap<Double, Integer> readData(){
		LinkedHashMap<Double, Integer>  years = new LinkedHashMap<Double, Integer>();
		try{
            File f = new File(filename);
            Scanner sc = new Scanner(f);
            while(sc.hasNext()){
                if(sc.hasNext()){
                    String word1 = sc.next();
                    String word2 = sc.next();
                    years.put(Double.parseDouble(word1), Integer.parseInt(word2));
                }
                else{
                    sc.next();
                }
            }
        }
        catch(FileNotFoundException ex){
            System.out.println("File Not Found.");
        }
        return years;
	}
	private static double mean(ArrayList<Integer> years){
		int n = years.size();
		double sum = 0.0;
		for(int i : years){
			sum = sum + i;
		}
		return sum/(double)n;
	}

	private static double stdev(ArrayList<Integer> ice){
		double mean = mean(ice);
		double sum = 0.0;
		for(int day : ice){
			double difference_squared = ((double)day - mean) * ((double)day - mean);
			sum = sum + difference_squared;
		}
		double to_be_rooted = sum * (1.0/(ice.size() - 1));
		return Math.sqrt(to_be_rooted);
	}

	private static double mse(double beta0, double beta1, LinkedHashMap<Double, Integer> data){
		Set<Double> years = data.keySet();
		double sum = 0.0;
		for(double year : years){
			double term = (beta0 + beta1 * year - data.get(year)) * (beta0 + beta1 * year - data.get(year));
			sum = sum + term;
		}
		return sum/(double)data.size();
	}

	private static double dmsedbeta0(double beta0, double beta1, LinkedHashMap<Double, Integer> data){
		Set<Double> years = data.keySet();
		double sum = 0.0;
		for(double year : years){
			double term = (beta0 + beta1 * year - data.get(year));
			sum = sum + term;
		}
		return sum * 2/(double)(data.size());
	}

	private static double dmsedbeta1(double beta0, double beta1, LinkedHashMap<Double, Integer> data){
		Set<Double> years = data.keySet();
		double sum = 0.0;
		for(double year : years){
			double term = (beta0 + beta1 * year - data.get(year)) * (year);
			sum = sum + term;
		}
		return sum * 2/(double)(data.size());
	}

	private static double[] betat(int t, double eta, LinkedHashMap<Double, Integer> data){
		double[] betat = new double[2];
		if(t == 0){
			betat[0] = betat[1] = 0;
		}else{
			betat[0] = betat(t-1, eta, data)[0] - eta * dmsedbeta0(betat(t-1, eta, data)[0], betat(t-1, eta, data)[1], data);
			betat[1] = betat(t-1, eta, data)[1] - eta * dmsedbeta1(betat(t-1, eta, data)[0], betat(t-1, eta, data)[1], data);
		}

		return betat;
	}

	public static LinkedHashMap<Double, Integer> normalizeData(LinkedHashMap<Double, Integer> data) {

		double xbar = meanYear(data);
        double sum = 0.0;
        ArrayList<Double> years = new ArrayList<Double>(data.keySet());
        for(double year : years) {
                double term = (year - xbar) * (year - xbar);
                sum = sum + term;
        }
        double stdx = sum/(data.size() - 1);
        stdx = Math.sqrt(stdx);

        LinkedHashMap<Double, Integer> normal_data = new LinkedHashMap<Double, Integer>();

        for(double year : years) {
                int y = data.get(year);
                double x = (year - xbar)/stdx;
                normal_data.put(x, y);
        }

        return normal_data;
	}

	private static double[] betahat(LinkedHashMap<Double, Integer> data){
		double xbar = meanYear(data);
		ArrayList<Integer> iceDays = new ArrayList<Integer>(data.values());
		double ybar = mean(iceDays);

		double num_sum = 0.0;
		double denom_sum = 0.0;
		Set<Double> years = data.keySet();
		for(double year : years){
			double numerator_term = (year - xbar) * (data.get(year) - ybar);
			double denominator_term = (year - xbar) * (year - xbar);

			num_sum = num_sum + numerator_term;
			denom_sum = denom_sum + denominator_term;
		}
		double betahat1 = num_sum / denom_sum;
		double betahat0 = ybar - betahat1 * xbar;

		double[] betahats = {betahat0, betahat1};
		return betahats;
	}

	private static double meanYear(LinkedHashMap<Double, Integer> data){
		Set<Double> years = data.keySet();
		double sum = 0;
		for(double year : years){
			sum = sum + year;
		}
		return sum/data.size();
	}
	private static double[] normalBetat(double beta0, double beta1, LinkedHashMap<Double, Integer> normal_data){
		double sum0 = 0.0;
		double sum1 = 0.0;
		ArrayList<Double> years = new ArrayList<Double>(normal_data.keySet());
		for(double i : years){
			double term0 = beta0 + (beta1 * i) - normal_data.get(i);
			double term1 = term0 * i;
			sum0 = sum0 + term0;
			sum1 = sum1 + term1;
		}
		double normalBeta0 = 2*sum0/normal_data.size();
		double normalBeta1 = 2 * sum1 / normal_data.size();
		double[] normalBetas = {normalBeta0, normalBeta1};
		return normalBetas;
	}

	private static double[] sgd_betat(double beta0, double beta1, LinkedHashMap<Double, Integer> data){
		int random1 = new Random().nextInt(162);
		int random0 = new Random().nextInt(162);
		ArrayList<Double> years = new ArrayList<Double>(data.keySet());
		double x1 = 0.0;
		double x0 = 0.0;
		for(int i = 0; i < years.size(); i++){
			if (i == random1){
				x1 = years.get(i);
			}
			if ( i == random0){
				x0 = years.get(i);
			}
		}
		int y1 = data.get(x1);
		int y0 = data.get(x0);

		double sgd_beta0 = 2*(beta0 + beta1 * x0 - y0);
		double sgd_beta1 = 2*(beta0 + beta1 * x1 - y1) * x1;
		double[] sgdbeta = {sgd_beta0, sgd_beta1};
		return sgdbeta;
	}
	static public void main(String[] args){
		LinkedHashMap<Double, Integer> data = readData();
		int flag = Integer.valueOf(args[0]);
		Set<Double> years = data.keySet();
		if(flag == 100){
			for(double i : years){
				System.out.println((int)i + " " + data.get(i));
			}
		}else if(flag == 200){
			ArrayList<Integer> iceDays = new ArrayList<Integer>(data.values());
			double mean = mean(iceDays);
			double stdev = stdev(iceDays);
			System.out.println(data.size());
			System.out.printf("%.2f\n", mean);
			System.out.printf("%.2f\n", stdev);

		}else if(flag == 300){
			double beta0 = Double.valueOf(args[1]);
			double beta1 = Double.valueOf(args[2]);
			double mse = mse(beta0, beta1, data);
			System.out.printf("%.2f\n", mse);
		}else if(flag == 400){
			double beta0 = Double.valueOf(args[1]);
			double beta1 = Double.valueOf(args[2]);
			double dmsdbeta0 = dmsedbeta0(beta0, beta1, data);
			double dmsdbeta1 = dmsedbeta1(beta0, beta1, data);
			System.out.printf("%.2f\n", dmsdbeta0);
			System.out.printf("%.2f\n", dmsdbeta1);
		}else if(flag == 500){
			double eta = Double.valueOf(args[1]);
			double T = Double.valueOf(args[2]);
			for(int t = 1; t <= T; t++){
				double[] betat = betat(t, eta, data);
				double mse_betat12 = mse(betat[0], betat[1], data);

				System.out.print(t + " ");
				System.out.printf("%.2f", betat[0]);
				System.out.print(" ");
				System.out.printf("%.2f", betat[1]);
				System.out.print(" ");
				System.out.printf("%.2f", mse_betat12);
				System.out.println();
			}
		}else if(flag == 600){
			double[] betahats = betahat(data);
			double mse = mse(betahats[0], betahats[1], data);
			System.out.printf("%.2f ", betahats[0]);
			System.out.printf("%.2f ", betahats[1]);
			System.out.printf("%.2f\n", mse);
		}else if(flag == 700){
			double[] betahats = betahat(data);
			double accumulation = betahats[0] + (betahats[1] * Integer.parseInt(args[1]));
			System.out.printf("%.2f\n", accumulation);
		}else if(flag == 800){
			double eta = Double.valueOf(args[1]);
			double T = Double.valueOf(args[2]);

			LinkedHashMap<Double, Integer> normalized_data = normalizeData(data);
			double beta0 = 0.0;
			double beta1 = 0.0;
			for(int t = 1; t <= T; t++){
				double[] betat = normalBetat(beta0, beta1, normalized_data);
				beta0 = beta0 - eta *betat[0];
				beta1 = beta1 - eta*betat[1];
				double mse_betat12 = mse(beta0, beta1, normalized_data);

				System.out.print(t + " ");
				System.out.printf("%.2f", beta0);
				System.out.print(" ");
				System.out.printf("%.2f", beta1);
				System.out.print(" ");
				System.out.printf("%.2f", mse_betat12);
				System.out.println();
			}
		}else if (flag == 900){
			double eta = Double.valueOf(args[1]);
			double T = Double.valueOf(args[2]);
			LinkedHashMap<Double, Integer> normalized_data = normalizeData(data);
			double beta0 = 0.0;
			double beta1 = 0.0;
			for(int t = 1; t <= T; t++){
				double[] sgdbeta = sgd_betat(beta0, beta1, normalized_data);
				beta0 = beta0 - eta * sgdbeta[0];
				beta1 = beta1 - eta * sgdbeta[1];
				double mse_sgd_betat12 = mse(beta0, beta1, normalized_data);
				System.out.print(t + " ");
				System.out.printf("%.2f", beta0);
				System.out.print(" ");
				System.out.printf("%.2f", beta1);
				System.out.print(" ");
				System.out.printf("%.2f", mse_sgd_betat12);
				System.out.println();
			}
		}
	}
}
