import java.util.*;

public class successor {
    public static class JugState {
        int[] Capacity = new int[]{0,0,0};
        int[] Content = new int[]{0,0,0};
        public JugState(JugState copyFrom)
        {
            this.Capacity[0] = copyFrom.Capacity[0];
            this.Capacity[1] = copyFrom.Capacity[1];
            this.Capacity[2] = copyFrom.Capacity[2];
            this.Content[0] = copyFrom.Content[0];
            this.Content[1] = copyFrom.Content[1];
            this.Content[2] = copyFrom.Content[2];
        }
        public JugState()
        {
        }
        public JugState(int A,int B, int C)
        {
            this.Capacity[0] = A;
            this.Capacity[1] = B;
            this.Capacity[2] = C;
        }
        public JugState(int A,int B, int C, int a, int b, int c)
        {
            this.Capacity[0] = A;
            this.Capacity[1] = B;
            this.Capacity[2] = C;
            this.Content[0] = a;
            this.Content[1] = b;
            this.Content[2] = c;
        }

        public void printContent()
        {
            System.out.println(this.Content[0] + " " + this.Content[1] + " " + this.Content[2]);
        }

        public ArrayList<JugState> getNextStates(){
            ArrayList<JugState> successors = new ArrayList<>();

            // TODO add all successors to the list
            // Fill one of the jugs
            for(int i = 0; i < 3; i++){
            	JugState successor = new JugState(this.Capacity[0], this.Capacity[1], this.Capacity[2],
            									  this.Content[0], this.Content[1], this.Content[2]);
            	successor.Content[i] = this.Capacity[i];
            	if(isValidState(successor, successors) && !myEquals(successor, this)){
            		successors.add(successor);
            	}

            }

            // Empty one of the jugs
            for(int i = 0; i < 3; i++){
            	JugState successor = new JugState(this.Capacity[0], this.Capacity[1], this.Capacity[2],
						  						  this.Content[0], this.Content[1], this.Content[2]);
            	successor.Content[i] = 0;
            	if(isValidState(successor, successors) && !myEquals(successor, this)){
            		successors.add(successor);
            	}
            }

            // A -> B, A -> C
            for (int i = 1; i < 3; i++){
            	JugState successor = new JugState(this.Capacity[0], this.Capacity[1], this.Capacity[2],
						  this.Content[0], this.Content[1], this.Content[2]);
            	// if the destination is too small to take all of A's contents
            	if(this.Content[0] + this.Content[i] > this.Capacity[i]){
            		// fill up as much of the destination as possible
            		successor.Content[i] = this.Capacity[i];
            		successor.Content[0] = this.Content[0] - (this.Capacity[i] - this.Content[i]);
            	}
            	//else if we can empty all of A into the destination, do so
            	else{
            		successor.Content[0] = 0;
            		successor.Content[i] = this.Content[0] + this.Content[i];
            	}
            	if(isValidState(successor, successors) && !myEquals(successor, this)){
            		successors.add(successor);
            	}
            }

            // B -> A
            JugState BtoA = new JugState(this.Capacity[0], this.Capacity[1], this.Capacity[2],
					  this.Content[0], this.Content[1], this.Content[2]);
            // if the destination is too small to take all of B's contents
        	if(this.Content[1] + this.Content[0] > this.Capacity[0]){
        		// fill up as much of the destination as possible
        		BtoA.Content[0] = this.Capacity[0];
        		BtoA.Content[1] = this.Content[1] - (this.Capacity[0] - this.Content[0]);
        	}
        	//else if we can empty all of B into the destination, do so
        	else{
        		BtoA.Content[1] = 0;
        		BtoA.Content[0] = this.Content[0] + this.Content[1];
        	}
        	if(isValidState(BtoA, successors) && !myEquals(BtoA, this)){
        		successors.add(BtoA);
        	}
            // B -> C
        	JugState BtoC = new JugState(this.Capacity[0], this.Capacity[1], this.Capacity[2],
					  this.Content[0], this.Content[1], this.Content[2]);
        	// if the destination is too small to take all of B's contents
        	if(this.Content[1] + this.Content[2] > this.Capacity[2]){
        		// fill up as much of the destination as possible
        		BtoC.Content[2] = this.Capacity[2];
        		BtoC.Content[1] = this.Content[1] - (this.Capacity[2] - this.Content[2]);
        	}
        	//else if we can empty all of B into the destination, do so
        	else{
        		BtoC.Content[1] = 0;
        		BtoC.Content[2] = this.Content[2] + this.Content[1];
        	}
        	if(isValidState(BtoC, successors) && !myEquals(BtoC, this)){
        		successors.add(BtoC);
        	}


            // C -> A, C -> B
            for (int i = 1; i >= 0; i--){
            	JugState successor = new JugState(this.Capacity[0], this.Capacity[1], this.Capacity[2],
						  this.Content[0], this.Content[1], this.Content[2]);
            	// if the destination is too small to take all of C's contents
            	if(this.Content[2] + this.Content[i] > this.Capacity[i]){
            		// fill up as much of the destination as possible
            		successor.Content[i] = this.Capacity[i];
            		successor.Content[2] = this.Content[2] - (this.Capacity[i] - this.Content[i]);
            	}
            	//else if we can empty all of C into the destination, do so
            	else{
            		successor.Content[2] = 0;
            		successor.Content[i] = this.Content[2] + this.Content[i];
            	}
            	if(isValidState(successor, successors) && !myEquals(successor, this)){
            		successors.add(successor);
            	}
            }
            return successors;
        }
    }
    public static boolean myEquals(JugState state, JugState newState){
    	if((state.Capacity[0] == newState.Capacity[0]) &&
    		(state.Capacity[1] == newState.Capacity[1]) &&
    		(state.Capacity[2] == newState.Capacity[2]) &&
    		(state.Content[0] == newState.Content[0]) &&
    		(state.Content[1] == newState.Content[1]) &&
    		(state.Content[2] == newState.Content[2])){
    		return true;
    	}else{
    		return false;
    	}
    }

    public static boolean isValidState(JugState state, ArrayList<JugState> list){
    	for(int i = 0; i < list.size(); i++){
    		if(myEquals(list.get(i), state)){
    			return false;
    		}

    	}
    	return true;
    }
    public static void main(String[] args) {
        if( args.length != 6 )
        {
            System.out.println("Usage: java successor [A] [B] [C] [a] [b] [c]");
            return;
        }

        // parse command line arguments
        JugState a = new JugState();
        a.Capacity[0] = Integer.parseInt(args[0]);
        a.Capacity[1] = Integer.parseInt(args[1]);
        a.Capacity[2] = Integer.parseInt(args[2]);
        a.Content[0] = Integer.parseInt(args[3]);
        a.Content[1] = Integer.parseInt(args[4]);
        a.Content[2] = Integer.parseInt(args[5]);

        // Implement this function
        ArrayList<JugState> asist = a.getNextStates();

        // Print out generated successors
        for(int i=0;i< asist.size(); i++)
        {
            asist.get(i).printContent();
        }

        return;
    }
}

