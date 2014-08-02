package io.github.adamcbrown1997.algebraicSolver.expression;

public class Expression {
	
	private enum ExpressionType{
		STRING, EXPRESSIONS, NUMBER, FUNCTION
	}
	
	private ExpressionType type;
	
	private String expression;
	
	private Expression e1;
	private char symbol;
	private Expression e2;
	
	private String function;
	
	private double number;
	
	public Expression(String expression){
		type=ExpressionType.STRING;
		this.expression=expression;
	}
	
	public Expression(Expression e1, char symbol, Expression e2){
		type=ExpressionType.EXPRESSIONS;
		this.e1=e1;
		this.symbol=symbol;
		this.e2=e2;
	}
	
	public Expression(String function, Expression e1){
		type=ExpressionType.FUNCTION;
		this.function=function;
		this.e1=e1;
	}
	
	public Expression(double number){
		type=ExpressionType.NUMBER;
		this.number=number;
	}
	
	public double solve() throws Exception{
		switch(type){
		case STRING:
			for(int i=0;i<expression.length();i++){
				if(expression.charAt(i)=='e'&& (i==0||!EquationUtilities.isLetter(expression.charAt(i-1))) && (i==expression.length()-1||!EquationUtilities.isLetter(expression.charAt(i+1)))){
					if(i==expression.length()-1){
						expression=expression.substring(0,i)+Math.E;
					}else{
						expression=expression.substring(0,i)+Math.E+expression.substring(i+1);
					}
				}

				if((expression.charAt(i)=='p'&&expression.charAt(i+1)=='i') && (i==0||!EquationUtilities.isLetter(expression.charAt(i-1))) && (i==expression.length()-2||!EquationUtilities.isLetter(expression.charAt(i+2)))){
					if(i==expression.length()-2){
						expression=expression.substring(0,i)+Math.PI;
					}else{
						expression=expression.substring(0,i)+Math.PI+expression.substring(i+2);
					}
				}
			}
			
			for(int i=0;i<expression.length();i++){
				if(expression.charAt(i)=='-'){
					int depth=0;
					int end=-1;
					for(int subI=i+1;subI<expression.length();subI++){
						char c=expression.charAt(subI);
						if(c=='('){
							depth++;
						}
						if(c==')'){
							depth--;
							if(depth==0){
								end=subI;
								break;
							}
						}
						if(((c<'0'|| c>'9')&&c!='.')&& depth==0){
							end=subI;
							break;
						}
					}
					
					if(end==-1){
						end=expression.length();
					}
					
					if(i==0){
						return new Expression("(0-"+expression.substring(1, end)+")"+ (end==expression.length()?"":expression.substring(end))).solve();
					}
					
					char before=expression.charAt(i-1);
					if(before=='('||EquationUtilities.isOperation(before)){
						return new Expression(expression.substring(0, i)+"(0-"+expression.substring(i+1, end)+")"+ (end==expression.length()?"":expression.substring(end))).solve();
					}
				}
			}
			
			if(EquationUtilities.countedCharacters(expression, '(')!=EquationUtilities.countedCharacters(expression, ')')){
				throw new Exception("Number of open parentheses does not equal the number of close parentheses");
			}
			try{
				return Double.parseDouble(expression);//First try to simply parse the number
			}catch(NumberFormatException e){
				if(expression.contains("(")){
					int depth=0;
					int start=0;
					for(int i=0;i<expression.length();i++){
						if(depth==0&&EquationUtilities.isLetter(expression.charAt(i))){
							int split=i-1;
							if(split==-1){
								int closeParen=-1;
								int subDepth=1;
								int openParen=expression.indexOf('(');
								
								for(int subI=openParen+1;subI<expression.length();subI++){
									if(expression.charAt(subI)=='('){
										subDepth++;
									}
									
									if(expression.charAt(subI)==')'){
										subDepth--;
										if(subDepth==0){
											closeParen=subI;
											break;
										}
									}
								}
								
								
								double funcValue = new Expression(expression.substring(0, openParen), new Expression(expression.substring(openParen,closeParen+1))).solve();
								if(closeParen==expression.length()-1){
									return funcValue;
								}else{
									return new Expression(new Expression(funcValue), expression.charAt(closeParen+1), new Expression(expression.substring(closeParen+2))).solve();
								}
							}else{
								return new Expression(new Expression(expression.substring(0, split)), expression.charAt(split), new Expression(expression.substring(split+1))).solve();
							}
						}
						
						if(expression.charAt(i)=='('){
							if(depth==0){
								start=i;
							}
							depth++;
						}
						
						if(expression.charAt(i)==')'){
							depth--;
							if(depth==0){
								if(start==0&&i==expression.length()-1){//Occurs in situations like (5+4), which should reduce to 5+4
									return new Expression(expression.substring(1, expression.length()-1)).solve();
								}else if(start==0){
									return new Expression(new Expression(expression.substring(0, i+1)), expression.charAt(i+1), new Expression(expression.substring(i+2))).solve();
								}else if(i==expression.length()-1){
									return new Expression(new Expression(expression.substring(0, start-1)), expression.charAt(start-1), new Expression(expression.substring(start))).solve();
								}
								
								if(EquationUtilities.isPriorityOp(expression.charAt(start-1), expression.charAt(i+1))){
									return new Expression(new Expression(expression.substring(0, i+1)), expression.charAt(i+1), new Expression(expression.substring(i+2))).solve();
								}else{
									return new Expression(new Expression(expression.substring(0, start-1)), expression.charAt(start-1), new Expression(expression.substring(start))).solve();
								}
							}
						}
					}
				}else{//If there are no parenthesis, then the order of operations should be followed
					for(char symbol : EquationUtilities.OPERATIONS){
						if(expression.contains(symbol+"")){
							int index=expression.indexOf(symbol);
							Expression e1=new Expression(expression.substring(0, index));
							Expression e2=new Expression(expression.substring(index+1));
							return new Expression(e1, symbol, e2).solve();
						}
					}
				}
			}
			break;
		case EXPRESSIONS:
			switch(symbol){
			case '+':
				return e1.solve()+e2.solve();
			case '-':
				return e1.solve()-e2.solve();
			case '*':
				return e1.solve()*e2.solve();
			case '/':
				return e1.solve()/e2.solve();
			case '^':
				return Math.pow(e1.solve(), e2.solve());
			}
			break;
		case FUNCTION:
			if(function.startsWith("log")){
				if(function.equalsIgnoreCase("log")){
					return Math.log10(e1.solve());
				}else{
					Expression base=new Expression(function.substring(3));
					return Math.log(e1.solve())/Math.log(base.solve());
				}
			}else if(function.equalsIgnoreCase("ln")){
				return Math.log(e1.solve());
			}
			break;
		case NUMBER:
			return number;
		default:
			throw new Exception("Unsupported use of a symbol or function");
		}
		throw new Exception("UH OH");
	}
}
