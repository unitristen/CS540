import java.util.*;

class State implements Comparable<State> {
	int[] board;
	State parentPt;
	int depth;

	public State(int[] arr) {
		this.board = Arrays.copyOf(arr, arr.length);
		this.parentPt = null;
		this.depth = 0;
	}

	public State[] getSuccessors() {
		State[] successors = new State[4];
		// TO DO: get all four successors and return them in sorted order
		for(int i = 0; i < 4; i++){
			successors[i] = new State(this.board);
		}
		// find the blank space
		int blank = 0;
		for(int i = 0; i < 9; i++){
			if(this.board[i] == 0){
				blank = i;
			}
		}
		switch (blank) {
			case 0:
				successors[0].board[0] = successors[0].board[1];
				successors[0].board[1] = 0;
				successors[1].board[0] = successors[1].board[3];
				successors[1].board[3] = 0;
				successors[2].board[0] = successors[2].board[2];
				successors[2].board[2] = 0;
				successors[3].board[0] = successors[3].board[6];
				successors[3].board[6] = 0;
				break;
			case 1:
				successors[0].board[1] = successors[0].board[0];
				successors[0].board[0] = 0;
				successors[1].board[1] = successors[1].board[2];
				successors[1].board[2] = 0;
				successors[2].board[1] = successors[2].board[4];
				successors[2].board[4] = 0;
				successors[3].board[1] = successors[3].board[7];
				successors[3].board[7] = 0;
				break;
			case 2:
				successors[0].board[2] = successors[0].board[1];
				successors[0].board[1] = 0;
				successors[1].board[2] = successors[1].board[0];
				successors[1].board[0] = 0;
				successors[2].board[2] = successors[2].board[5];
				successors[2].board[5] = 0;
				successors[3].board[2] = successors[3].board[8];
				successors[3].board[8] = 0;
				break;
			case 3:
				successors[0].board[3] = successors[0].board[0];
				successors[0].board[0] = 0;
				successors[1].board[3] = successors[1].board[4];
				successors[1].board[4] = 0;
				successors[2].board[3] = successors[2].board[5];
				successors[2].board[5] = 0;
				successors[3].board[3] = successors[3].board[6];
				successors[3].board[6] = 0;
				break;
			case 4:
				successors[0].board[4] = successors[0].board[1];
				successors[0].board[1] = 0;
				successors[1].board[4] = successors[1].board[5];
				successors[1].board[5] = 0;
				successors[2].board[4] = successors[2].board[3];
				successors[2].board[3] = 0;
				successors[3].board[4] = successors[3].board[7];
				successors[3].board[7] = 0;
				break;
			case 5:
				successors[0].board[5] = successors[0].board[4];
				successors[0].board[4] = 0;
				successors[1].board[5] = successors[1].board[2];
				successors[1].board[2] = 0;
				successors[2].board[5] = successors[2].board[3];
				successors[2].board[3] = 0;
				successors[3].board[5] = successors[3].board[8];
				successors[3].board[8] = 0;
				break;
			case 6:
				successors[0].board[6] = successors[0].board[0];
				successors[0].board[0] = 0;
				successors[1].board[6] = successors[1].board[3];
				successors[1].board[3] = 0;
				successors[2].board[6] = successors[2].board[7];
				successors[2].board[7] = 0;
				successors[3].board[6] = successors[3].board[8];
				successors[3].board[8] = 0;
				break;
			case 7:
				successors[0].board[7] = successors[0].board[1];
				successors[0].board[1] = 0;
				successors[1].board[7] = successors[1].board[4];
				successors[1].board[4] = 0;
				successors[2].board[7] = successors[2].board[6];
				successors[2].board[6] = 0;
				successors[3].board[7] = successors[3].board[8];
				successors[3].board[8] = 0;
				break;
			case 8:
				successors[0].board[8] = successors[0].board[2];
				successors[0].board[2] = 0;
				successors[1].board[8] = successors[1].board[5];
				successors[1].board[5] = 0;
				successors[2].board[8] = successors[2].board[6];
				successors[2].board[6] = 0;
				successors[3].board[8] = successors[3].board[7];
				successors[3].board[7] = 0;
				break;
		}
		Arrays.sort(successors);
		return successors;
	}

	public int compareTo(State s) {
		return getBoard().compareTo(s.getBoard());
	}

	public void printState(int option) {

		// TO DO: print a torus State based on option (flag)
		if(option != 3){
			System.out.println(this.board[0] + " "+ this.board[1] + " " + this.board[2] + " " +
					   this.board[3] + " " + this.board[4] + " " + this.board[5] + " " +
					   this.board[6] + " " + this.board[7] + " " + this.board[8]);
		}else if(option == 3){
			for(int i = 0; i < 9; i++){
				System.out.print(this.board[i] + " ");
			}
			System.out.print("parent ");
			if(this.parentPt == null){
				System.out.println("0 0 0 0 0 0 0 0 0");
			}else{
				for(int i = 0; i < 9; i++){
					System.out.print(this.parentPt.board[i] + " ");
				}
				System.out.println();
			}
		}
	}

	public String getBoard() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 9; i++) {
			builder.append(this.board[i]).append(" ");
		}
		return builder.toString().trim();
	}

	public boolean isGoalState() {
		for (int i = 0; i < 9; i++) {
			if (this.board[i] != (i + 1) % 9)
				return false;
		}
		return true;
	}

	public boolean equals(State src) {
		for (int i = 0; i < 9; i++) {
			if (this.board[i] != src.board[i])
				return false;
		}
		return true;
	}
}

public class Torus {

	public static void main(String args[]) {
//		long time1 = System.nanoTime();
		if (args.length < 10) {
			System.out.println("Invalid Input");
			return;
		}
		int flag = Integer.valueOf(args[0]);
		int[] board = new int[9];
		for (int i = 0; i < 9; i++) {
			board[i] = Integer.valueOf(args[i + 1]);
		}
		int option = flag / 100;
		int cutoff = flag % 100;
		if (option == 1) {
			State init = new State(board);
			State[] successors = init.getSuccessors();
			for (State successor : successors) {
				successor.printState(option);
			}
		} else {
			State init = new State(board);
			Stack<State> stack = new Stack<>();
			List<State> prefix = new ArrayList<>();
			int goalChecked = 0;
			int maxStackSize = Integer.MIN_VALUE;
			boolean found_goal = false;
			// needed for Part E
			while (!found_goal) {
				stack.push(init);
				prefix.clear();
				boolean escape = false;
				while (!stack.isEmpty()) {
					//TO DO: perform iterative deepening; implement prefix list
					State s = stack.pop();
					//clear the prefix list
					int parentIndex = prefix.indexOf(s.parentPt);
					prefix.subList(parentIndex + 1, prefix.size()).clear();
					prefix.add(s);
					if(option == 4 && s.depth == cutoff && !escape){
						escape = true;
						for(State state : prefix){
							state.printState(option);
						}
					}else if(option < 4){
						s.printState(option);
					}
					goalChecked++;
					if(s.isGoalState()){
						found_goal = true;
						break;
					}

					if(s.depth < cutoff){
						State[] successors = s.getSuccessors();
						for(int i = 0; i < 4; i++){
							boolean existsInPrefix = false;
							for(int j = 0; j < prefix.size(); j++){
								if(successors[i].equals(prefix.get(j))){
									existsInPrefix = true;
								}
							}
							if(!existsInPrefix){
								successors[i].depth = s.depth + 1;
								successors[i].parentPt = s;
								stack.push(successors[i]);
							}
						}
					}

					if(maxStackSize < stack.size()){
						maxStackSize = stack.size();
					}
				}
				if (option != 5){
					found_goal = true;
				}else{
					cutoff++;
				}
			}
			if(option == 5){
				for(State state : prefix){
					state.printState(option);
				}
				System.out.println("Goal-check " + goalChecked);
				System.out.println("Max-stack-size " + maxStackSize);
			}
		}
		// timing testing to make sure worst case executed in time
//		long time2 = System.nanoTime();
//		System.out.println("time elapsed " + (time2 - time1));
	}
}
