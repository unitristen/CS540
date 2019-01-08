
import java.util.*;
import java.io.*;
import java.text.DecimalFormat;

public class Chatbot{
    private static String filename = "/userspace/h/hallock2/school/cs540/hw7files/chatbot/src/WARC201709_wid.txt";

    // Interval class to represent intervals
    private static class Interval{
    	double l;
    	double r;
    	public Interval(double l, double r){
    		this.l = l;
    		this.r = r;
    	}
    }
    private static Hashtable<Integer, Interval> unigramDistribution(List<Integer> corpus){
    	Hashtable<Integer, Double> probabilities = new Hashtable<Integer, Double>();
//    	Hashtable<Integer, Double> individual_occurrences = new Hashtable<Integer, Double>();
    	for(int word : corpus){
    		double probability = 0.0;
    		if(!probabilities.containsKey(word)){
    			probability = 1/(double)corpus.size();
    		}else{
    			double count = (probabilities.get(word) * corpus.size()) + 1;
    			probability = count/(double)corpus.size();
    		}
    		probabilities.put(word, probability);
    	}

    	Hashtable<Integer, Interval> intervals = new Hashtable<Integer, Interval>();
    	for(int word : probabilities.keySet()){
    		double left = .0;
    		for(int i = 0; i < word; i++){
    			left = left + probabilities.get(i);
    		}

    		double right = left;
    		right = right + probabilities.get(word);

    		Interval newInterval = new Interval(left, right);
    		intervals.put(word, newInterval);
    	}
    	return intervals;
    }
    private static Hashtable<Integer, Interval> bigramDistribution(List<Integer> corpus, int h){

    	// all words that fall after h in the corpus, (same as option 300)
    	ArrayList<Integer> words_after_h = new ArrayList<Integer>();

    	for(int i = 0; i < corpus.size(); i++){
    		if(corpus.get(i) == h && i != corpus.size()-1){
    			words_after_h.add(corpus.get(i+1));
    		}
    	}
    	if(words_after_h.size() == 0){
    		// doesn't exist in corpus or it is the last word
    		return null;
    	}
        Hashtable<Integer, Double> probabilities = new Hashtable<Integer, Double>();
        for(int word : corpus){
        	double count;
        	if(!probabilities.containsKey(word)){
        		count = 0.0;
        		double probability = 0;
        		for(int i = 0; i < words_after_h.size(); i++){
        			if(word == words_after_h.get(i)){
        				count++;
        				probability  = count/words_after_h.size();
        			}
        		}
        		probabilities.put(word, probability);
        	}
        }
        Hashtable<Integer, Interval> intervals = new Hashtable<Integer, Interval>();
        for(int word : probabilities.keySet()){
        	double left, right;
        	left = .0;
        	for(int i = 0; i < word; i++){
        		left = left + probabilities.get(i);
        	}
        	right = left + probabilities.get(word);
        	if(left - right != 0){
        		Interval interval = new Interval(left, right);
        		intervals.put(word, interval);
        	}
        }
        return intervals;
    }

    private static Hashtable<Integer, Interval> trigramDistribution(List<Integer> corpus, int h1, int h2){
    	ArrayList<Integer> words_after_h1h2 = new ArrayList<Integer>();
    	// read through the corpus
    	for(int i = 0; i < corpus.size() - 2; i++){
    		if(corpus.get(i) == h1 && corpus.get(i+1) == h2){
    			words_after_h1h2.add(corpus.get(i+2));
    		}
    	}

    	if(words_after_h1h2.size() == 0){
    		return null;
    	}

    	Hashtable<Integer, Double> probabilities = new Hashtable<Integer, Double>();
    	for(int word : corpus){
    		if(!probabilities.containsKey(word)){
    			int count = 0;
    			for (int after : words_after_h1h2){
    				if(after == word){
    					count++;
    				}
    			}
    			probabilities.put(word, count/(double)words_after_h1h2.size());
    		}
    	}

        ArrayList<Integer> arrayOfProbabilities = new ArrayList<Integer>(probabilities.keySet());
        Hashtable<Integer, Interval> intervals = new Hashtable<Integer, Interval>();
        for(int word : arrayOfProbabilities){
        	double left, right;
        	left = .0;
        	for(int i = 0; i < word; i++){
        		left = left + probabilities.get(i);
        	}
        	right = left + probabilities.get(word);
        	if(left - right != 0){
        		Interval interval = new Interval(left, right);
        		intervals.put(word, interval);
        	}
        }
        return intervals;

    }


    private static ArrayList<Integer> readCorpus(){
        ArrayList<Integer> corpus = new ArrayList<Integer>();
        try{
            File f = new File(filename);
            Scanner sc = new Scanner(f);
            while(sc.hasNext()){
                if(sc.hasNextInt()){
                    int i = sc.nextInt();
                    corpus.add(i);
                }
                else{
                    sc.next();
                }
            }
        }
        catch(FileNotFoundException ex){
            System.out.println("File Not Found.");
        }
        return corpus;
    }
    static public void main(String[] args){
        ArrayList<Integer> corpus = readCorpus();
		int flag = Integer.valueOf(args[0]);

        if(flag == 100){
			int w = Integer.valueOf(args[1]);
            int count = 0;
            //TODO count occurrence of w
            count = Collections.frequency(corpus, w);

            System.out.println(count);
            System.out.println(String.format("%.7f",count/(double)corpus.size()));
        }
        else if(flag == 200){
            int n1 = Integer.valueOf(args[1]);
            int n2 = Integer.valueOf(args[2]);
            double random = (double)n1/(double)n2;

            int iteration = 0;
            double left = 0; double right = 0;
            //TODO generate

            Hashtable<Integer, Interval> intervals = unigramDistribution(corpus);
            Set<Integer> words = intervals.keySet();

            for(int word : words){
            	double interval_l = intervals.get(word).l;
            	double interval_r = intervals.get(word).r;
            	if(random >= interval_l && random <= interval_r){
            		left = interval_l;
            		right = interval_r;
            		iteration = word;
            	}
            }
            System.out.println(iteration);
            System.out.println(String.format("%.7f", left));
            System.out.println(String.format("%.7f", right));

        }
        else if(flag == 300){
            int h = Integer.valueOf(args[1]);
            int w = Integer.valueOf(args[2]);
            int count = 0;
            ArrayList<Integer> words_after_h = new ArrayList<Integer>();
            //TODO
            for(int i = 0; i < corpus.size(); i++){
        		if(corpus.get(i) == h && i != corpus.size()-1){
        			words_after_h.add(corpus.get(i+1));
        			if(corpus.get(i+1) == w){
        				count++;
        			}
        		}
        	}

            //output
            System.out.println(count);
            System.out.println(words_after_h.size());
            System.out.println(String.format("%.7f",count/(double)words_after_h.size()));
        }
        else if(flag == 400){
            int n1 = Integer.valueOf(args[1]);
            int n2 = Integer.valueOf(args[2]);
            int h = Integer.valueOf(args[3]);
            double random = n1/(double)n2;
            double left = 0; double right = 0;
            int iteration = 0;

            Hashtable<Integer, Interval> intervals = bigramDistribution(corpus, h);
            Set<Integer> words = intervals.keySet();

            for(int word : words){
            	double interval_l = intervals.get(word).l;
            	double interval_r = intervals.get(word).r;
            	if(random >= interval_l && random <= interval_r){
            		left = interval_l;
            		right = interval_r;
            		iteration = word;
            	}
            }
            System.out.println(iteration);
            System.out.println(String.format("%.7f", left));
            System.out.println(String.format("%.7f", right));
            //TODO

        }
        else if(flag == 500){
            int h1 = Integer.valueOf(args[1]);
            int h2 = Integer.valueOf(args[2]);
            int w = Integer.valueOf(args[3]);
            int count = 0;
            ArrayList<Integer> words_after_h1h2 = new ArrayList<Integer>();
            //TODO
            for(int i = 0; i < corpus.size() - 2; i++){
            	if(corpus.get(i) == h1 && corpus.get(i + 1) == h2){
            		words_after_h1h2.add(corpus.get(i + 2));
            		if(corpus.get(i+2) == w){
            			count++;
            		}
            	}
            }

            //output
            System.out.println(count);
            System.out.println(words_after_h1h2.size());
            if(words_after_h1h2.size() == 0)
                System.out.println("undefined");
            else
                System.out.println(String.format("%.7f",count/(double)words_after_h1h2.size()));
        }
        else if(flag == 600){
            int n1 = Integer.valueOf(args[1]);
            int n2 = Integer.valueOf(args[2]);
            int h1 = Integer.valueOf(args[3]);
            int h2 = Integer.valueOf(args[4]);
            //TODO
            double random = n1/(double)n2;
            int iteration = 0;
            double left = 0; double right = 0;
            Hashtable<Integer, Interval> intervals = trigramDistribution(corpus, h1, h2);
            if(intervals != null){
            	Set<Integer> words = intervals.keySet();
            	for(int word : words){
            		double interval_l = intervals.get(word).l;
                	double interval_r = intervals.get(word).r;
                    if(random >= interval_l && random <= interval_r){
                    	left = interval_l;
                    	right = interval_r;
                    	iteration = word;
                    }
            	}
            	System.out.println(iteration);
                System.out.println(String.format("%.7f", left));
                System.out.println(String.format("%.7f", right));
            }else{
            	System.out.println("undefined");
            }
        }
        else if(flag == 700){
            int seed = Integer.valueOf(args[1]);
            int t = Integer.valueOf(args[2]);
            int h1=0,h2=0;

            Random rng = new Random();
            if (seed != -1) rng.setSeed(seed);

            if(t == 0){
                // TODO Generate first word using r
                double r = rng.nextDouble();

                Hashtable<Integer, Interval> unigram = unigramDistribution(corpus);
                Set<Integer> uniwords = unigram.keySet();
                for(int word : uniwords){
                	double interval_l = unigram.get(word).l;
                	double interval_r = unigram.get(word).r;
                    if(r >= interval_l && r <= interval_r){
                    	h1 = word;
                    }
            	}
                System.out.println(h1);
                if(h1 == 9 || h1 == 10 || h1 == 12){
                    return;
                }

                // TODO Generate second word using r
                r = rng.nextDouble();

                Hashtable<Integer, Interval> bigram = bigramDistribution(corpus, h1);
                Set<Integer> biwords = bigram.keySet();
                for(int word : biwords){
                	double interval_l = bigram.get(word).l;
                	double interval_r = bigram.get(word).r;
                    if(r >= interval_l && r <= interval_r){
                    	h2 = word;
                    }
            	}
                System.out.println(h2);
            }
            else if(t == 1){
                h1 = Integer.valueOf(args[3]);
                // TODO Generate second word using r
                double r = rng.nextDouble();

                Hashtable<Integer, Interval> bigram = bigramDistribution(corpus, h1);
                Set<Integer> biwords = bigram.keySet();
                for(int word : biwords){
                	double interval_l = bigram.get(word).l;
                	double interval_r = bigram.get(word).r;
                    if(r >= interval_l && r <= interval_r){
                    	h2 = word;
                    }
            	}

                System.out.println(h2);
            }
            else if(t == 2){
                h1 = Integer.valueOf(args[3]);
                h2 = Integer.valueOf(args[4]);
            }

            while(h2 != 9 && h2 != 10 && h2 != 12){
                double r = rng.nextDouble();
                int w  = 0;
                // TODO Generate new word using h1,h2
                Hashtable<Integer, Interval> trigram = trigramDistribution(corpus, h1, h2);
                Set<Integer> triwords = trigram.keySet();

                for(int word : triwords){
                	double interval_l = trigram.get(word).l;
                	double interval_r = trigram.get(word).r;
                    if(r >= interval_l && r <= interval_r){
                    	w = word;
                    }
            	}
                System.out.println(w);
                h1 = h2;
                h2 = w;
            }
        }

        return;
    }
}
