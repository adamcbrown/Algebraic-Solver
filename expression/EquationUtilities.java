package io.github.adamcbrown1997.algebraicSolver.expression;

public class EquationUtilities {
	public static int countedCharacters(String string, char character){
		int count=0;
		for(int i=0;i<string.length();i++){
			if(string.charAt(i)==character){
				count++;
			}
		}
		return count;
	}
	
	public static final char[] OPERATIONS={'+', '-', '*', '/', '^'};

	public static boolean isLetter(char charAt) {
		return ('a'<=charAt&&charAt<='z');
	}
	
	public static boolean isPriorityOp(char op1, char op2){
		int op1Index=0, op2Index=0;
		for(int i=0;i<OPERATIONS.length;i++){
			if(OPERATIONS[i]==op1){
				op1Index=i;
			}
			if(OPERATIONS[i]==op2){
				op2Index=i;
			}
		}
		
		if(op1Index-op2Index>=0){
			return true;
		}else if(op1Index-op2Index<-1){
			return false;
		}else{//This means op1 is one less then op2
			return op1Index==0||op1Index==2;
		}
	}
	
	public static boolean isOperation(char c){
		for(int i=0;i<OPERATIONS.length;i++){
			if(OPERATIONS[i]==c){
				return true;
			}
		}
		return false;
	}
	
}
