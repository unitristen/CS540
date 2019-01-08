import java.util.*;
import java.io.*;
import java.text.DecimalFormat;

public class Neural {
	private static class info{
		double midterm;
		double hw;
		double pass;
		public info(double hw, double midterm, double pass){
			this.midterm = midterm;
			this.hw = hw;
			this.pass = pass;
		}
	}

	private static double[] fetchWeights(String[] args){
		double[] weights = new double[9];
		weights[0] = Double.parseDouble(args[1]);
		for (int i = 0; i < 9; i++){
			weights[i] = Double.parseDouble(args[i+1]);
		}
		return weights;
	}

	private static double dvdu(double u){
		if(u >= 0) return 1;
		return 0.0;
	}
	private static double[] computeUVs(double[] weights, double x1, double x2){
		double ua = weights[0] + weights[1]*x1 + weights[2]*x2;
		double ub = weights[3] + weights[4]*x1 + weights[5]*x2;

		double va = Math.max(0, ua);
		double vb = Math.max(0, ub);

		double uc = weights[6] + weights[7]*va + weights[8]*vb;
		double vc = 1/(1 + Math.exp(-uc));

		double[] return_array = {ua, va, ub, vb, uc, vc};
		return return_array;
	}


	static private double[] partialDerivativesC(double vc, double y){
		double derrordvC = vc - y;
		double derrorduC = derrordvC * vc * (1 - vc);
		double[] return_array = {derrordvC, derrorduC};
		return return_array;
	}

	static private double[] partialDerivativesAB(double derrorduC, double[] weights, double x1, double x2){
		double derrordvA = derrorduC * weights[7];
		double derrordvB = derrorduC * weights[8];

		double u1 = weights[0] + weights[1] * x1 + weights[2] * x2;
		double u2 = weights[3] + weights[4] * x1 + weights[5] * x2;

		double derrorduA = derrordvA * dvdu(u1);
		double derrorduB = derrordvB * dvdu(u2);

		double[] return_array = {derrordvA, derrorduA, derrordvB, derrorduB};
		return return_array;
	}

	static private double[] partialDerivativeWeights(double[] derrordu, double x1, double x2, double va, double vb){
		double[] derrordW = {
				derrordu[0],
				x1 * derrordu[0],
				x2 * derrordu[0],

				derrordu[1],
				x1 * derrordu[1],
				x2 * derrordu[1],

				derrordu[2],
				va * derrordu[2],
				vb * derrordu[2]
		};
		return derrordW;
	}

	static private void updateWeights(double[] weights, double eta, double[] partialDerivativeWeights){
		for(int i = 0; i < weights.length; i++){
			weights[i] = weights[i] - eta * partialDerivativeWeights[i];
		}
	}

	static ArrayList<info> read(String file){
		ArrayList<info> info = new ArrayList<info>();
//		System.out.println(file);
		try{
		    File f = new File(file);
		    Scanner sc = new Scanner(f);
		    while(sc.hasNext()){
		    	String line = sc.nextLine();
		        //System.out.println(line);
		        double hw = Double.parseDouble(line.split(" ")[0]);
		        double  midterm = Double.parseDouble(line.split(" ")[1]);
		        double pass = Double.parseDouble(line.split(" ")[2]);

		        info student = new info(hw, midterm, pass);
		        info.add(student);

		    }
		    sc.close();
		}
		catch(FileNotFoundException ex){
		    System.out.println("File Not Found.");
		}
		return info;

	}

	public static double evaluationSetError(ArrayList<info> students, double[] weights){
		double sum = 0;
		double x1, x2;
		for(int i = 0; i <students.size(); i++){
			x1 = students.get(i).hw;
			x2 = students.get(i).midterm;
			double y = students.get(i).pass;
			double[] UVs = computeUVs(weights, x1, x2);
			sum = sum + (0.5 * (UVs[5] - y) * (UVs[5] - y));
		}
		return sum;
	}

	private static double sumWeights(int flag, info student, double[] weights, double eta, ArrayList<info> eval, ArrayList<info> test){
		double x1 = student.hw;
		double x2 = student.midterm;
		double y = student.pass;
		ArrayList<info> students;
		if(flag == 700){
			//students = read("/userspace/h/hallock2/school/cs540/hw10/hw2_midterm_A_eval.txt");
			students = eval;
		}else{
			//students = read("/userspace/h/hallock2/school/cs540/hw10/hw2_midterm_A_test.txt");
			students = test;
		}
		double[] UVs = computeUVs(weights, x1, x2);

		double derrorduC = partialDerivativesC(UVs[5], y)[1];

		double[] partialDerivativesAB = partialDerivativesAB(derrorduC, weights, x1, x2);
		double[] derrordu = {partialDerivativesAB[1], partialDerivativesAB[3], derrorduC};
		double[] partialDerivativeWeights = partialDerivativeWeights(derrordu, x1, x2, UVs[1], UVs[3]);

		double error = 0.5 * (UVs[5] - y) * (UVs[5] - y);
		//System.out.printf("%.5f\n", error);
		updateWeights(weights, eta, partialDerivativeWeights);

		return evaluationSetError(students, weights);

	}
	static public void main(String[] args){
		double[] weights;
		double[]  v = new double[3];
		double[] dErrordu = new double[3];
		double[] dErrordv = new double[3];
		double[] dErrordWeights = new double[9];


		int flag = Integer.valueOf(args[0]);
		if (flag == 100){
			weights = fetchWeights(args);

			double x1 = Double.parseDouble(args[args.length-2]);
			double x2 = Double.parseDouble(args[args.length-1]);

			double[] UVs = computeUVs(weights, x1, x2);

			System.out.printf("%.5f %.5f %.5f %.5f %.5f %.5f\n", UVs[0], UVs[1], UVs[2], UVs[3], UVs[4], UVs[5]);
		}else if(flag == 200){
			weights = fetchWeights(args);

			double x1 = Double.parseDouble(args[args.length-3]);
			double x2 = Double.parseDouble(args[args.length-2]);
			double y = Double.parseDouble(args[args.length-1]);

			double[] UVs = computeUVs(weights, x1, x2);

			double error = 0.5 * (UVs[5] - y) * (UVs[5] - y);
			double derrordvc = (UVs[5] - y);
			double derrorduc = (UVs[5] - y) * (UVs[5]) * (1 - UVs[5]);

			System.out.printf("%.5f %.5f %.5f\n", error, derrordvc, derrorduc);
		}else if (flag == 300){
			weights = fetchWeights(args);

			double x1 = Double.parseDouble(args[args.length-3]);
			double x2 = Double.parseDouble(args[args.length-2]);
			double y = Double.parseDouble(args[args.length-1]);

			double vc = computeUVs(weights, x1, x2)[5];

			double derrorduC = partialDerivativesC(vc, y)[1];

			double[] partialDerivativesAB = partialDerivativesAB(derrorduC, weights, x1, x2);

			System.out.printf("%.5f %.5f %.5f %.5f\n", partialDerivativesAB[0], partialDerivativesAB[1], partialDerivativesAB[2], partialDerivativesAB[3]);

		}else if (flag == 400){
			weights = fetchWeights(args);

			double x1 = Double.parseDouble(args[args.length-3]);
			double x2 = Double.parseDouble(args[args.length-2]);
			double y = Double.parseDouble(args[args.length-1]);

			double[] UVs = computeUVs(weights, x1, x2);

			double derrorduC = partialDerivativesC(UVs[5], y)[1];

			double[] partialDerivativesAB = partialDerivativesAB(derrorduC, weights, x1, x2);
			double[] derrordu = {partialDerivativesAB[1], partialDerivativesAB[3], derrorduC};
			double[] partialDerivativeWeights = partialDerivativeWeights(derrordu, x1, x2, UVs[1], UVs[3]);

			for(int i = 0; i < partialDerivativeWeights.length; i++){
				System.out.printf("%.5f ", partialDerivativeWeights[i]);
			}
			System.out.println();
		}else if(flag == 500){
			weights = fetchWeights(args);
			for(int i = 0; i < weights.length; i++){
				System.out.printf("%.5f ", weights[i]);
			}
			System.out.println();
			double eta = Double.parseDouble(args[args.length -1]);

			double x1 = Double.parseDouble(args[args.length-4]);
			double x2 = Double.parseDouble(args[args.length-3]);
			double y = Double.parseDouble(args[args.length-2]);

			double[] UVs = computeUVs(weights, x1, x2);

			double derrorduC = partialDerivativesC(UVs[5], y)[1];

			double[] partialDerivativesAB = partialDerivativesAB(derrorduC, weights, x1, x2);
			double[] derrordu = {partialDerivativesAB[1], partialDerivativesAB[3], derrorduC};
			double[] partialDerivativeWeights = partialDerivativeWeights(derrordu, x1, x2, UVs[1], UVs[3]);

			double error = 0.5 * (UVs[5] - y) * (UVs[5] - y);
			System.out.printf("%.5f\n", error);
			updateWeights(weights, eta, partialDerivativeWeights);
			for(int i = 0; i < weights.length; i++){
				System.out.printf("%.5f ", weights[i]);
			}
			System.out.println();
			UVs = computeUVs(weights, x1, x2);
			error = 0.5 * (UVs[5] - y) * (UVs[5] - y);
			System.out.printf("%.5f\n", error);
		}else if (flag == 600){
			weights = fetchWeights(args);
			double eta = Double.parseDouble(args[args.length-1]);
			ArrayList<info> info = read("./hw2_midterm_A_train.txt");
			ArrayList<info> students = read(".hw2_midterm_A_eval.txt");
			for(int i = 0; i < info.size(); i++){
				double x1 = info.get(i).hw;
				double x2 = info.get(i).midterm;
				double y = info.get(i).pass;

				double[] UVs = computeUVs(weights, x1, x2);

				double derrorduC = partialDerivativesC(UVs[5], y)[1];

				double[] partialDerivativesAB = partialDerivativesAB(derrorduC, weights, x1, x2);
				double[] derrordu = {partialDerivativesAB[1], partialDerivativesAB[3], derrorduC};
				double[] partialDerivativeWeights = partialDerivativeWeights(derrordu, x1, x2, UVs[1], UVs[3]);

				updateWeights(weights, eta, partialDerivativeWeights);

				System.out.printf("%.5f %.5f %.5f\n", x1, x2, y);
				for(int j = 0; j < weights.length; j++){
					System.out.printf("%.5f ", weights[j]);
				}
				System.out.printf("\n%.5f\n", evaluationSetError(students, weights));

			}
		}else if(flag == 700){
			double sum = 0;
			weights = fetchWeights(args);
			double T = Double.parseDouble(args[args.length-1]);
			double eta = Double.parseDouble(args[args.length-2]);
			ArrayList<info> info = read("./hw2_midterm_A_train.txt");
			ArrayList<info> eval = read("./hw2_midterm_A_eval.txt");
			for(double t = 0; t < T; t++){
				for(int i = 0; i < info.size(); i++){
					sum = sumWeights(flag, info.get(i), weights, eta, eval, null);
				}
				for(int j = 0; j < weights.length; j++){
					System.out.printf("%.5f ", weights[j]);
				}
				System.out.printf("\n%.5f\n", sum);
			}
		}else if(flag == 800){
			weights = fetchWeights(args);
			double T = Double.parseDouble(args[args.length-1]);
			double eta = Double.parseDouble(args[args.length-2]);

			ArrayList<info> info = read("./hw2_midterm_A_train.txt");
			ArrayList<info> test = read(".hw2_midterm_A_test.txt");
			ArrayList<info> eval = read("./hw2_midterm_A_eval.txt");
			double sum = 0;
			double previousSum = Double.MAX_VALUE;
			double t;
			for(t = 0; t < T; t++){
//				System.out.println("Epoch is: " + t);
				for(int i = 0; i < info.size(); i++){
					sum = sumWeights(700, info.get(i), weights, eta, eval, test);
//					System.out.println("Sum is: " + sum);
				}
				if(previousSum < sum){
					break;
				}else{
					previousSum = sum;
				}
			}
			System.out.println(t + 1);
			for(int j = 0; j < weights.length; j++){
				System.out.printf("%.5f ", weights[j]);
			}
			double testSum = 0;
			System.out.printf("\n%.5f\n", sum);
			for(info student : test){
				double x1 = student.hw;
				double x2 = student.midterm;
				double y = student.pass;
				double prediction;
				if(computeUVs(weights, x1, x2)[5] >= 0.5){
					prediction = 1;
				}else{
					prediction = 0;
				}
				if(y == prediction){
					testSum++;
				}
			}
			System.out.println(String.format("%.5f", testSum/test.size()));
		}
	}
}
